package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.util.AggregationResultFileWriter;
import org.fastcatgroup.analytics.analysis.util.WeightedSortedRunFileMerger;

public class MergeKeyCountProcessHandler extends ProcessHandler {

	File resultDir;
	String outFileName;
	int fileLimitCount;
	String encoding;
	KeyCountRunEntryParser entryParser;
	File[] inFileList;
	
	public MergeKeyCountProcessHandler(File[] inFileList, File resultDir, String outFileName, String encoding, KeyCountRunEntryParser entryParser) {
		this.resultDir = resultDir;
		this.outFileName = outFileName;
		this.encoding = encoding;
		this.entryParser = entryParser;
		this.inFileList = inFileList;
	}

	@Override
	public Object process(Object parameter) {
		//logger.debug("start process.. ");
		
		File keyCountFile = new File(resultDir, outFileName);
		
		float[] weightList = new float[inFileList.length];
		Arrays.fill(weightList, 1.0f);
		
		try {

			if (inFileList == null || inFileList.length == 0) {
				logger.warn("skip making keyword process due to no working log files at {}");
				return null;
			}

			if (!resultDir.exists()) {
				resultDir.mkdirs();
			}
			
			logger.debug("writing key-count file : {}", keyCountFile);
			
			AggregationResultFileWriter writer = new AggregationResultFileWriter(keyCountFile, encoding);
			WeightedSortedRunFileMerger merger = new WeightedSortedRunFileMerger(inFileList, weightList, encoding, writer, entryParser);
			merger.merge();
		} catch (IOException e) {
			logger.error("", e);
		} finally {
		}

		return null;
	}

}
