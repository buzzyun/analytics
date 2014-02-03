package org.fastcatgroup.analytics.analysis2.schedule;

import java.util.Date;

public class EveryWeekSchedule extends Schedule {
	private int dayOfWeek;
	private int timeOfDay;

	/**
	 * dayOfWeek는 요일. Calendar#DAY_OF_WEEK 참조. int timeOfDay 는 0~24 이다. 24는 자정이면서 다음날 0시도 된다.
	 * */
	public EveryWeekSchedule(int dayOfWeek, int timeOfDay, int delayInSeconds) {
		super(0, delayInSeconds);
		this.dayOfWeek = dayOfWeek;
		this.timeOfDay = timeOfDay;
	}

	@Override
	public Date updateSchduleTime() {
		//TODO scheduledTime 설정필요.
				//scheduledTime =  
		return null;
	}

}
