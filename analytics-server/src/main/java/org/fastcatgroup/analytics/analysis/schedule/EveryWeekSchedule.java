package org.fastcatgroup.analytics.analysis.schedule;


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
	public void updateSchduleTime() {
		//TODO scheduledTime 설정필요.
				//scheduledTime =  
	}
	
	@Override
	public long baseTime() {
		return scheduledTime - 24 * 3600 * 1000; //통계 대상은 하루전으로. 
	}

}
