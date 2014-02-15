package org.fastcatgroup.analytics.analysis;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class SearchStatisticsPropertiesTest {

	@Test
	public void test() {
		
		Calendar calendar = Calendar.getInstance();
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

}
