package org.fastcatgroup.analytics.analysis.log;

public class CategoryLog {

	private LogType logType;
	private String[] data;
	
	public CategoryLog(LogType logType, String[] data){
		this.logType = logType;
		this.data = data;
	}

	public LogType getLogType() {
		return logType;
	}
	
	public String[] getData() {
		return data;
	}
	
}
