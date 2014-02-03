package org.fastcatgroup.analytics.analysis2.schedule;

import java.util.Date;

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
	public Date updateSchduleTime() {
		//TODO scheduledTime 설정필요.
		//scheduledTime =  
		return null;
	}

}
