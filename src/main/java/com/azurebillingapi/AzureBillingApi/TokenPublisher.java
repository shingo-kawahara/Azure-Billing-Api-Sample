package com.azurebillingapi.AzureBillingApi;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class TokenPublisher {

	private TokenPublisher() {
	}

	static String publishToken(
			OkHttpClient client,
			String clientId,
			String clientSecret,
			String tenantId) throws IOException {
		String requestUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/token";
		String grantType = "client_credentials";
		String resourceUrl = "https://management.core.windows.net/";

		RequestBody formBody = new FormBody.Builder()
				.add("grant_type", grantType)
				.add("resource", resourceUrl)
				.add("client_id", clientId)
				.add("client_secret", clientSecret)
				.build();

		Request request = new Request.Builder()
				.url(requestUrl)
				.post(formBody)
				.build();

		Response response = client.newCall(request).execute();
		String jsonText = response.body().string();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(jsonText);

		return root.get("access_token").textValue();
	}
}
