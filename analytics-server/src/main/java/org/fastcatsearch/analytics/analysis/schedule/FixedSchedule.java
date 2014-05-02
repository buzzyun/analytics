package org.fastcatsearch.analytics.analysis.schedule;

import java.util.Calendar;

public class FixedSchedule extends Schedule {
	private int periodInSeconds;

	public FixedSchedule(Calendar startTime, int periodInSeconds, int delayInSeconds) {
		this(startTime, periodInSeconds, delayInSeconds, true);
	}
			
	public FixedSchedule(Calendar startTime, int periodInSeconds, int delayInSeconds, boolean isScheduled) {
		super(startTime.getTimeInMillis(), delayInSeconds, isScheduled);
		this.periodInSeconds = periodInSeconds;
	}

	public void updateSchduleTime() {
		long now = System.currentTimeMillis();
		if (scheduledTime < now) {
			long nextStartTime = scheduledTime;
			long period = periodInSeconds * 1000L;
			// 현 시간보다 커질때까지 더한다.
			if (period > 0) {
				// 주기가 0이 아닐때만 더해서 다음 시간을 구한다.
				while (nextStartTime < now) {
					nextStartTime += period;// increase by period
				}

				scheduledTime = nextStartTime;
			} else {
				// 주기가 0 이라면 그대로 둔다.
			}
		} else {
			// 현시간보다 크면 그대로 둔다.
		}

	}
	
	@Override
	public long baseTime() {
		return scheduledTime;
	}
}
