package org.fastcatgroup.analytics.analysis.log;

import org.fastcatgroup.analytics.analysis.EntryParser;
import org.fastcatgroup.analytics.analysis.util.KeyCountRunEntry;

public class KeyCountRunEntryParser implements EntryParser<KeyCountRunEntry> {
	
	int[] keyIndex;
	int countIndex;
	
	public KeyCountRunEntryParser() {
		this(new int[] {0}, 1);
	}
	
	public KeyCountRunEntryParser(int[] keyIndex, int countIndex) {
		this.keyIndex = keyIndex;
		this.countIndex = countIndex;
	}

	@Override
	public KeyCountRunEntry parse(String line) {
		
		String[] el = line.split("\t");
		if (el.length >= 2) {
			try {
				//logger.debug("KeyCount parsed {}, {}", el[0], el[1]);
				
				String key = "";
				for (int inx = 0; inx < keyIndex.length; inx++) {
					if (inx > 0) {
						key += "\t";
					}
					key += el[keyIndex[inx]];
				}
				logger.trace(">>>>>el {} {} / key:{} / {}", "", el, key, keyIndex);
				return new KeyCountRunEntry(line, key, Integer.parseInt(el[countIndex]));
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
