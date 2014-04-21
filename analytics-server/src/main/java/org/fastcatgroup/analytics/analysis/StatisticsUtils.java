package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsUtils {
	private static Logger logger = LoggerFactory.getLogger(StatisticsUtils.class);
	
	public static final String encoding = "utf-8";
	public static int runKeySize = 10 * 10000;
	public static int realtimeSearchLogLimit = 6; //이전 타임 6개까지 저장.
	public static int maxKeywordLength = 20;

	public static final String ROOT_ID = "_root";
	
	public static int getDateDiff(Calendar c1, Calendar c2) {
		return (int) ((c2.getTimeInMillis() - c1.getTimeInMillis()) / (1000 * 60 * 60 * 24));
	}
	
	public static int getMonthDiff(Calendar c1, Calendar c2) {
		
		int mon1 = c1.get(Calendar.MONTH);
		int mon2 = c2.get(Calendar.MONTH);
		
		int y1 = c1.get(Calendar.YEAR);
		int y2 = c2.get(Calendar.YEAR);
		
		return (y2 - y1) * 12 + mon2 - mon1;
	}

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
	
	public static File getWeekDataDir(File dir, Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		String yearString = "Y" + year;
		String weekString = null;
		
		if(week < 10) {
			weekString = "W0" + week;
		} else {
			weekString = "W"+week;
		}
		return new File(new File(new File(dir, yearString), weekString), "data");
	}
	
	
	//type 에 따라 날짜를 변경한다. 예를들어, week type의 경우 입력날짜가 일요일이 아니더라도 일요일로 리턴한다.
	public static Calendar getCorrectedStartTime(Calendar calendar, int type) {
		if(type == Calendar.HOUR_OF_DAY){
			//금일 0시.
			calendar.set(Calendar.HOUR_OF_DAY, 0);
		}else if(type == Calendar.DAY_OF_MONTH){
			//변경안함.
		}else if(type == Calendar.WEEK_OF_YEAR){
			//해당 week의 월요일로 변경.
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONTH);
		}else if(type == Calendar.MONTH){
			//1월 1일 0시로 변경.
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		}else if(type == Calendar.YEAR){
			//변경안함.
		}
		return calendar;
	}
	
	public static Calendar getCorrectedEndTime(Calendar calendar, int type) {
		if(type == Calendar.HOUR_OF_DAY){
			//금일 0시.
			calendar.set(Calendar.HOUR_OF_DAY, 23);
		}else if(type == Calendar.DAY_OF_MONTH){
			//변경안함.
		}else if(type == Calendar.WEEK_OF_YEAR){
			//해당 week의 일요일로 변경.
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		}else if(type == Calendar.MONTH){
			//1월 1일 0시로 변경.
			calendar.set(Calendar.DAY_OF_MONTH, 1);
		}else if(type == Calendar.YEAR){
			//변경안함.
		}
		return calendar;
	}
	
	public static String[] getTimeComponent(Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		
		String[] component = new String[5];
		
		String yearString = String.valueOf(year);
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
		
		//일요일을 주의 마지막으로 간주한다.
		
		
		
		if (week < 10) {
			weekString = "0" + week;
		} else {
			weekString = "" + week;
		}
		
		int i = 0;
		component[i++] = yearString;
		component[i++] = monthString;
		component[i++] = dayString;
		component[i++] = hourString;
		component[i++] = weekString;
		
		return component;
	}
	
	public static String getTimeId(Calendar calendar, int type) {
		String timeId = null;
		
		String[] timeComponent = getTimeComponent(calendar);
		
		String yearString = timeComponent[0];
		String monthString = timeComponent[1];
		String dayString = timeComponent[2];
		String hourString = timeComponent[3];
		String weekString = timeComponent[4];
		
		if(type == Calendar.HOUR_OF_DAY){
			timeId = "H" + yearString + monthString + dayString + hourString;
		}else if(type == Calendar.DAY_OF_MONTH){
			timeId = "D" + yearString + monthString + dayString;
		}else if(type == Calendar.WEEK_OF_YEAR){
			timeId = "W" + yearString + weekString;
		}else if(type == Calendar.MONTH){
			timeId = "M" + yearString + monthString;
		}else if(type == Calendar.YEAR){
			timeId = "Y" + yearString;
		}
		
		return timeId;
	}
	
	static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy.MM.dd");
	
	public static Calendar parseDatetimeString(String timeString, boolean start) {
		try {
			Calendar calendar = StatisticsUtils.getCalendar();
			calendar.setTime(dateTimeFormat.parse(timeString));
			if(start) {
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
			} else {
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
			}
			return calendar;
		} catch (ParseException e) {
			logger.error("", e);
		}
		return null;
	}
	
	public static Calendar getCalendar() {
		return Calendar.getInstance(Locale.GERMAN);
	}

	public static String toDatetimeString(Calendar calendar) {
		return toDatetimeString(calendar, Calendar.DAY_OF_MONTH);
	}
	
	static SimpleDateFormat hourDateFormat = new SimpleDateFormat("yyyy.MM.dd HH");
	static SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyy.MM.dd");
	static SimpleDateFormat weekDateFormat = new SimpleDateFormat("yyyy.ww");
	static SimpleDateFormat monthDateFormat = new SimpleDateFormat("yyyy.MM");
	static SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");
	
	public static String toDatetimeString(Calendar calendar, int type) {
		if(type == Calendar.HOUR_OF_DAY){
			return hourDateFormat.format(calendar.getTime());
		}else if(type == Calendar.DAY_OF_MONTH){
			return dayDateFormat.format(calendar.getTime());
		}else if(type == Calendar.WEEK_OF_YEAR){
			return weekDateFormat.format(calendar.getTime());
		}else if(type == Calendar.MONTH){
			return monthDateFormat.format(calendar.getTime());
		}else if(type == Calendar.YEAR){
			return yearDateFormat.format(calendar.getTime());
		}
		
		return dateTimeFormat.format(calendar.getTime());
	}
	
	
	
	///한주의 시작을 월요일로 만드는 Locale.GERMAN 을 사용한다.
	public static Calendar parseTimeId(String timeId) {
		char type = timeId.charAt(0);
		if(type == 'H'){
			int year = Integer.parseInt(timeId.substring(1,5));
			int month = Integer.parseInt(timeId.substring(5,7)) - 1;
			int day = Integer.parseInt(timeId.substring(7,9));
			int hour = Integer.parseInt(timeId.substring(9,11));
			Calendar calendar = StatisticsUtils.getCalendar();
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
			Calendar calendar = StatisticsUtils.getCalendar();
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
			Calendar calendar = StatisticsUtils.getCalendar();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.WEEK_OF_YEAR, week);
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar;
		}else if(type == 'M'){
			int year = Integer.parseInt(timeId.substring(1,5));
			int month = Integer.parseInt(timeId.substring(5,7)) - 1;
			Calendar calendar = StatisticsUtils.getCalendar();
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
			Calendar calendar = StatisticsUtils.getCalendar();
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
	
	public static void setTimeFrom(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
	}
	
	public static void setTimeTo(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
	}
	
	public static Calendar getFirstDayOfWeek(Calendar calendar) {
		Calendar ret = (Calendar) calendar.clone();
		ret.add(Calendar.DAY_OF_MONTH, -((ret.get(Calendar.DAY_OF_WEEK) + 5) % 7));
		return ret;
	}
	
	public static Calendar getLastDayOfWeek(Calendar calendar) {
		Calendar ret = (Calendar) calendar.clone();
		ret.add(Calendar.DAY_OF_MONTH, (8 - ret.get(Calendar.DAY_OF_WEEK)) % 7);
		return ret;
	}

	public static boolean isEquals(Calendar startTime, Calendar timeCurrent, int type) {
		if(startTime == null || timeCurrent == null) {
			return false;
		}
		if(startTime.get(Calendar.YEAR) == timeCurrent.get(Calendar.YEAR)){
			if(type == Calendar.YEAR){
				return true;
			}
			
			if(startTime.get(Calendar.WEEK_OF_YEAR) == timeCurrent.get(Calendar.WEEK_OF_YEAR)){
				if(type == Calendar.WEEK_OF_YEAR){
					return true;
				}
				
			}
			
			if(startTime.get(Calendar.MONTH) == timeCurrent.get(Calendar.MONTH)){
				if(type == Calendar.MONTH){
					return true;
				}
				
				if(startTime.get(Calendar.DAY_OF_MONTH) == timeCurrent.get(Calendar.DAY_OF_MONTH)){
					if(type == Calendar.DAY_OF_MONTH){
						return true;
					}
					
					if(startTime.get(Calendar.HOUR_OF_DAY) == timeCurrent.get(Calendar.HOUR_OF_DAY)){
						if(type == Calendar.HOUR_OF_DAY){
							return true;
						}
						
					}
				}
			}
			
		}
		
		return false;
		
	}
}
