package com.azurebillingapi.AzureBillingApi;

import java.util.Map;
import java.util.TreeMap;

class TotalBillingCreator {
	static Map<String, Integer> createTotalBilling(
			Map<String, Double> usageMap,
			Map<String, Double> rateCardMap,
			Map<String, String> rateCategoryMap) {
		Map<String, Integer> totalMap = new TreeMap<>();
		int usageTotal;

		for (String key : usageMap.keySet()) {
			usageTotal = (int) (usageMap.get(key) * rateCardMap.get(key));
			if (totalMap.containsKey(rateCategoryMap.get(key))) {
				usageTotal = totalMap.get(rateCategoryMap.get(key)) + usageTotal;
			}
			totalMap.put(rateCategoryMap.get(key), usageTotal);
		}
		return totalMap;
	}

}
