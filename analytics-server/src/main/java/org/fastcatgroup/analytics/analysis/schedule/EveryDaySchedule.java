package org.fastcatgroup.analytics.analysis.schedule;

import java.util.Calendar;

public class EveryDaySchedule extends Schedule {
	private int timeInDay;

	/**
	 * int timeInDay 는 0~24 이다. 24는 자정이면서 다음날 0시도 된다.
	 * */
	public EveryDaySchedule(int timeInDay, int delayInSeconds) {
		super(0, delayInSeconds);
		this.timeInDay = timeInDay;
	}

	@Override
	public void updateSchduleTime() {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		calendar.set(Calendar.HOUR_OF_DAY, timeInDay);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if(timeInDay < hour){
			//시간이 지났다면 다음날로 넘어간다.
			calendar.add(Calendar.DATE, 1);
		}
		scheduledTime = calendar.getTimeInMillis();
	}

}
