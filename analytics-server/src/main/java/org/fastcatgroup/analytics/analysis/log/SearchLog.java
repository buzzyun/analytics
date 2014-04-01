package org.fastcatgroup.analytics.analysis.log;

public class SearchLog extends LogData {

	protected String time;
	protected String categoryId;
	protected String keyword;
	protected int count;
	protected int responseTime;
	
	public SearchLog(String time, String categoryId, String keyword) {
		this(time, categoryId, keyword, "1", "0");
	}
	
	public SearchLog(String time, String categoryId, String keyword, String count, String responseTime) {
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
			if(responseTime!=null && !"".equals(responseTime)) {
				this.responseTime = Integer.parseInt(responseTime);
			}
		} catch (NumberFormatException ignore) {
		}
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
	
	public int getResponseTime() {
		return responseTime;
	}
	
	@Override
	public String toString(){
		return categoryId + "\t" + keyword + "\t" + count;
	}
}
