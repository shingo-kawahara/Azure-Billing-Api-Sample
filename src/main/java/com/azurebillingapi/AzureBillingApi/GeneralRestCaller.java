package com.azurebillingapi.AzureBillingApi;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class GeneralRestCaller {

	private GeneralRestCaller() {
	}

	static String callAzureGeneralApi(
			OkHttpClient client,
			String url,
			String token) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.header("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + token)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}
}
