package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;

import org.fastcatgroup.analytics.analysis.util.AggregationResultFileWriter;
import org.fastcatgroup.analytics.analysis.util.WeightedSortedRunFileMerger;

/**
 * o.log, 1.log.. 들을 가중치 합산한 key-count.log로 만든다.
 * */
public class MergeKeyCountProcessHandler extends ProcessHandler {

	File baseDir;
	File resultDir;
	String outFileName;
	int fileLimitCount;
	String encoding;

	public MergeKeyCountProcessHandler(File baseDir, File resultDir, String outFileName, String encoding, int fileLimitCount) {
		this.baseDir = baseDir;
		this.resultDir = resultDir;
		this.outFileName = outFileName;
		this.fileLimitCount = fileLimitCount;
		this.encoding = encoding;
	}

	@Override
	public Object process(Object parameter) {
		File[] inFileList = new File[fileLimitCount];
		float[] weightList = new float[fileLimitCount];
		for (int i = 0; i < fileLimitCount; i++) {
			inFileList[i] = new File(baseDir, i + ".log");
			weightList[i] = (float) (fileLimitCount - i) / (float) fileLimitCount;
		}
		
		File keyCountFile = new File(resultDir, outFileName);
		try {

			if (inFileList == null || inFileList.length == 0) {
				logger.warn("skip making realtime popular keyword due to no working log files at {}");
				return null;
			}

			if (!resultDir.exists()) {
				resultDir.mkdirs();
			}
			
			AggregationResultFileWriter writer = new AggregationResultFileWriter(keyCountFile, encoding);
			WeightedSortedRunFileMerger merger = new WeightedSortedRunFileMerger(inFileList, weightList, encoding, writer);
			merger.merge();
		} catch (IOException e) {
			logger.error("", e);
		} finally {
		}

		return null;
	}

}
