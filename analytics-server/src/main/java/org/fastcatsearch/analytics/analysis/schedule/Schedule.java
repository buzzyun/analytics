package org.fastcatsearch.analytics.analysis.schedule;

import java.util.Date;


public abstract class Schedule {
	
	protected long scheduledTime;
	protected int delayInSeconds;
	
	public Schedule(long scheduledTime, int delayInSeconds){
		this.scheduledTime = scheduledTime;
		this.delayInSeconds = delayInSeconds;
	}
	
	public abstract void updateSchduleTime();

	public long scheduledTime() {
		return scheduledTime;
	}
	
	//delay 시간까지 감안한 실제 시작시간.
	public long delayedScheduledTime() {
		return scheduledTime + delayInSeconds * 1000L;
	}

	//실행날짜와는 별개로 통계대상이 되는 시각.
	public abstract long baseTime();
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + " > " + new Date(scheduledTime) + " : " + new Date(baseTime());
	}
	
}
