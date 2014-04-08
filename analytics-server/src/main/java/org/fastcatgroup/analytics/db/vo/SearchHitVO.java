package org.fastcatgroup.analytics.db.vo;

public class SearchHitVO {

	protected String categoryId;
	protected String timeId;
	protected int hit;
	protected int avgTime;
	protected int maxTime;

	public String getTimeId() {
		return timeId;
	}

	public void setTimeId(String timeId) {
		this.timeId = timeId;
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

	public int getAvgTime() {
		return avgTime;
	}

	public void setAvgTime(int avgTime) {
		this.avgTime = avgTime;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}
}
