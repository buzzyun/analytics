package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.log.LogData;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.util.AggregationResultFileWriter;
import org.fastcatgroup.analytics.analysis.util.AggregationResultWriter;
import org.fastcatgroup.analytics.analysis.util.RunMerger;
import org.fastcatgroup.analytics.analysis.util.SortedRunFileMerger;

/**
 * 인기검색어를 만들기 위한 handler AbstractLogAggregator를 구현하여 run파일의 위치와 최종 머저를 생성한다.
 * 
 * */
public class KeyCountLogAggregator extends AbstractLogAggregator<SearchLog> {

	private File runTmpDir;
	private File destFile;
	
	public KeyCountLogAggregator(File targetDir, String targetFilename, int runKeySize, String outputEncoding, Set<String> banWords, int minimumHitCount) {
		super(runKeySize, outputEncoding, banWords, minimumHitCount);
		this.runTmpDir = new File(targetDir, "_run");
		this.destFile = new File(targetDir, targetFilename);
		
		if (!targetDir.exists()) {
			targetDir.mkdir();
		}
		if (!runTmpDir.exists()) {
			runTmpDir.mkdir();
		}
	}

	@Override
	protected AggregationResultWriter newRunWriter(String encoding, int flushId) {
		File file = getRunFile(flushId);
		return new AggregationResultFileWriter(file, encoding);
	}

	@Override
	protected RunMerger newFinalMerger(String encoding, int flushCount) {

		File[] runFileList = new File[flushCount];
		for (int i = 0; i < flushCount; i++) {
			runFileList[i] = getRunFile(i);
		}

		AggregationResultWriter writer = new AggregationResultFileWriter(destFile, encoding, minimumHitCount);
		return new SortedRunFileMerger(runFileList, encoding, writer);
	}

	@Override
	protected void doDone() {
		FileUtils.deleteQuietly(runTmpDir);
	}

	private File getRunFile(int i) {
		return new File(runTmpDir, Integer.valueOf(i) + ".run");
	}
}
