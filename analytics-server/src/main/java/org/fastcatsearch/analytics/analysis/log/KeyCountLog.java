package org.fastcatsearch.analytics.analysis.log;

public class KeyCountLog extends LogData {

	private String keyword;
	private int count;
	
	public KeyCountLog(String keyword, String count) {
		//if(keyword!=null) {
		//	keyword = keyword.toUpperCase();
		//}
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
