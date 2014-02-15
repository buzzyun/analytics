package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.Calendar;

public class SearchStatisticsProperties {
	public static final String KEY_COUNT_LOG_FILENAME = "key-count.log";
	public static final String encoding = "utf-8";
	public static int runKeySize = 10 * 10000;
	public static int realtimeSearchLogLimit = 6; //이전 타임 6개까지 저장.

	public static final String ROOT_ID = "_root";

	public static File getYearDataDir(File dir, Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		String yearString = "Y" + year;
		return new File(new File(dir, yearString), "data");
	}

	public static File getMonthDataDir(File dir, Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
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
		int month = calendar.get(Calendar.MONTH) + 1;
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
	
	
	public static String getTimeId(Calendar calendar, int type) {
		String timeId = null;
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		
		String monthString = null;
		String dayString = null;
		String hourString = null;
		String weekString = null;
		
		if (month < 10) {
			monthString = "0" + month;
		} else {
			monthString = "" + month;
		}
		if (day < 10) {
			dayString = "0" + day;
		} else {
			dayString = "" + day;
		}
		if (hour < 10) {
			hourString = "0" + hour;
		} else {
			hourString = "" + hour;
		}
		if (week < 10) {
			weekString = "0" + week;
		} else {
			weekString = "" + week;
		}
		
		if(type == Calendar.HOUR_OF_DAY){
			timeId = "H" + year + monthString + dayString + hourString;
		}else if(type == Calendar.DATE){
			timeId = "D" + year + monthString + dayString;
		}else if(type == Calendar.WEEK_OF_YEAR){
			timeId = "W" + year + weekString;
		}else if(type == Calendar.MONTH){
			timeId = "M" + year + monthString;
		}else if(type == Calendar.YEAR){
			timeId = "Y" + year;
		}
		
		return timeId;
	}
	
	public static Calendar parseTimeId(String timeId) {
		char type = timeId.charAt(0);
		if(type == 'H'){
			int year = Integer.parseInt(timeId.substring(1,5));
			int month = Integer.parseInt(timeId.substring(5,7)) - 1;
			int day = Integer.parseInt(timeId.substring(7,9));
			int hour = Integer.parseInt(timeId.substring(9,11));
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar;
		}else if(type == 'D'){
			int year = Integer.parseInt(timeId.substring(1,5));
			int month = Integer.parseInt(timeId.substring(5,7)) - 1;
			int day = Integer.parseInt(timeId.substring(7,9));
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar;
		}else if(type == 'W'){
			int year = Integer.parseInt(timeId.substring(1,5));
			int week = Integer.parseInt(timeId.substring(5,7));
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.WEEK_OF_YEAR, week);
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar;
		}else if(type == 'M'){
			int year = Integer.parseInt(timeId.substring(1,5));
			int month = Integer.parseInt(timeId.substring(5,7)) - 1;
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar;
		}else if(type == 'Y'){
			int year = Integer.parseInt(timeId.substring(1,5));
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar;
		}
		
		return null;
	}
}
