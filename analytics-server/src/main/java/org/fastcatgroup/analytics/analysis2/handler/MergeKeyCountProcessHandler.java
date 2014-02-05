package org.fastcatgroup.analytics.analysis2.handler;

import java.io.File;
import java.io.IOException;

import org.fastcatgroup.analytics.analysis.util.AggregationResultFileWriter;
import org.fastcatgroup.analytics.analysis.util.WeightedSortedRunFileMerger;

/**
 * o.log, 1.log.. 들을 가중치 합산한 key-count.log로 만든다.
 * */
public class MergeKeyCountProcessHandler extends ProcessHandler {

	private final String KEY_COUNT_LOG_FILENAME = "key-count.log";
	File baseDir;
	File resultDir;
	int fileLimitCount;
	String encoding;

	public MergeKeyCountProcessHandler(File baseDir, File resultDir, int fileLimitCount, String encoding) {
		this.baseDir = baseDir;
		this.resultDir = resultDir;
		this.fileLimitCount = fileLimitCount;
		this.encoding = encoding;
	}

	@Override
	public void reset() {
	}

	@Override
	public Object process(String categoryId, Object parameter) {
		File categoryDir = new File(baseDir, categoryId);
		File[] inFileList = new File[fileLimitCount];
		float[] weightList = new float[fileLimitCount];
		for (int i = 0; i < fileLimitCount; i++) {
			inFileList[i] = new File(categoryDir, i + ".log");
			weightList[i] = (float) (fileLimitCount - i) / (float) fileLimitCount;
		}
		File targetDir = new File(resultDir, categoryId);
		File keyCountFile = new File(targetDir, KEY_COUNT_LOG_FILENAME);
		try {

			if (inFileList == null || inFileList.length == 0) {
				logger.warn("skip making realtime popular keyword due to no working log files at {}");
				return null;
			}

			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}
			
			AggregationResultFileWriter writer = new AggregationResultFileWriter(keyCountFile, encoding);
			WeightedSortedRunFileMerger merger = new WeightedSortedRunFileMerger(inFileList, weightList, encoding, writer);
			merger.merge();
		} catch (IOException e) {
			logger.error("", e);
		} finally {
		}

		return keyCountFile;
	}

}
