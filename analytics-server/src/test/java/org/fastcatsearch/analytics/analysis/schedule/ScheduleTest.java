package org.fastcatsearch.analytics.analysis.schedule;

import java.util.Calendar;
import java.util.Date;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.schedule.EveryDaySchedule;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
import org.junit.Test;

public class ScheduleTest {

	@Test
	public void dailyTest() {
		int timeInDay = 0;
		int delayInSeconds = 5;
		Schedule dailySchedule = new EveryDaySchedule(timeInDay, delayInSeconds); 
		
		dailySchedule.updateSchduleTime();
		
		long time = dailySchedule.scheduledTime();
		System.out.println(new Date(time));
		
		time = dailySchedule.delayedScheduledTime();
		System.out.println(new Date(time));
		
	}

	@Test
	public void testNextDaytime(){
		int timeInDay = 0;
		int hour = 1;
		Calendar now = StatisticsUtils.getCalendar();
		now.set(Calendar.HOUR_OF_DAY, hour);
		System.out.println("now > " + new Date(now.getTimeInMillis()));
		Calendar calendar = StatisticsUtils.getCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, timeInDay);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		System.out.println("tobe > " + new Date(calendar.getTimeInMillis()));
		if(calendar.before(now)){
			//시간이 지났다면 다음날로 넘어간다.
			calendar.add(Calendar.DATE, 1);
		}
		long scheduledTime = calendar.getTimeInMillis();
		System.out.println(new Date(scheduledTime));
		long wait =  scheduledTime - now.getTimeInMillis();
		System.out.println("waiting " + wait/1000 + " s");
	}
}
