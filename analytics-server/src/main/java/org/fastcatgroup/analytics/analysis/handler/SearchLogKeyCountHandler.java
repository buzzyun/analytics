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
	
	public SearchLogKeyCountHandler(String categoryId, File baseDir, String targetFilename, Set<String> banWords, int minimumHitCount) {
		super(categoryId);
		int runKeySize = SearchStatisticsProperties.runKeySize;
		String encoding = SearchStatisticsProperties.encoding;
		aggregator = new KeyCountLogAggregator(baseDir, targetFilename, runKeySize, encoding, banWords, minimumHitCount);
	}

	@Override
	public void handleLog(SearchLog logData) throws IOException {
//		logger.debug("handleLog[{}] > {}", categoryId, logData);
		String keyword = logData.keyword();
		if (keyword != null && keyword.length() > 0) {
			if(categoryId.equals(logData.categoryId())){
				//해당 카테고리만 
				aggregator.handleLog(logData);
			}else if(categoryId.equals("_root")){
				//root는 모두다.
				aggregator.handleLog(logData);
			}
			
		}
	}

	@Override
	public Object done() throws IOException {
		aggregator.done();
		return null;
	}

}
