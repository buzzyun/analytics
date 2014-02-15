package org.fastcatgroup.analytics.db.vo;

public class SearchTypeHitVO {
	
	private String categoryId;
	
	private String timeId;
	
	private String typeId;
	
	private String dtype;
	
	private int hit;
	
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getTimeId() {
		return timeId;
	}
	public void setTimeId(String timeId) {
		this.timeId = timeId;
	}
	
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
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
