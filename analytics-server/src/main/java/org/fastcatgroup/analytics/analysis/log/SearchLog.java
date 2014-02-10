package org.fastcatgroup.analytics.analysis.log;

public class SearchLog extends LogData {

	protected String categoryId;
	protected String keyword;
	
	public SearchLog(String categoryId, String keyword) {
		this.categoryId = categoryId;
		this.keyword = keyword;
	}

	public String categoryId() {
		return categoryId;
	}

	public String keyword() {
		return keyword;
	}


	@Override
	public String getKey() {
		return keyword;
	}
	
	@Override
	public String toString(){
		return categoryId + "\t" + keyword;
	}
}
