package org.fastcatgroup.analytics.analysis;

import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.RelateSearchLog;

public class RelateSearchLogValidator implements LogValidator<RelateSearchLog> {
	
	Set<String> banWords;
	
	public RelateSearchLogValidator(Set<String> banWords) {
		this.banWords = banWords;
	}

	@Override
	public boolean isValid(RelateSearchLog logData) {
		
		if (banWords != null) {
			for (String banWord : banWords) {
				if (logData.keyword() == null || logData.keyword().contains(banWord) || logData.previousKeyword() == null || logData.previousKeyword().contains(banWord)) {
					// 금지어의 경우 로그에 기록하지 않는다.
					return false;
				}
			}
		}
		return true;
	}

}
