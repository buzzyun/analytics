package org.fastcatsearch.analytics.analysis.log;

public class SearchLogResult {
	
	private int searchCount;
	private int searchResultCount;
	private int averageResponseTime;
	private int maxResponseTime;
	
	public SearchLogResult(int searchCount, int searchResultCount, int averageResponseTime, int maxResponseTime){
		this.searchCount = searchCount;
		this.searchResultCount = searchResultCount;
		this.averageResponseTime = averageResponseTime;
		this.maxResponseTime = maxResponseTime;
	}
	
	public int getSearchResultCount() {
		return searchResultCount;
	}
	
	public void setSearchResultCount(int searchResultCount) {
		this.searchResultCount = searchResultCount;
	}

	public int getSearchCount() {
		return searchCount;
	}
	
	public void setSearchCount(int searchCount) {
		this.searchCount = searchCount;
	}
	

	public int getAverageResponseTime() {
		return averageResponseTime;
	}

	public void setAverageResponseTime(int averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}

	public int getMaxResponseTime() {
		return maxResponseTime;
	}

	public void setMaxResponseTime(int maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}
	
	@Override
	public String toString(){
		return "count[" + searchCount +"] avg-resptime[" +averageResponseTime+ "] max-resptime[" +maxResponseTime+ "]";
	}
	
}
