package org.fastcatsearch.analytics.analysis.schedule;

public class TimeSchedule extends Schedule {

	public TimeSchedule(long scheduledTime, int delayInSeconds) {
		super(scheduledTime, delayInSeconds);
	}

	@Override
	public void updateSchduleTime() {
	}

	@Override
	public long baseTime() {
		return scheduledTime; //실행과 동일 시간대.
	}
}
