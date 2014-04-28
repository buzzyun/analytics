package org.fastcatsearch.analytics.db.vo;

import java.sql.Timestamp;


public class RelateKeywordVO {
	
	private int id;
	private String keyword;
	private String value;
	private Timestamp updateTime;
	
	public RelateKeywordVO(){
	}

	public RelateKeywordVO(String keyword, Timestamp updateTime){
		this.keyword = keyword;
		this.updateTime = updateTime;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
