package org.fastcatgroup.analytics.analysis.log;

public class KeyCountLog extends LogData {

	private String keyword;
	private int count;
	
	public KeyCountLog(String keyword) {
		this(keyword, "1");
	}
	
	public KeyCountLog(String keyword, String count) {
		this.keyword = keyword;
		try {
			if(count!=null && !"".equals(count)) {
				this.count = Integer.parseInt(count);
			}
		} catch (NumberFormatException ignore) {
		}
	}

	@Override
	public String getKey() {
		return keyword;
	}

	@Override
	public int getCount() {
		return count;
	}
}
