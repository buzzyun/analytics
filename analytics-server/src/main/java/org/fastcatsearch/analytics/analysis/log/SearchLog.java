package org.fastcatsearch.analytics.analysis.log;

public class SearchLog extends LogData {

	protected String time;
	protected String categoryId;
	protected String keyword;
	protected int count;
	protected int resultCount;
	protected int responseTime;
	protected String serviceType;
	
	public SearchLog(String time, String categoryId, String keyword, String count, String resultCount, String responseTime, String serviceType) {
		//if(keyword!=null) {
		//	keyword = keyword.toUpperCase();
		//}
		this.time = time;
		this.categoryId = categoryId;
		this.keyword = keyword;
		try {
			if(count!=null && !"".equals(count)) {
				this.count = Integer.parseInt(count);
			}
		} catch (NumberFormatException ignore) {
		}
		
		try {
			if(resultCount!=null && !"".equals(resultCount)) {
				this.resultCount = Integer.parseInt(resultCount);
			}
		} catch (NumberFormatException ignore) {
		}
		
		try {
			if(responseTime!=null && !"".equals(responseTime)) {
				this.responseTime = Integer.parseInt(responseTime);
			}
		} catch (NumberFormatException ignore) {
		}
		this.serviceType = serviceType;
	}
	
	public String getTime() {
		return time;
	}

	public String categoryId() {
		return categoryId;
	}

	public String keyword() {
		return keyword;
	}
	
	@Override
	public int getCount() {
		return count;
	}

	@Override
	public String getKey() {
		return keyword;
	}
	
	public int getResultCount() {
		return resultCount;
	}
	
	public int getResponseTime() {
		return responseTime;
	}
	
	public String getServiceType() {
		return serviceType;
	}
	
	@Override
	public String toString(){
		return categoryId + "\t" + keyword + "\t" + count;
	}
}
