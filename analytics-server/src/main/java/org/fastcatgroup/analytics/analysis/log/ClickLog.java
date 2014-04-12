package org.fastcatgroup.analytics.analysis.log;

public class ClickLog extends LogData {

	protected String time;
	protected String keyword;
	protected String clickId;
	protected String clickType;
	
	public ClickLog(String time, String keyword, String clickId, String clickType) {
		this.time = time;
		this.keyword = keyword;
		this.clickId = clickId;
		this.clickType = clickType;
	}
	
	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getKeyword() {
		return keyword;
	}


	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}


	public String getClickId() {
		return clickId;
	}


	public void setClickId(String clickId) {
		this.clickId = clickId;
	}


	public String getClickType() {
		return clickType;
	}


	public void setClickType(String clickType) {
		this.clickType = clickType;
	}


	@Override
	public String toString(){
		return keyword + "\t" + clickId + "\t" + clickType;
	}

	@Override
	public String getKey() {
		return keyword;
	}


	@Override
	public int getCount() {
		return 1;
	}
}
