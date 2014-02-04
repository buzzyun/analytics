package org.fastcatgroup.analytics.analysis.log;

public class SearchLog extends LogData {

	protected String categoryId;
	protected String keyword;
	protected String previousKeyword;
	
	public SearchLog(String categoryId, String keyword, String previousKeyword) {
		this.categoryId = categoryId;
		this.keyword = keyword;
		this.previousKeyword = previousKeyword;
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
		return keyword;
	}
}
