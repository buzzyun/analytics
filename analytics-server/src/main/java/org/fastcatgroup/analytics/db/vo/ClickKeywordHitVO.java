package org.fastcatgroup.analytics.db.vo;

public class ClickKeywordHitVO {
	
	private String keyword;
	private int count;
	private String clickType;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getClickType() {
		return clickType;
	}
	public void setClickType(String clickType) {
		this.clickType = clickType;
	}
	
	
}
