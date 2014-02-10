package org.fastcatgroup.analytics.analysis.log;

import org.fastcatgroup.analytics.analysis.EntryParser;
import org.fastcatgroup.analytics.analysis.util.KeyCountRunEntry;

public class KeyCountRunEntryParser implements EntryParser<KeyCountRunEntry> {

	@Override
	public KeyCountRunEntry parse(String line) {
		
		String[] el = line.split("\t");
		if (el.length == 2) {
			try {
				return new KeyCountRunEntry(line, el[0], Integer.parseInt(el[1]));
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
