package org.fastcatsearch.analytics.util;

import java.lang.Character.UnicodeBlock;

public class CharacterUtils {
	
	public static boolean isValidCharacter(String str) {
		for(int inx=0;inx< str.length(); inx++) {
			char c = str.charAt(inx);
			if(c >= 32 && c <= 126) {
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
}
