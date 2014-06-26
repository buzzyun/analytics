package org.fastcatsearch.analytics.analysis.log;

public class RelateSearchLog extends LogData {

	protected String categoryId;
	protected String keyword;
	protected String previousKeyword;
	protected String key;
	
	public RelateSearchLog(String categoryId, String keyword, String previousKeyword) {
		if(keyword!=null) {
			keyword = keyword.toUpperCase();
		}
		this.categoryId = categoryId;
		this.keyword = keyword;
		this.previousKeyword = previousKeyword;
		this.key = keyword + "\t" + previousKeyword; 
	}

	public String categoryId() {
		return categoryId;
	}

	public String keyword() {
		return keyword;
	}

	public String previousKeyword() {
		return previousKeyword;
	}

	@Override
	public String getKey() {
		return key;
	}
	
	@Override
	public int getCount() {
		return 1;
	}
	
	@Override
	public String toString(){
		return categoryId + "\t" + keyword + "\t" + previousKeyword;
	}
}
