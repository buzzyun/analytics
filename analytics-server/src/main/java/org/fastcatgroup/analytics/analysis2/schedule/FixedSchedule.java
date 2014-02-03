package org.fastcatgroup.analytics.analysis2.schedule;

import java.util.Date;

public class FixedSchedule extends Schedule {
	private int periodInSeconds;

	public FixedSchedule(Date startTime, int periodInSeconds, int delayInSeconds) {
		super(startTime.getTime(), delayInSeconds);
		this.periodInSeconds = periodInSeconds;
	}

	public Date updateSchduleTime() {
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

		// 실제시작시간은 delay이후 시작한다.
		return new Date(scheduledTime + delayInSeconds);
	}
}
