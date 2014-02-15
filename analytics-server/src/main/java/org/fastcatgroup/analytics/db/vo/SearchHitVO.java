package org.fastcatgroup.analytics.db.vo;

public class SearchHitVO {

	protected String categoryId;
	protected String timeId;
	protected char timeType;

	protected int hit;

	public String getTimeId() {
		return timeId;
	}

	public void setTimeId(String timeId) {
		this.timeId = timeId;
	}

	public char getTimeType() {
		return timeType;
	}

	public void setTimeType(char timeType) {
		this.timeType = timeType;
	}
	
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}
}
