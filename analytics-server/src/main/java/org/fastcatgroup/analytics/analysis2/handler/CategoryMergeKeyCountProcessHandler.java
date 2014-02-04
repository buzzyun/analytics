package org.fastcatgroup.analytics.analysis2.handler;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.util.AggregationResultFileWriter;
import org.fastcatgroup.analytics.analysis.util.WeightedSortedRunFileMerger;

/**
 * o.log, 1.log.. 들을 가중치 합산한 key-count.log로 만든다.
 * */
public class CategoryMergeKeyCountProcessHandler extends ProcessHandler {

	private final String KEY_COUNT_LOG_FILENAME = "key-count.log";

	File resultDir;
	int limitCount;
	String encoding;

	public CategoryMergeKeyCountProcessHandler(File resultDir, int limitCount, String encoding) {
		this.resultDir = resultDir;
		this.limitCount = limitCount;
		this.encoding = encoding;
	}

	@Override
	public void reset() {
	}

	@Override
	public Object process(Object parameter) {
		Object[] params = (Object[]) parameter;
		File baseDir = (File) params[0];
		Set<String> categoryIdSet = (Set<String>) params[1];

		for (String categoryId : categoryIdSet) {
			File categoryDir = new File(baseDir, categoryId);
			File[] inFileList = new File[limitCount];
			float[] weightList = new float[limitCount];
			for (int i = 0; i < limitCount; i++) {
				inFileList[i] = new File(categoryDir, i + ".log");
				weightList[i] = (float) (limitCount - i) / (float) limitCount;
			}
			File targetDir = new File(resultDir, categoryId);

			try {

				if (inFileList == null || inFileList.length == 0) {
					logger.warn("skip making realtime popular keyword due to no working log files at {}");
					return null;
				}

				if (!targetDir.exists()) {
					targetDir.mkdirs();
				}
				File keyCountFile = new File(targetDir, KEY_COUNT_LOG_FILENAME);
				AggregationResultFileWriter writer = new AggregationResultFileWriter(keyCountFile, encoding);
				WeightedSortedRunFileMerger merger = new WeightedSortedRunFileMerger(inFileList, weightList, encoding, writer);
				merger.merge();
			} catch (IOException e) {
				logger.error("", e);
			} finally {
			}
		}

		return new Object[] { resultDir, categoryIdSet };
	}

}
