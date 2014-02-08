package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.Calendar;

public class SearchStatisticsProperties {
	public static final String KEY_COUNT_LOG_FILENAME = "key-count.log";
	public static final String encoding = "utf-8";
	public static int runKeySize = 10 * 10000;

	public static final String ROOT_ID = "_root";

	public static File getYearDataDir(File dir, Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		String yearString = "Y" + year;
		return new File(new File(dir, yearString), "data");
	}

	public static File getMonthDataDir(File dir, Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		String yearString = "Y" + year;
		String monthString = null;
		if (month < 10) {
			monthString = "M0" + month;
		} else {
			monthString = "M" + month;
		}
		return new File(new File(new File(dir, yearString), monthString), "data");
	}

	public static File getDayDataDir(File dir, Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String yearString = "Y" + year;
		String monthString = null;
		String dayString = null;
		if (month < 10) {
			monthString = "M0" + month;
		} else {
			monthString = "M" + month;
		}
		if (day < 10) {
			dayString = "D0" + day;
		} else {
			dayString = "D" + day;
		}
		return new File(new File(new File(new File(dir, yearString), monthString), dayString), "data");
	}
}
