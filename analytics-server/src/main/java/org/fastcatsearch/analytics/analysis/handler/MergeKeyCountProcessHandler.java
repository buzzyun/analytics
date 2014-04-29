package org.fastcatsearch.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;

import org.fastcatsearch.analytics.analysis.EntryParser;
import org.fastcatsearch.analytics.analysis.util.AggregationResultFileWriter;
import org.fastcatsearch.analytics.analysis.util.KeyCountRunEntry;
import org.fastcatsearch.analytics.analysis.util.WeightedSortedRunFileMerger;

public class MergeKeyCountProcessHandler extends ProcessHandler {

	private File resultDir;
	private String outFileName;
	private String encoding;
	private EntryParser<KeyCountRunEntry> entryParser;
	private File[] inFileList;
	private float[] weightList;
	
	public MergeKeyCountProcessHandler(File[] inFileList, File resultDir, String outFileName, String encoding, EntryParser<KeyCountRunEntry> entryParser) {
		this(inFileList, null, resultDir, outFileName, encoding, entryParser);
	}
	public MergeKeyCountProcessHandler(File[] inFileList, float[] weightList, File resultDir, String outFileName, String encoding, EntryParser<KeyCountRunEntry> entryParser) {
		this.resultDir = resultDir;
		this.outFileName = outFileName;
		this.encoding = encoding;
		this.entryParser = entryParser;
		this.inFileList = inFileList;
		this.weightList = weightList;
	}
	
	@Override
	public Object process(Object parameter) {
		//logger.debug("start process.. ");
		File keyCountFile = new File(resultDir, outFileName);
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
