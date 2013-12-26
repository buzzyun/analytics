package org.fastcatgroup.analytics.db.vo;

public class SearchTypeRatioVO {
	
	private String timeId;
	
	private String type;
	
	private int hit;
	
	public String getTimeId() {
		return timeId;
	}
	public void setTimeId(String timeId) {
		this.timeId = timeId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getHit() {
		return hit;
	}
	public void setHit(int hit) {
		this.hit = hit;
	}

}
