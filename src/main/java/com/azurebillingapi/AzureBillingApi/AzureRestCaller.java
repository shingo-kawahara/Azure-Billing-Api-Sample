package com.azurebillingapi.AzureBillingApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;

class AzureRestCaller {

	private AzureRestCaller() {
	}

	static Map<String, Double> callResourceUsageAggregates(
			OkHttpClient client,
			String token,
			String subscriptionId,
			String useApiVersion) throws IOException {
		Map<String, Double> resourceUsageMap = new HashMap<>();

		ObjectMapper mapper = new ObjectMapper();
		String meterId;
		Double quantity;
		String jsonText;
		JsonNode root;
		String url = "https://management.azure.com/subscriptions/" + subscriptionId +
				"/providers/Microsoft.Commerce/UsageAggregates" + "?api-version=" + useApiVersion +
				"&reportedStartTime=" + DateGetter.getEndOfLastMonth() +
				"&reportedEndTime=" + DateGetter.getToday() + "&showDetails=f";

		do {
			jsonText = GeneralRestCaller.callAzureGeneralApi(client, url, token);
			root = mapper.readTree(jsonText);

			for (JsonNode node : root.get("value")) {
				meterId = node.get("properties").get("meterId").textValue();
				quantity = node.get("properties").get("quantity").doubleValue();

				if (resourceUsageMap.containsKey(meterId)) {
					Double quantitySum = resourceUsageMap.get(meterId) + quantity;
					resourceUsageMap.put(meterId, quantitySum);
				} else {
					resourceUsageMap.put(meterId, quantity);
				}
			}
			if(!root.has("nextLink")) {
				break;
			}
			url = root.get("nextLink").textValue();
		} while (true);

		return resourceUsageMap;
	}

	static Map<String, Double> callRateCard(
			OkHttpClient client,
			String token,
			String subscriptionId,
			String rateApiVersion,
			String planId) throws IOException {
		Map<String, Double> rateCardMap = new HashMap<>();

		ObjectMapper mapper = new ObjectMapper();
		String meterId;
		Double meterRates;
		String url = "https://management.azure.com/subscriptions/" + subscriptionId +
				"/providers/Microsoft.Commerce/RateCard" + "?api-version=" + rateApiVersion +
				"&%24filter=OfferDurableId+eq+'" + planId +
				"'+and+Currency+eq+'JPY'+and+Locale+eq+'ja-JP'+and+RegionInfo+eq+'JP'";

		String jsonText = GeneralRestCaller.callAzureGeneralApi(client, url, token);
		JsonNode root = mapper.readTree(jsonText);

		for (JsonNode node : root.get("Meters")) {
			meterId = node.get("MeterId").textValue();
			meterRates = node.get("MeterRates").get("0").doubleValue();
			rateCardMap.put(meterId, meterRates);
		}
		return rateCardMap;
	}

	static Map<String, String> callRateCategory(
			OkHttpClient client,
			String token,
			String subscriptionId,
			String rateApiVersion,
			String planId) throws IOException {
		Map<String, String> categoryMap = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		String meterId;
		String meterCategory;
		String url = "https://management.azure.com/subscriptions/" + subscriptionId +
				"/providers/Microsoft.Commerce/RateCard" + "?api-version=" + rateApiVersion +
				"&%24filter=OfferDurableId+eq+'" + planId +
				"'+and+Currency+eq+'JPY'+and+Locale+eq+'ja-JP'+and+RegionInfo+eq+'JP'";

		String jsonText = GeneralRestCaller.callAzureGeneralApi(client, url, token);
		JsonNode root = mapper.readTree(jsonText);

		for (JsonNode node : root.get("Meters")) {
			meterId = node.get("MeterId").textValue();
			meterCategory = node.get("MeterCategory").textValue();
			categoryMap.put(meterId, meterCategory);
		}
		return categoryMap;
	}
}
