package org.fastcatgroup.analytics.analysis.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fastcatgroup.analytics.analysis.EntryParser;
import org.fastcatgroup.analytics.analysis.WeightedKeyCountRunEntryReader;

/**
 * decay factor를 적용한 머징로그파일을 만들기 위한 클래스. 예) 0.log * 1.0 + 1.log * 0.9 + 2.log * 0.8 + ... SortedRunFileMerger를 상속받아 구현함.
 * */
public class WeightedSortedRunFileMerger extends SortedRunFileMerger {

	private float[] weightList;

	public WeightedSortedRunFileMerger(File[] runFileList, float[] weightList, String encoding, AggregationResultWriter writer, EntryParser<KeyCountRunEntry> entryParser) {
		super(runFileList, encoding, writer, entryParser);
		this.weightList = weightList;
	}

	@Override
	protected List<RunEntryReader<KeyCountRunEntry>> getReaderList(File[] fileList) throws IOException {
		List<RunEntryReader<KeyCountRunEntry>> list = new ArrayList<RunEntryReader<KeyCountRunEntry>>();
		for (int i = 0; i < fileList.length; i++) {
			File f = fileList[i];
			if (f.exists()) {
				float weight = 1;
				if(weightList!=null) {
					weight = weightList[i];
				}
				WeightedKeyCountRunEntryReader r = new WeightedKeyCountRunEntryReader(f, encoding, weight, entryParser);
				r.next();
				list.add(r);
			}
		}
		return list;
	}
}
