package org.fastcatgroup.analytics.analysis.log;

public class LogType {
	private String siteId;
	private String categoryId;
	
	public LogType(String siteIdId, String categoryId){
		this.siteId = siteIdId;
		this.categoryId = categoryId;
	}
	public String getSiteId() {
		return siteId;
	}

	public String getCategoryId() {
		return categoryId;
	}
}
