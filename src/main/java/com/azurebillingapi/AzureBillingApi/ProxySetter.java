package com.azurebillingapi.AzureBillingApi;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.commons.lang3.StringUtils;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient.Builder;

class ProxySetter {
	void proxySet(Builder builder) {
		String proxyHost = System.getenv("proxyHost");
		String proxyPort = System.getenv("proxyPort");
		String proxyUser = System.getenv("proxyUser");
		String proxyPass = System.getenv("proxyPass");

		if (StringUtils.isEmpty(proxyHost)
				|| StringUtils.isEmpty(proxyPort)
				|| StringUtils.isEmpty(proxyUser)
				|| StringUtils.isEmpty(proxyPass)) {
			return;
		}
		int proxyPortInt = Integer.parseInt(proxyPort);

		Authenticator proxyAuthenticator = (route, response) -> {
			String credential = Credentials.basic(proxyUser, proxyPass);
			return response.request().newBuilder()
					.header("Proxy-Authorization", credential)
					.build();
		};
		builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPortInt)));
		builder.proxyAuthenticator(proxyAuthenticator);
	}
}
