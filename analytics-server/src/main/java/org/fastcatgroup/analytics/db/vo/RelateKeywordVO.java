package org.fastcatgroup.analytics.db.vo;

import java.sql.Timestamp;


public class RelateKeywordVO {
	
	private int id;
	private String site;
	private String category;
	private String keyword;
	private String value;
	private Timestamp updateTime;
	
	public RelateKeywordVO(){
	}

	public RelateKeywordVO(String site, String category, String keyword, Timestamp updateTime){
		this.setSiteId(site);
		this.category = category;
		this.keyword = keyword;
		this.updateTime = updateTime;
	}
	
	public String getSiteId() {
		return site;
	}

	public void setSiteId(String site) {
		this.site = site;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
