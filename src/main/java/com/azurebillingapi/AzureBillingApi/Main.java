package com.azurebillingapi.AzureBillingApi;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import okhttp3.OkHttpClient;

public class Main {
	@FunctionName("billing")
	public HttpResponseMessage main(
			@HttpTrigger(name = "req", methods = { HttpMethod.GET,
					HttpMethod.POST }, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
			ExecutionContext context) throws IOException {
		context.getLogger().info("Java HTTP trigger processed a request.");

		String querySubscription = request.getQueryParameters().get("subscriptionId");
		String subscriptionId = request.getBody().orElse(querySubscription);

		String queryClientId = request.getQueryParameters().get("clientId");
		String clientId = request.getBody().orElse(queryClientId);

		String queryClientSecret = request.getQueryParameters().get("clientSecret");
		String clientSecret = request.getBody().orElse(queryClientSecret);

		String queryTenantId = request.getQueryParameters().get("tenantId");
		String tenantId = request.getBody().orElse(queryTenantId);

		if (StringUtils.isEmpty(subscriptionId) && StringUtils.isEmpty(clientId)
				&& StringUtils.isEmpty(clientSecret) && StringUtils.isEmpty(tenantId)) {
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
					.body("Please pass a subscriptionId,clinetId,clinetSecret,tenantId on the query string or in the request body")
					.build();
		}

		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.connectTimeout(180, TimeUnit.SECONDS)
				.writeTimeout(180, TimeUnit.SECONDS)
				.readTimeout(180, TimeUnit.SECONDS);

		ProxySetter proxySetter = new ProxySetter();
		proxySetter.proxySet(builder);

		OkHttpClient client = builder.build();

		context.getLogger().info("Start Azure Token Publish");
		String token = TokenPublisher.publishToken(client, clientId, clientSecret, tenantId);

		String useApiVersion = "2015-06-01-preview";
		String rateApiVersion = "2016-08-31-preview";
		String planId = "MS-AZR-0003P";

		context.getLogger().info("Start Resource Usage Aggregates API Call");
		Map<String, Double> usageMap = AzureRestCaller.callResourceUsageAggregates(
				client, token, subscriptionId, useApiVersion);

		context.getLogger().info("Start Rate Card API Call");
		Map<String, Double> rateCardMap = AzureRestCaller.callRateCard(
				client, token, subscriptionId, rateApiVersion, planId);

		context.getLogger().info("Start Rate Category API Call");
		Map<String, String> rateCategoryMap = AzureRestCaller.callRateCategory(
				client, token, subscriptionId, rateApiVersion, planId);

		context.getLogger().info("Start Resource Total Billing Map Create");
		Map<String, Integer> totalMap = TotalBillingCreator.createTotalBilling(
				usageMap, rateCardMap, rateCategoryMap);

		context.getLogger().info("Return Total Billing");
		int totalBilling = 0;
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Integer> entry : totalMap.entrySet()) {
			totalBilling = totalBilling + entry.getValue();
			sb.append(entry.getKey() + " => " + entry.getValue());
			sb.append(System.lineSeparator());
		}
		sb.insert(0, "今日時点のAzure利用料は " +
				totalBilling + " 円です" +
				System.lineSeparator() +
				System.lineSeparator());

		return request.createResponseBuilder(HttpStatus.OK).body(sb).build();
	}
}
