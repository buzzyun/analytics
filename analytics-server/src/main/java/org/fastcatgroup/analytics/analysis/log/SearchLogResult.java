package org.fastcatgroup.analytics.analysis.log;

public class SearchLogResult {
	
	private int searchCount;
	private int averageResponseTime;
	private int maxResponseTime;
	
	public SearchLogResult(int searchCount, int averageResponseTime, int maxResponseTime){
		this.searchCount = searchCount;
		this.averageResponseTime = averageResponseTime;
		this.maxResponseTime = maxResponseTime;
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
