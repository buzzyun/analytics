package org.fastcatgroup.analytics.analysis.schedule;

import java.util.Calendar;
import java.util.Date;

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
		int hour = 22;
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, hour);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, timeInDay);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if(timeInDay < hour){
			//시간이 지났다면 다음날로 넘어간다.
			calendar.add(Calendar.DATE, 1);
		}
		long scheduledTime = calendar.getTimeInMillis();
		System.out.println(new Date(scheduledTime));
		long wait =  scheduledTime - now.getTimeInMillis();
		System.out.println("waiting " + wait/1000 + " s");
	}
}
