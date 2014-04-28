package org.fastcatsearch.analytics.analysis.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.fastcatsearch.analytics.analysis.FileRunEntryReader;
import org.fastcatsearch.analytics.analysis.util.KeyCountRunEntry;
import org.fastcatsearch.analytics.analysis.util.RunEntryMergeReader;
import org.fastcatsearch.analytics.analysis.util.RunEntryReader;
import org.junit.Test;

public class RunEntryMergeReaderTest {

	@Test
	public void testLogFileMerge() throws IOException {
		String destDir = "src/test/resources/statistics/rt/test";
		
		File[] fileList = new File(destDir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		});

//		KeyCountRunEntryReader[] entryReaderList = getReaderList(fileList);
		List<RunEntryReader<KeyCountRunEntry>> entryReaderList = null;
		RunEntryMergeReader<KeyCountRunEntry> reader = new RunEntryMergeReader<KeyCountRunEntry>(entryReaderList);

		try {
			KeyCountRunEntry entry = null;

			while ((entry = reader.read()) != null) {
				System.out.println(entry);
			}

		} finally {
			for (RunEntryReader r : entryReaderList) {
				r.close();
			}
		}
	}

	private FileRunEntryReader[] getReaderList(File[] fileList) throws IOException {
		FileRunEntryReader[] list = new FileRunEntryReader[fileList.length];
		for (int i = 0; i < fileList.length; i++) {
			File f = fileList[i];
//			list[i] = new FileRunEntryReader(f, "utf-8", entryParser);
			list[i].next();
		}
		return list;
	}
}
