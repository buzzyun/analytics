package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.util.KeyCountRunEntry;
import org.fastcatgroup.analytics.analysis.util.LogSorter;
import org.fastcatgroup.analytics.analysis2.handler.ProcessHandler;

public class KeyCountLogSortHandler extends ProcessHandler {
	private final String KEY_COUNT_RANK_LOG_FILENAME = "key-count-rank.log";
	private final String KEY_COUNT_RANK_PREV_LOG_FILENAME = "key-count-rank-prev.log";

	private File baseDir;
	private String encoding;
	private int runKeySize;

	public KeyCountLogSortHandler(File baseDir, String encoding, int runKeySize) {
		this.baseDir = baseDir;
		this.encoding = encoding;
		this.runKeySize = runKeySize;
	}

	@Override
	public void reset() {

	}

	@Override
	public Object process(String categoryId, Object parameter) throws Exception {
		File keyCountFile = (File) parameter;

		File categoryDir = new File(baseDir, categoryId);
		File rankFile = new File(categoryDir, KEY_COUNT_RANK_LOG_FILENAME);
		File prevRankFile = new File(categoryDir, KEY_COUNT_RANK_PREV_LOG_FILENAME);
		if (rankFile.exists()) {
			if (prevRankFile.exists()) {
				prevRankFile.delete();
			}
			FileUtils.copyFile(rankFile, prevRankFile);
		}

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
		File sortWorkDir = new File(categoryDir, "tmp");
		InputStream is = new FileInputStream(keyCountFile);
		OutputStream os = new FileOutputStream(rankFile);
		try {
			LogSorter logSorter = new LogSorter(is, encoding, runKeySize);
			logSorter.sort(os, comparator, sortWorkDir);
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}

		return new File[] { rankFile, prevRankFile };
	}

}
