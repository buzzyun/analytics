package org.fastcatsearch.analytics.db.vo;

import java.sql.Timestamp;

public class TaskResultVO {
	private String siteId;
	private String targetTime; //통계대상이 되는 날짜.
	private Timestamp startTime; //실제 task를 시작한 날짜와 시간.
	private Timestamp endTime;
	private String duration;
	private String scheduled;	//스케쥴인지. S(schedule), M(manual)
	private String resultStatus; //결과. success인지, fail인지.
	private String taskId; 		//task id. 약자. DAILY_SP 등.
	private String taskName;	// task 명. Daily Search Progress Task 등.
	private String detail;
	
	public TaskResultVO() { }
	
	public TaskResultVO(String siteId, String targetTime, Timestamp startTime, Timestamp endTime, String duration, String scheduled, String resultStatus, String taskId,
			String taskName, String detail) {
		this.siteId = siteId;
		this.targetTime = targetTime;
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
		this.scheduled = scheduled;
		this.resultStatus = resultStatus;
		this.taskId = taskId;
		this.taskName = taskName;
		this.detail = detail;
	}

	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public String getTargetTime() {
		return targetTime;
	}
	public void setTargetTime(String targetTime) {
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
	public String getScheduled() {
		return scheduled;
	}
	public void setScheduled(String scheduled) {
		this.scheduled = scheduled;
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
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
}
