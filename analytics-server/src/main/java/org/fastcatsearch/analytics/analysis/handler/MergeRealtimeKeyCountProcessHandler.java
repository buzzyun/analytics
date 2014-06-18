package org.fastcatsearch.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;

import org.fastcatsearch.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.util.AggregationResultFileWriter;
import org.fastcatsearch.analytics.analysis.util.WeightedSortedRunFileMerger;

/**
 * o.log, 1.log.. 들을 가중치 합산한 key-count.log로 만든다.
 * */
public class MergeRealtimeKeyCountProcessHandler extends ProcessHandler {

	File baseDir;
	File resultDir;
	String outFileName;
	int fileLimitCount;
	String encoding;
	KeyCountRunEntryParser entryParser;
	
	public MergeRealtimeKeyCountProcessHandler(File baseDir, File resultDir, String outFileName, String encoding, int fileLimitCount, KeyCountRunEntryParser entryParser) {
		this.baseDir = baseDir;
		this.resultDir = resultDir;
		this.outFileName = outFileName;
		this.fileLimitCount = fileLimitCount;
		this.encoding = encoding;
		this.entryParser = entryParser;
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
		AggregationResultFileWriter writer = null;
		WeightedSortedRunFileMerger merger = null;
		try {

			if (inFileList == null || inFileList.length == 0) {
				logger.warn("skip making realtime popular keyword due to no working log files at {}");
				return null;
			}

			if (!resultDir.exists()) {
				resultDir.mkdirs();
			}
			
			writer = new AggregationResultFileWriter(keyCountFile, encoding);
			merger = new WeightedSortedRunFileMerger(inFileList, weightList, encoding, writer, entryParser);
			merger.merge();
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if(merger!=null) {
				merger.close();
			}
			if(writer!=null) try {
				writer.close();
			} catch (Exception ignore) { }
		}

		return null;
	}

}
