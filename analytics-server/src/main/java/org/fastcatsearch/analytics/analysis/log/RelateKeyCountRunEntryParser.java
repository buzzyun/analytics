package org.fastcatsearch.analytics.analysis.log;

import org.fastcatsearch.analytics.analysis.EntryParser;
import org.fastcatsearch.analytics.analysis.util.KeyCountRunEntry;

public class RelateKeyCountRunEntryParser implements EntryParser<KeyCountRunEntry> {

	@Override
	public KeyCountRunEntry parse(String line) {
		
		String[] el = line.split("\t");
		if (el.length >= 3) {
			try {
				//1,2컬럼은 검색어, 3컬럼이 count.
				logger.trace("RelateKeyCount parsed {}-{}, {}", el[0], el[1], el[2]);
				return new KeyCountRunEntry(line, el[0] + "\t" + el[1], Integer.parseInt(el[2]));
			} catch (Exception e) {
				logger.error("", e);
			}
			return null;
		} else {
			// 파싱실패시 다음 라인확인.
			return null;
		}
		
		
	}
	
}
