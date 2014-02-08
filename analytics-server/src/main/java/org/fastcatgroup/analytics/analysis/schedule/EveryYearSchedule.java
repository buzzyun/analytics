package org.fastcatgroup.analytics.analysis.schedule;

import java.util.Date;

public class EveryYearSchedule extends Schedule {
	private int monthOfYear;
	private int dayOfMonth;
	private int timeOfDay;

	/**
	 * int timeOfDay 는 0~24 이다. 24는 자정이면서 다음날 0시도 된다.
	 * */
	public EveryYearSchedule(int monthOfYear, int dayOfMonth, int timeOfDay, int delayInSeconds) {
		super(0, delayInSeconds);
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		this.timeOfDay = timeOfDay;
	}

	@Override
	public Date updateSchduleTime() {
		//TODO scheduledTime 설정필요.
				//scheduledTime =  
		return null;
	}

}
