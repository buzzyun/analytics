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
 * 인기검색어를 만들기 위한 handler LogAggregateHandler를 구현하여 run파일의 위치와 최종 머저를 생성한다.
 * 
 * */
public class PopularKeywordLogAggregateHandler extends LogAggregateHandler<SearchLog> {

	private File runTmpDir;
	private File destFile;
	public PopularKeywordLogAggregateHandler(File targetDir, String fileName, int runKeySize, String outputEncoding, Set<String> banWords, int minimumHitCount) {
		super(new SearchLogParser(), runKeySize, outputEncoding, banWords, minimumHitCount);
		this.runTmpDir = new File(targetDir, "_run");
		this.destFile = new File(targetDir, fileName);
		
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

//	@Override
//	protected boolean checkNeedMerge(int flushCount) {
//		if (flushCount == 1) {
//			File srcFile = getRunFile(0);
//			if(destFile.exists()){
//				destFile.delete();
//			}
//			try {
//				FileUtils.moveFile(srcFile, destFile);
//			} catch (IOException e) {
//				logger.error("", e);
//			}
//		}
//		
//		//flush가 1번이거나 없으면, 머징하지 않는다.
//		return flushCount > 1;
//	}

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
