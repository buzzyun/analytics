package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;

import org.fastcatgroup.analytics.analysis.EntryParser;
import org.fastcatgroup.analytics.analysis.util.KeyCountRunEntry;
import org.fastcatgroup.analytics.analysis.util.LogSorter;

public class KeyCountLogSortHandler extends ProcessHandler {

	private File baseDir;
	String inFileName;
	String sortedFileName;
	private String encoding;
	private int runKeySize;
	EntryParser<KeyCountRunEntry> entryParser;
	
	public KeyCountLogSortHandler(File baseDir, String inFileName, String sortedFileName, String encoding, int runKeySize, EntryParser<KeyCountRunEntry> entryParser) {
		this.baseDir = baseDir;
		this.inFileName = inFileName;
		this.sortedFileName = sortedFileName;
		this.encoding = encoding;
		this.runKeySize = runKeySize;
		this.entryParser = entryParser;
	}

	@Override
	public Object process(Object parameter) throws Exception {
		logger.debug("process {} [{}]", getClass().getSimpleName(), parameter);
		File keyCountFile = new File(baseDir, inFileName);
		File rankFile = new File(baseDir, sortedFileName);

		// count 내림차순정렬을 위한 comparator
		Comparator<KeyCountRunEntry> comparator = new Comparator<KeyCountRunEntry>() {

			@Override
			public int compare(KeyCountRunEntry o1, KeyCountRunEntry o2) {
				if (o1 == null && o2 == null) {
					return 0;
				} else if (o1 == null) {
					return -1;
				} else if (o2 == null) {
					return 1;
				}

				// 내림차순 정렬.
				return o2.getCount() - o1.getCount();
			}

		};

		// LogSorter를 사용해 keyCountFile -> rankFile 로 저장한다.
		File sortWorkDir = new File(baseDir, "tmp");
		InputStream is = new FileInputStream(keyCountFile);
		OutputStream os = new FileOutputStream(rankFile);
		try {
			LogSorter<KeyCountRunEntry> logSorter = new LogSorter<KeyCountRunEntry>(is, encoding, runKeySize);
			logSorter.sort(os, entryParser, comparator, sortWorkDir);
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}

		return null;
	}

}
