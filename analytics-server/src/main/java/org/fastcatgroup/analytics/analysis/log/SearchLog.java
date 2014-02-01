package org.fastcatgroup.analytics.analysis.log;

public class SearchLog  {
	private String prevKeyword;

	public SearchLog(String keyword, String prevKeyword) {
//		super(keyword);
		this.prevKeyword = prevKeyword;
	}

	public String getPrevKeyword() {
		return prevKeyword;
	}

	public String toString() {
		return getClass().getSimpleName() + ": " + null + " : " + prevKeyword;
	}
}
