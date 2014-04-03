package org.fastcatgroup.analytics.analysis;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

public class SearchStatisticsPropertiesTest {

	@Test
	public void test() {
		
		Calendar calendar = SearchStatisticsProperties.getCalendar();
		int type = Calendar.HOUR_OF_DAY;
		String timeId = SearchStatisticsProperties.getTimeId(calendar, type);
		System.out.println("HOUR_OF_DAY :" + timeId);
		
		type = Calendar.WEEK_OF_YEAR;
		timeId = SearchStatisticsProperties.getTimeId(calendar, type);
		System.out.println("WEEK_OF_YEAR :" + timeId);
		
		type = Calendar.MONTH;
		timeId = SearchStatisticsProperties.getTimeId(calendar, type);
		System.out.println("MONTH :" + timeId);
		
		type = Calendar.YEAR;
		timeId = SearchStatisticsProperties.getTimeId(calendar, type);
		System.out.println("YEAR :" + timeId);
	}
	
	@Test
	public void testParseTimeId() {
		String timeId = "Y2013";
		Calendar calendar = SearchStatisticsProperties.parseTimeId(timeId);
		System.out.println(timeId + " : " + new Date(calendar.getTimeInMillis()));
		
		timeId = "M201307";
		calendar = SearchStatisticsProperties.parseTimeId(timeId);
		System.out.println(timeId + " : " + new Date(calendar.getTimeInMillis()));
		
		
		timeId = "W201345";
		calendar = SearchStatisticsProperties.parseTimeId(timeId);
		System.out.println(timeId + " : " + new Date(calendar.getTimeInMillis()));
		
		timeId = "D20130722";
		calendar = SearchStatisticsProperties.parseTimeId(timeId);
		System.out.println(timeId + " : " + new Date(calendar.getTimeInMillis()));
		
		timeId = "H2013072214";
		calendar = SearchStatisticsProperties.parseTimeId(timeId);
		System.out.println(timeId + " : " + new Date(calendar.getTimeInMillis()));
		
	}

	@Test
	public void testCorrectedTimeId() {
		int type = Calendar.WEEK_OF_YEAR;
		Calendar startTime = null;
		Calendar endTime = null;
		
		startTime = SearchStatisticsProperties.getCorrectedStartTime(Calendar.getInstance(Locale.GERMAN), type);
		endTime = SearchStatisticsProperties.getCorrectedEndTime(Calendar.getInstance(Locale.GERMAN), type);
		
		System.out.println("start = " + startTime.getTime());
		System.out.println("end = " + endTime.getTime());
		
		String startTimeId = SearchStatisticsProperties.getTimeId(startTime, type);
		String endTimeId = SearchStatisticsProperties.getTimeId(endTime, type);
		
		System.out.println("start = " + startTimeId);
		System.out.println("end = " + endTimeId);
	}
	
	@Test
	public void testCalendarLocale() {
		Calendar today = SearchStatisticsProperties.getCalendar();
		System.out.println("default > " + today.getFirstDayOfWeek() + " > " + today.getTime() + " : " + today.get(Calendar.DAY_OF_WEEK));
		today.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		System.out.println(">>> " + today.getTime() + " : " + today.get(Calendar.WEEK_OF_YEAR));	
	
		today = Calendar.getInstance(Locale.GERMAN);
		System.out.println("Locale.GERMAN > " + today.getFirstDayOfWeek() + " > " + today.getTime() + " : " + today.get(Calendar.DAY_OF_WEEK));
		today.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		System.out.println(">>> " + today.getTime() + " : " + today.get(Calendar.WEEK_OF_YEAR));
	}
}
