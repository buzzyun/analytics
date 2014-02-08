package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.KeyCountLogAggregator;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.log.SearchLog;

/**
 * search log를 읽어들여 key-count를 계산한다.
 * */
public class SearchLogKeyCountHandler extends CategoryLogHandler<SearchLog> {
	
	private KeyCountLogAggregator aggregator;
	
	public SearchLogKeyCountHandler(String categoryId, File baseDir, Set<String> banWords, int minimumHitCount) {
		super(categoryId);
		File categoryDir = new File(baseDir, categoryId);
		String targetFilename = SearchStatisticsProperties.KEY_COUNT_LOG_FILENAME;
		int runKeySize = SearchStatisticsProperties.runKeySize;
		String encoding = SearchStatisticsProperties.encoding;
		aggregator = new KeyCountLogAggregator(categoryDir, targetFilename, runKeySize, encoding, banWords, minimumHitCount);
	}

	@Override
	public void handleLog(SearchLog logData) throws IOException {
		String keyword = logData.keyword();
		if (keyword != null && keyword.length() > 0) {
			aggregator.handleLog(logData);
		}
	}

	@Override
	public void reset() {
	}

	@Override
	public Object done() throws IOException {
		aggregator.done();
		return null;
	}

}
