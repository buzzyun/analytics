package org.fastcatsearch.analytics.analysis.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fastcatsearch.analytics.analysis.EntryParser;
import org.fastcatsearch.analytics.analysis.FileRunEntryReader;
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
	EntryParser<KeyCountRunEntry> entryParser;
	
	public SortedRunFileMerger(File[] runFileList, String encoding, AggregationResultWriter writer, EntryParser<KeyCountRunEntry> entryParser) {
		this.runFileList = runFileList;
		this.encoding = encoding;
		this.writer = writer;
		this.entryParser = entryParser;
	}

	@Override
	public void merge() throws IOException {
		try{
			List<RunEntryReader<KeyCountRunEntry>> entryReaderList = getReaderList(runFileList);
			if (entryReaderList.size() > 0) {
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
					
					logger.debug("Wrote merge file {}", writer);
				}
			}else{
				logger.debug("no file to merge");
			}
		} finally {
			writer.close();
		}

	}

	protected List<RunEntryReader<KeyCountRunEntry>> getReaderList(File[] fileList) throws IOException {
		List<RunEntryReader<KeyCountRunEntry>> list = new ArrayList<RunEntryReader<KeyCountRunEntry>>();
		for (int i = 0; i < fileList.length; i++) {
			File f = fileList[i];
			if (f.exists()) {
				FileRunEntryReader<KeyCountRunEntry> r = new FileRunEntryReader<KeyCountRunEntry>(f, encoding, entryParser);
				r.next();
				list.add(r);
			}
		}
		return list;
	}

}
