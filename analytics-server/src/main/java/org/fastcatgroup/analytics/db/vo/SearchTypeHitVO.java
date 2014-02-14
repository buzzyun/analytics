package org.fastcatgroup.analytics.db.vo;

public class SearchTypeHitVO {
	
	private String timeId;
	
	private String dtype;
	
	private int hit;
	
	public String getTimeId() {
		return timeId;
	}
	public void setTimeId(String timeId) {
		this.timeId = timeId;
	}
	public String getDtype() {
		return dtype;
	}
	public void setDtype(String dType) {
		this.dtype = dType;
	}
	public int getHit() {
		return hit;
	}
	public void setHit(int hit) {
		this.hit = hit;
	}

}
