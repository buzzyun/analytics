package org.fastcatsearch.analytics.analysis;

public interface SearchStatistics {
	public static final String KEYWORD = "Keyword";
	public static final String PREV_KEYWORD = "PrevKeyword";
	public static final String CATEGORY = "Category";
	
	public void add(Object q);
}
