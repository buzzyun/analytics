package org.fastcatgroup.analytics.analysis.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fastcatgroup.analytics.analysis.KeyCountRunEntryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 정렬되어 기록된 여러개의 RUN 파일을 하나로 머징한다.
 * */
public class SortedRunFileMerger implements RunMerger {

	protected static Logger logger = LoggerFactory.getLogger(SortedRunFileMerger.class);

	private File[] runFileList;
	private AggregationResultWriter writer;
	protected String encoding;

	public SortedRunFileMerger(File[] runFileList, String encoding, AggregationResultWriter writer) {
		this.runFileList = runFileList;
		this.encoding = encoding;
		this.writer = writer;
	}

	@Override
	public void merge() throws IOException {
		List<RunEntryReader<KeyCountRunEntry>> entryReaderList = getReaderList(runFileList);
		RunEntryMergeReader<KeyCountRunEntry> reader = new RunEntryMergeReader<KeyCountRunEntry>(entryReaderList);

		try {
			KeyCountRunEntry entry = null;

			while ((entry = reader.read()) != null) {
				writer.write(entry.getKey(), entry.getCount());
			}

		} finally {
			for (RunEntryReader<KeyCountRunEntry> r : entryReaderList) {
				r.close();
			}

			writer.close();
		}

	}

	protected List<RunEntryReader<KeyCountRunEntry>> getReaderList(File[] fileList) throws IOException {
		List<RunEntryReader<KeyCountRunEntry>> list = new ArrayList<RunEntryReader<KeyCountRunEntry>>();
		for (int i = 0; i < fileList.length; i++) {
			File f = fileList[i];
			if (f.exists()) {
				KeyCountRunEntryReader r = new KeyCountRunEntryReader(f, encoding);
				r.next();
				list.add(r);
			}
		}
		return list;
	}

}
