package org.fastcatgroup.analytics.analysis.schedule;

import java.util.Date;

public abstract class Schedule {
	public static final int LAST_DAY_OF_MONTH = Integer.MAX_VALUE;
	
	protected long scheduledTime;
	protected int delayInSeconds;
	
	public Schedule(long scheduledTime, int delayInSeconds){
		this.scheduledTime = scheduledTime;
		this.delayInSeconds = delayInSeconds;
	}
	
	public abstract Date updateSchduleTime();

	public long scheduledTime() {
		return scheduledTime;
	}

	
	//delay 시간까지 감안한 실제 시작시간.
	public long delayedScheduledTime() {
		return scheduledTime + delayInSeconds * 1000L;
	}
	
	
}
