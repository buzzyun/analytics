package org.fastcatgroup.analytics.db.vo;

public class SearchKeywordHitVO {
	
	private String timeId;
	
	private String keyword;
	
	private int hit;
	
	public String getTimeId() {
		return timeId;
	}
	public void setTimeId(String timeId) {
		this.timeId = timeId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getHit() {
		return hit;
	}
	public void setHit(int hit) {
		this.hit = hit;
	}
}
