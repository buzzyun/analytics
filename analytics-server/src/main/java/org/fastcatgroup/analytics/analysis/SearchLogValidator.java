package org.fastcatgroup.analytics.analysis;

import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.SearchLog;

public class SearchLogValidator implements LogValidator<SearchLog> {
	
	private Set<String> banWords;
	private int maxKeywordLength;
	
	public SearchLogValidator(Set<String> banWords, int maxKeywordLength) {
		this.banWords = banWords;
		this.maxKeywordLength = maxKeywordLength;
	}

	@Override
	public boolean isValid(SearchLog logData) {
		if(logData.keyword().length() > maxKeywordLength){
			return false;
		}
		
		if (banWords != null) {
			for (String banWord : banWords) {
				if (logData.keyword().contains(banWord)) {
					// 금지어의 경우 로그에 기록하지 않는다.
					return false;
				}
			}
		}
		return true;
	}

}
