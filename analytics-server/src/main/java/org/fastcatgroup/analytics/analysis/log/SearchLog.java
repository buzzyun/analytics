package org.fastcatgroup.analytics.analysis.log;

public class SearchLog extends LogData {

	public SearchLog(String[] data) {
		super(data);
	}

	public String category() {
		return data[0];
	}

	public String keyword() {
		if (data.length >= 2) {
			return data[1];
		}else{
			return null;
		}
	}

	public String previousKeyword() {
		if (data.length >= 3) {
			return data[2];
		} else {
			return null;
		}
	}
}
