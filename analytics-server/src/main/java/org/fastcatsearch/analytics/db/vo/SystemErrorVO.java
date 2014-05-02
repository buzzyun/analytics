package org.fastcatsearch.analytics.db.vo;

import java.sql.Timestamp;

public class SystemErrorVO {
	private int id;
	private Timestamp time;
	private String errorMessage;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
