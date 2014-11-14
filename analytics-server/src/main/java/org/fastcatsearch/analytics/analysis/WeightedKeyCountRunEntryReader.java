package org.fastcatsearch.analytics.analysis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.fastcatsearch.analytics.analysis.util.KeyCountRunEntry;

/**
 * key, count 쌍의 로그파일을 읽을 때 이전통계에 대해서는 decay factor를 적용하기 위해서 count에 1보다 작은 weight를 곱해준다.
 * */
public class WeightedKeyCountRunEntryReader extends FileRunEntryReader<KeyCountRunEntry> {

	private float weight;
	private boolean ignoreZero = false;

	public WeightedKeyCountRunEntryReader(File file, String encoding, float weight, boolean ignoreZero, EntryParser<KeyCountRunEntry> entryParser) throws IOException {
		super(file, encoding, entryParser);
		this.weight = weight;
		this.ignoreZero = ignoreZero;
	}

	public WeightedKeyCountRunEntryReader(InputStream is, String encoding, float weight, boolean ignoreZero, EntryParser<KeyCountRunEntry> entryParser) throws IOException {
		super(is, encoding, entryParser);
		this.weight = weight;
		this.ignoreZero = ignoreZero;
	}

	@Override
	public boolean next() {
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {

				entry = entryParser.parse(line); // exception발생시 종료.
				entry.setCount(Math.round(entry.getCount() * weight));
				if (entry == null) {
					// 파싱실패시 다음 라인확인.
					continue;
				}
				if(ignoreZero && entry.getCount() == 0) {
					continue;
				}
				return true;
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		entry = null;
		return false;
	}
}
