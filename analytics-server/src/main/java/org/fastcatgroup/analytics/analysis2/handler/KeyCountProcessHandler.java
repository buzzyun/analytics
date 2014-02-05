package org.fastcatgroup.analytics.analysis2.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.KeyCountLogAggregator;

/**
 * tmp.log 파일을 key-count로 합산한 0.log로 만든다.
 * */
public class KeyCountProcessHandler extends ProcessHandler {

	private KeyCountLogAggregator handler;

	private File baseDir;
	int fileLimitCount;
	int runKeySize;
	private Set<String> banWords;
	private int minimumHitCount;
	String encoding;

	/**
	 * fileLimitCount : 0.log 1.log .. 의 갯수.
	 * */
	public KeyCountProcessHandler(File baseDir, int fileLimitCount, int runKeySize, Set<String> banWords, int minimumHitCount, String encoding) {
		this.baseDir = baseDir;
		this.fileLimitCount = fileLimitCount;
		this.runKeySize = runKeySize;
		this.banWords = banWords;
		this.minimumHitCount = minimumHitCount;
		this.encoding = encoding;
	}

	@Override
	public void reset() {

	}

	@Override
	public Object process(String categoryId, Object parameter) {

		File categoryDir = new File(baseDir, categoryId);
		rollingPrevFiles(categoryDir);

		File inFile = new File(categoryDir, "tmp.log");
		logger.debug("Process file > {}", inFile);
		String targetFilename = "0.log";

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), encoding));
			handler = new KeyCountLogAggregator(categoryDir, targetFilename, runKeySize, encoding, banWords, minimumHitCount);
			String line = null;
			while ((line = reader.readLine()) != null) {
				handler.handleLog(line);
			}

			handler.done();
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignore) {
				}
			}
		}

		return parameter;
	}

	private void rollingPrevFiles(File categoryDir) {
		File lastFile = getLogFile(categoryDir, fileLimitCount - 1);
		if (lastFile.exists()) {
			lastFile.delete();
		}

		for (int i = fileLimitCount - 1; i >= 0; i--) {
			File file = getLogFile(categoryDir, i);

			if (file.exists()) {
				File destFile = getLogFile(categoryDir, i + 1);
				try {
					FileUtils.moveFile(file, destFile);
				} catch (IOException e) {
					logger.error("", e);
				}
				logger.debug("[{}] rename {} > {}", categoryDir.getName(), file.getName(), destFile.getName());
			}
		}
	}

	private File getLogFile(File dir, int number) {
		return new File(dir, number + ".log");
	}
}
