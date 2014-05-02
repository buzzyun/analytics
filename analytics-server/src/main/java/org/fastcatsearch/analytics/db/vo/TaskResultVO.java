package org.fastcatsearch.analytics.db.vo;

import java.sql.Timestamp;

public class TaskResultVO {
	private String siteId;
	private Timestamp targetTime; //통계대상이 되는 날짜.
	private Timestamp startTime; //실제 task를 시작한 날짜와 시간.
	private Timestamp endTime;
	private String duration;
	private String scheduled;	//스케쥴인지. S(schedule), M(manual)
	private String resultStatus; //결과. success인지, fail인지.
	private String taskId; 		//task id. 약자. DAILY_SP 등.
	private String taskName;	// task 명. Daily Search Progress Task 등.
	private String explain;
	
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public Timestamp getTargetTime() {
		return targetTime;
	}
	public void setTargetTime(Timestamp targetTime) {
		this.targetTime = targetTime;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getExplain() {
		return explain;
	}
	public void setExplain(String explain) {
		this.explain = explain;
	}
	
}
