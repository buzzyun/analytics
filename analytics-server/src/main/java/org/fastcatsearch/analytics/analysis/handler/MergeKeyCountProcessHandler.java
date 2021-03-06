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
	private boolean ignoreZero;
	
	public MergeKeyCountProcessHandler(File[] inFileList, File resultDir, String outFileName, String encoding, boolean ignoreZero, EntryParser<KeyCountRunEntry> entryParser) {
		this(inFileList, null, resultDir, outFileName, encoding, ignoreZero, entryParser);
	}
	public MergeKeyCountProcessHandler(File[] inFileList, float[] weightList, File resultDir, String outFileName, String encoding, boolean ignoreZero, EntryParser<KeyCountRunEntry> entryParser) {
		this.resultDir = resultDir;
		this.outFileName = outFileName;
		this.encoding = encoding;
		this.entryParser = entryParser;
		this.inFileList = inFileList;
		this.weightList = weightList;
		this.ignoreZero = ignoreZero;
	}
	
	@Override
	public Object process(Object parameter) {
		//logger.debug("start process.. ");
		File keyCountFile = new File(resultDir, outFileName);
		AggregationResultFileWriter writer = null;
		WeightedSortedRunFileMerger merger = null;
		try {

			if (inFileList == null || inFileList.length == 0) {
				logger.warn("skip making keyword process due to no working log files at {}");
				return null;
			}

			if (!resultDir.exists()) {
				resultDir.mkdirs();
			}
			
			logger.debug("writing key-count file : {}", keyCountFile);
			
			writer =new AggregationResultFileWriter(keyCountFile, encoding);
			merger = new WeightedSortedRunFileMerger(inFileList, weightList, encoding, ignoreZero, writer, entryParser);
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
