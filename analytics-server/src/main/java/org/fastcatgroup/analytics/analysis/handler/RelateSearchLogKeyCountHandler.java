package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.RelateKeyCountLogAggregator;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.log.RelateKeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.log.RelateSearchLog;

/**
 * search log를 읽어들여 연관검색어의 key:prevKey의 count를 계산한다.
 * */
public class RelateSearchLogKeyCountHandler extends CategoryLogHandler<RelateSearchLog> {
	
	private RelateKeyCountLogAggregator aggregator;
	
	public RelateSearchLogKeyCountHandler(String categoryId, File baseDir, String targetFilename, Set<String> banWords, int minimumHitCount, RelateKeyCountRunEntryParser entryParser) {
		super(categoryId);
		int runKeySize = SearchStatisticsProperties.runKeySize;
		String encoding = SearchStatisticsProperties.encoding;
		aggregator = new RelateKeyCountLogAggregator(baseDir, targetFilename, runKeySize, encoding, banWords, minimumHitCount, entryParser);
	}

	@Override
	public void handleLog(RelateSearchLog logData) throws IOException {
//		logger.debug("handleLog[{}] > {}", categoryId, logData);
		String keyword = logData.keyword();
		String previousKeyword = logData.previousKeyword();
		if (keyword != null && keyword.length() > 0 && previousKeyword != null && previousKeyword.length() > 0) {
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
