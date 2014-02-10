package org.fastcatgroup.analytics.analysis.schedule;


public class EveryMonthSchedule extends Schedule {
	private int dayOfMonth;
	private int timeOfDay;

	/**
	 * dayOfWeek는 요일. Calendar#DAY_OF_WEEK 참조. int timeOfDay 는 0~24 이다. 24는 자정이면서 다음날 0시도 된다.
	 * */
	public EveryMonthSchedule(int dayOfMonth, int timeOfDay, int delayInSeconds) {
		super(0, delayInSeconds);
		this.dayOfMonth = dayOfMonth;
		this.timeOfDay = timeOfDay;
	}

	@Override
	public void updateSchduleTime() {
		//TODO scheduledTime 설정필요.
				//scheduledTime =  
	}

}
