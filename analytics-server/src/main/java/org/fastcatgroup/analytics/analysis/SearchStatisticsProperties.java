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
		return new File(new File(dir, "Y" + year), "data");
	}

	public static File getMonthDataDir(File dir, Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		return new File(new File(new File(dir, "Y" + year), "M" + month), "data");
	}

	public static File getDayDataDir(File dir, Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return new File(new File(new File(new File(dir, "Y" + year), "M" + month), "D" + day), "data");
	}
}
