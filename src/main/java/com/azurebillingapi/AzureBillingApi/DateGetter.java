package com.azurebillingapi.AzureBillingApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class DateGetter {

	private DateGetter() {
	}

	static String getToday() {
		LocalDate date = LocalDate.now();
		return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
	}

	static String getEndOfLastMonth() {
		LocalDate date = LocalDate.now().withDayOfMonth(1).minusDays(1);
		return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
	}
}
