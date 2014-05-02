package org.fastcatsearch.analytics.analysis.schedule;


public class EveryYearSchedule extends Schedule {
	private int monthOfYear;
	private int dayOfMonth;
	private int timeOfDay;

	/**
	 * int timeOfDay 는 0~24 이다. 24는 자정이면서 다음날 0시도 된다.
	 * */
	public EveryYearSchedule(int monthOfYear, int dayOfMonth, int timeOfDay, int delayInSeconds) {
		this(monthOfYear, dayOfMonth, timeOfDay, delayInSeconds, true);
	}
	public EveryYearSchedule(int monthOfYear, int dayOfMonth, int timeOfDay, int delayInSeconds, boolean isScheduled) {
		super(0, delayInSeconds, isScheduled);
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		this.timeOfDay = timeOfDay;
	}

	@Override
	public void updateSchduleTime() {
		//TODO scheduledTime 설정필요.
				//scheduledTime =  
	}

	@Override
	public long baseTime() {
		return scheduledTime - 24 * 3600 * 1000; //통계 대상은 하루전으로. 
	}
}
