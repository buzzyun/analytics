package org.fastcatgroup.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;

import org.fastcatgroup.analytics.analysis.AbstractLogAggregator;
import org.fastcatgroup.analytics.analysis.LogValidator;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.log.SearchLogResult;

/**
 * search log를 읽어들여 key-count를 계산한다.
 * */
public class SearchLogKeyCountHandler extends CategoryLogHandler<SearchLog> {

	private AbstractLogAggregator<SearchLog> aggregator;
	LogValidator<SearchLog> logValidator;
	int count;
	int searchCount;
	int maxResponseTime;
	long sumResponseTime;
	
	public SearchLogKeyCountHandler(String categoryId, AbstractLogAggregator<SearchLog> aggregator, LogValidator<SearchLog> logValidator,
			KeyCountRunEntryParser entryParser) {
		super(categoryId);
		this.aggregator = aggregator;
		this.logValidator = logValidator;
	}

	@Override
	public void handleLog(SearchLog logData) throws IOException {
		// logger.debug("handleLog[{}] > {}", categoryId, logData);
		
		String keyword = logData.keyword();
		if (keyword != null && keyword.length() > 0) {
			if (categoryId.equals(logData.categoryId())) {
				// 해당 카테고리만

				if (logValidator != null && logValidator.isValid(logData)) {
					aggregator.handleLog(logData);
				}
				count+= logData.getCount();
				searchCount += logData.getResultCount();
				sumResponseTime += logData.getResponseTime();
				if(logData.getResponseTime() > maxResponseTime){
					maxResponseTime = logData.getResponseTime();
				}
			} else if (categoryId.equals("_root")) {
				// root는 모두다.
				if (logValidator != null && logValidator.isValid(logData)) {
					aggregator.handleLog(logData);
				}
				count+=logData.getCount();
				searchCount += logData.getResultCount();
				sumResponseTime += logData.getResponseTime();
				if(logData.getResponseTime() > maxResponseTime){
					maxResponseTime = logData.getResponseTime();
				}
			}
		}
	}

	@Override
	public Object done() throws IOException {
		aggregator.done();
		return new SearchLogResult(count, searchCount, count > 0 ? (int) (sumResponseTime / count) : 0, maxResponseTime);
	}
}
