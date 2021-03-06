package org.fastcatsearch.analytics.analysis;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.fastcatsearch.analytics.analysis.log.LogData;
import org.fastcatsearch.analytics.analysis.util.AggregationResultFileWriter;
import org.fastcatsearch.analytics.analysis.util.AggregationResultWriter;
import org.fastcatsearch.analytics.analysis.util.KeyCountRunEntry;
import org.fastcatsearch.analytics.analysis.util.RunMerger;
import org.fastcatsearch.analytics.analysis.util.SortedRunFileMerger;

/**
 * 인기검색어를 만들기 위한 handler AbstractLogAggregator를 구현하여 run파일의 위치와 최종 머저를 생성한다.
 * 
 * */
public class KeyCountLogAggregator<LogType extends LogData> extends AbstractLogAggregator<LogType> {

	protected File runTmpDir;
	protected File destFile;
	EntryParser<KeyCountRunEntry> entryParser;
	
	public KeyCountLogAggregator(File targetDir, String fileName, int runKeySize, String outputEncoding, int minimumHitCount, EntryParser<KeyCountRunEntry> entryParser) {
		super(runKeySize, outputEncoding, minimumHitCount);
		this.entryParser = entryParser;
		this.runTmpDir = new File(targetDir, "_run");
		this.destFile = new File(targetDir, fileName);
		destFile.delete();
		
		if (!targetDir.exists()) {
			targetDir.mkdir();
		}
	}
	
	@Override
	protected AggregationResultWriter newRunWriter(String encoding, int flushId) {
		if (!runTmpDir.exists()) {
			runTmpDir.mkdir();
		}
		File file = getRunFile(flushId);
		return new AggregationResultFileWriter(file, encoding);
	}

	@Override
	public RunMerger newFinalMerger(String encoding, int flushCount) {
		File[] runFileList = new File[flushCount];
		for (int i = 0; i < flushCount; i++) {
			runFileList[i] = getRunFile(i);
		}

		AggregationResultWriter writer = new AggregationResultFileWriter(destFile, encoding, minimumHitCount);
		return new SortedRunFileMerger(runFileList, encoding, writer, entryParser);
	}

	@Override
	public void doDone() {
		FileUtils.deleteQuietly(runTmpDir);
	}

	protected File getRunFile(int i) {
		return new File(runTmpDir, Integer.valueOf(i) + ".run");
	}
}
