package org.fastcatgroup.analytics.analysis;

import java.lang.Character.UnicodeBlock;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.log.RelateSearchLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelateSearchLogValidator implements LogValidator<RelateSearchLog> {
	
	private static final Logger logger = LoggerFactory.getLogger(RelateSearchLogValidator.class);
	
	Set<String> banWords;
	
	public RelateSearchLogValidator(Set<String> banWords) {
		this.banWords = banWords;
	}

	@Override
	public boolean isValid(RelateSearchLog logData) {
		
		
		if (banWords != null) {
			for (String banWord : banWords) {
				if (logData.keyword() == null
						|| logData.keyword().contains(banWord)
						|| logData.previousKeyword() == null
						|| logData.previousKeyword().contains(banWord)) {
					// 금지어의 경우 로그에 기록하지 않는다.
					logger.trace("keyword:[{}:{}] not avail", logData.keyword(), logData.previousKeyword());
					return false;
				}
			}
		}
		// 표현할 수 없는 특수문자는 기록하지 않는다.
		if( !isAvailCharacter(logData.keyword().trim()) ||
				!isAvailCharacter(logData.previousKeyword().trim()) ) {
			logger.trace("keyword:[{}:{}] not avail", logData.keyword(), logData.previousKeyword());
			return false;
		}
		// 원본과 대상이 같으면 기록하지 않는다.
		//if(logData.keyword().trim().equalsIgnoreCase(logData.previousKeyword().trim())) {
		if(logData.keyword().trim().equals(logData.previousKeyword().trim())) {
			logger.trace("keyword:[{}:{}] not avail", logData.keyword(), logData.previousKeyword());
			return false;
		}
		logger.trace("keyword:[{}] / prev:[{}]",logData.keyword(), logData.previousKeyword());
		return true;
	}
	
	private boolean isAvailCharacter(String str) {
		for(int inx=0;inx< str.length(); inx++) {
			char c = str.charAt(inx);
			if(c > 0 && c < 127) {
				continue;
			}
			
			int type = Character.getType(c);
			
			switch(type){
			case Character.DASH_PUNCTUATION:
			case Character.START_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.CONNECTOR_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.MATH_SYMBOL:
			case Character.CURRENCY_SYMBOL:
			case Character.MODIFIER_SYMBOL:
			case Character.OTHER_SYMBOL:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
				continue;
			}
			UnicodeBlock uc = UnicodeBlock.of(c);
			
			if (uc == UnicodeBlock.HANGUL_SYLLABLES
					|| uc == UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
					|| uc == UnicodeBlock.HANGUL_JAMO
					|| uc == UnicodeBlock.HIRAGANA
					|| uc == UnicodeBlock.KATAKANA
					|| uc == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
					) {
				continue;
			}
			
			return false;
		}
		return true;
	}
	
//	public static void main(String[] arg) throws Exception {
//		
//		RelateSearchLogValidator rslv = new RelateSearchLogValidator(null);
//		RelateSearchLog logData = new RelateSearchLog("","[하하]","[TS]");
//		boolean ret = rslv.isValid(logData);
//		
//		logger.trace("ret : {}", ret);
//	}
}
