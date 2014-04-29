package org.fastcatsearch.analytics.analysis;

import java.util.Set;

import org.fastcatsearch.analytics.analysis.log.RelateSearchLog;
import org.fastcatsearch.analytics.util.CharacterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelateSearchLogValidator implements LogValidator<RelateSearchLog> {
	
	private static final Logger logger = LoggerFactory.getLogger(RelateSearchLogValidator.class);
	
	private Set<String> banWords;
	private int maxKeywordLength;
	
	public RelateSearchLogValidator(Set<String> banWords, int maxKeywordLength) {
		this.banWords = banWords;
		this.maxKeywordLength = maxKeywordLength;
	}

	@Override
	public boolean isValid(RelateSearchLog logData) {
		if(logData.keyword() == null || logData.previousKeyword() == null) {
			return false;
		}
		
		if(maxKeywordLength != 0 
				&& (logData.keyword().length() > maxKeywordLength 
						|| logData.previousKeyword().length() > maxKeywordLength)){
			return false;
		}
		
		if (banWords != null) {
			
			// 금지어의 경우 로그에 기록하지 않는다.
			if(banWords.contains(logData.keyword()) || banWords.contains(logData.previousKeyword())){
				return false;
			}
		}
		
		// 표현할 수 없는 특수문자는 기록하지 않는다.
		if( !CharacterUtils.isValidCharacter(logData.keyword()) ||
				!CharacterUtils.isValidCharacter(logData.previousKeyword()) ) {
			logger.trace("keyword:[{}:{}] not avail", logData.keyword(), logData.previousKeyword());
			return false;
		}
		
		// 원본과 대상이 같으면 기록하지 않는다.
		if(logData.keyword().equals(logData.previousKeyword())) {
			logger.trace("keyword:[{}:{}] not avail", logData.keyword(), logData.previousKeyword());
			return false;
		}
		logger.trace("keyword:[{}] / prev:[{}]",logData.keyword(), logData.previousKeyword());
		return true;
	}
	
}
