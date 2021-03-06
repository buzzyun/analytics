package org.fastcatsearch.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;

import org.fastcatsearch.analytics.analysis.KeyCountLogAggregator;
import org.fastcatsearch.analytics.analysis.LogValidator;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.log.RelateKeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.log.RelateSearchLog;

/**
 * search log를 읽어들여 연관검색어의 key:prevKey의 count를 계산한다.
 * */
public class RelateSearchLogKeyCountHandler extends CategoryLogHandler<RelateSearchLog> {

	private KeyCountLogAggregator<RelateSearchLog> aggregator;
	LogValidator<RelateSearchLog> logValidator;
	
	public RelateSearchLogKeyCountHandler(String categoryId, File baseDir, String targetFilename, int minimumHitCount, LogValidator<RelateSearchLog> logValidator,
			RelateKeyCountRunEntryParser entryParser) {
		super(categoryId);
		int runKeySize = StatisticsProperties.runKeySize;
		String encoding = StatisticsProperties.encoding;
		aggregator = new KeyCountLogAggregator<RelateSearchLog>(baseDir, targetFilename, runKeySize, encoding, minimumHitCount, entryParser);
		this.logValidator = logValidator;
	}

	@Override
	public void handleLog(RelateSearchLog logData) throws IOException {
		 logger.trace("handleLog[{}] > {}", categoryId, logData);
		String keyword = logData.keyword();
		String previousKeyword = logData.previousKeyword();
		if (keyword != null && keyword.length() > 0 && previousKeyword != null && previousKeyword.length() > 0) {
			if (categoryId.equals(logData.categoryId())) {
				// 해당 카테고리만
				if (logValidator != null && logValidator.isValid(logData)) {
					aggregator.handleLog(logData);
				}
			} else if (categoryId.equals("_root")) {
				// root는 모두다.
				if (logValidator != null && logValidator.isValid(logData)) {
					aggregator.handleLog(logData);
				}
			}

		}
	}

	@Override
	public Object done() throws IOException {
		aggregator.done();
		return null;
	}

}
