package org.fastcatgroup.analytics.analysis;

import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.SearchLog;

public class SearchLogValidator implements LogValidator<SearchLog> {
	
	Set<String> banWords;
	
	public SearchLogValidator(Set<String> banWords) {
		this.banWords = banWords;
	}

	@Override
	public boolean isValid(SearchLog logData) {
		
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
