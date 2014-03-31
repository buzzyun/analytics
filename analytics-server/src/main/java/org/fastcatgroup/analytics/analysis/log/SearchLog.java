package org.fastcatgroup.analytics.analysis.log;

public class SearchLog extends LogData {

	protected String time;
	protected String categoryId;
	protected String keyword;
	protected int count;
	
	public SearchLog(String time, String categoryId, String keyword) {
		this(time, categoryId, keyword, "1");
	}
	
	public SearchLog(String time, String categoryId, String keyword, String count) {
		this.time = time;
		this.categoryId = categoryId;
		this.keyword = keyword;
		try {
			if(count!=null && !"".equals(count)) {
				this.count = Integer.parseInt(count);
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
	
	@Override
	public String toString(){
		return categoryId + "\t" + keyword + "\t" + count;
	}
}
