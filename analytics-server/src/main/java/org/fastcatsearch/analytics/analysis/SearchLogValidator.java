package org.fastcatsearch.analytics.analysis;

import java.util.Set;

import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.util.CharacterUtils;

public class SearchLogValidator implements LogValidator<SearchLog> {
	
	private Set<String> banWords;
	private int maxKeywordLength;
	
	public SearchLogValidator(Set<String> banWords, int maxKeywordLength) {
		this.banWords = banWords;
		this.maxKeywordLength = maxKeywordLength;
	}

	@Override
	public boolean isValid(SearchLog logData) {
		if(logData.keyword() == null) {
			return false;
		}
		
		if(maxKeywordLength != 0 && logData.keyword().length() > maxKeywordLength){
			return false;
		}
		
		if(!CharacterUtils.isValidCharacter(logData.keyword())){
			return false;
		}
		
		if (banWords != null) {
			// 금지어의 경우 로그에 기록하지 않는다.
			return !banWords.contains(logData.keyword());
		}
		return true;
	}

}
