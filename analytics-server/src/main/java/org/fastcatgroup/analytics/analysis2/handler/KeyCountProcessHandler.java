package org.fastcatgroup.analytics.analysis2.handler;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.KeyCountLogAggregator;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis2.FileSearchLogReader;

/**
 * tmp.log 파일을 key-count로 합산한 0.log로 만든다.
 * */
public class KeyCountProcessHandler extends ProcessHandler {

	private KeyCountLogAggregator merger;

	private File baseDir;
	String inFileName;
	String outFileName;
	int runKeySize;
	private Set<String> banWords;
	private int minimumHitCount;
	String encoding;

	/**
	 * fileLimitCount : 0.log 1.log .. 의 갯수.
	 * */
	public KeyCountProcessHandler(File baseDir, String inFileName, String outFileName, int runKeySize, Set<String> banWords, int minimumHitCount, String encoding) {
		this.baseDir = baseDir;
		this.inFileName = inFileName;
		this.outFileName = outFileName;
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

		File inFile = new File(categoryDir, inFileName);
		logger.debug("Process file > {}", inFile);

		FileSearchLogReader reader = null;
		try {
			reader = new FileSearchLogReader(inFile, encoding);
			merger = new KeyCountLogAggregator(categoryDir, outFileName, runKeySize, encoding, banWords, minimumHitCount);
			SearchLog log = null;
			while ((log = reader.readLog()) != null) {
				merger.handleLog(log);
			}

			merger.done();
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return parameter;
	}

}