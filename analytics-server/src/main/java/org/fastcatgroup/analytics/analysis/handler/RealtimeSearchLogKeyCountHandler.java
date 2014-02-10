package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.KeyCountLogAggregator;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.log.SearchLog;

/**
 * search log를 읽어들여 카테고리별 0.log를 만든다.
 * */
public class RealtimeSearchLogKeyCountHandler extends CategoryLogHandler<SearchLog> {

	private KeyCountLogAggregator<SearchLog> aggregator;

	public RealtimeSearchLogKeyCountHandler(String categoryId, File storeDir, String targetFilename, Set<String> banWords, int minimumHitCount, int realtimeSearchLogLimit, KeyCountRunEntryParser entryParser) {
		super(categoryId);
		if (!storeDir.exists()) {
			storeDir.mkdirs();
		}
		rollingStoredLogs(storeDir, realtimeSearchLogLimit);
		int runKeySize = SearchStatisticsProperties.runKeySize;
		String encoding = SearchStatisticsProperties.encoding;
		aggregator = new KeyCountLogAggregator<SearchLog>(storeDir, targetFilename, runKeySize, encoding, banWords, minimumHitCount, entryParser);
	}

	@Override
	public void handleLog(SearchLog logData) throws IOException {
		logger.debug("handleLog[{}] > {}", categoryId, logData);
		String keyword = logData.keyword();
		if (keyword != null && keyword.length() > 0) {
			if (categoryId.equals(logData.categoryId())) {
				// 해당 카테고리만
				aggregator.handleLog(logData);
			} else if (categoryId.equals("_root")) {
				// root는 모두다.
				aggregator.handleLog(logData);
			}

		}
	}

	@Override
	public Object done() throws IOException {
		aggregator.done();
		return null;
	}

	//0.log, 1.log.. 를 각각 1.log, 2.log.. 로 옮긴다.
	private void rollingStoredLogs(File storeDir, int realtimeSearchLogLimit) {
		File f = new File(storeDir, (realtimeSearchLogLimit - 1) + ".log");
		if (f.exists()) {
			FileUtils.deleteQuietly(f);
		}
		for (int i = realtimeSearchLogLimit - 2; i >= 0; i--) {
			File srcFile = new File(storeDir, i + ".log");
			if (srcFile.exists()) {
				File destFile = new File(storeDir, (i + 1) + ".log");
				if (destFile.exists()) {
					FileUtils.deleteQuietly(f);
				}
				try {
					FileUtils.moveFile(srcFile, destFile);
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}
	
}
