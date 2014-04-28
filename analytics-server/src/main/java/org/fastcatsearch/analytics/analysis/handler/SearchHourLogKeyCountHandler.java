package org.fastcatsearch.analytics.analysis.handler;

import java.io.File;
import java.io.IOException;

import org.fastcatsearch.analytics.analysis.LogValidator;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.analysis.log.SearchLogResult;

/**
 * search log를 읽어들여 key-count를 계산한다.
 * */
public class SearchHourLogKeyCountHandler extends CategoryLogHandler<SearchLog> {

	LogValidator<SearchLog> logValidator;
	int searchCount;
	private int[] searchLogMaxTime;
	private int[] searchLogSumTime;
	private int[] searchLogCount;
	private int[] searchResultCount;
	
	public SearchHourLogKeyCountHandler(String categoryId, LogValidator<SearchLog> logValidator) {
		super(categoryId);
		this.logValidator = logValidator;
		searchLogCount = new int[24];
		searchLogMaxTime = new int[24];
		searchLogSumTime = new int[24];
		searchResultCount = new int[24];
	}
	

	@Override
	public void handleLog(SearchLog logData) throws IOException {
		// logger.debug("handleLog[{}] > {}", categoryId, logData);
		
		String keyword = logData.keyword();
		int hour = -1;
		String[] timeId = logData.getTime().split(":");
		logger.trace("timeId:{}", logData.getTime());
		try {
			hour = Integer.parseInt(timeId[0]);
			logger.trace("timeId:{} / hour:{} / logData:{}", timeId, hour, logData);
			if(hour >= 0 && hour < 24) {
				if (keyword != null && keyword.length() > 0) {
					if (categoryId.equals(logData.categoryId())) {
						// 해당 카테고리만
						searchLogCount[hour] += logData.getCount();
						searchResultCount[hour] += logData.getResultCount();
						searchLogSumTime[hour] += logData.getResponseTime();
						if(logData.getResponseTime() > searchLogMaxTime[hour]){
							searchLogMaxTime[hour] = logData.getResponseTime();
						}
					} else if (categoryId.equals("_root")) {
						// root는 모두다.
						searchLogCount[hour] += logData.getCount();
						searchResultCount[hour] += logData.getResultCount();
						searchLogSumTime[hour] += logData.getResponseTime();
						if(logData.getResponseTime() > searchLogMaxTime[hour]){
							searchLogMaxTime[hour] = logData.getResponseTime();
						}
					}
				}
			}
		} catch (NumberFormatException ignore) {
			logger.debug("{}", ignore);
		}
	}

	@Override
	public Object done() throws IOException {
		
		SearchLogResult[] result = new SearchLogResult[searchLogCount.length];
		
		for (int inx = 0; inx < searchLogCount.length; inx++) {
			if(searchLogCount[inx] != 0) {
				result[inx] = new SearchLogResult(searchLogCount[inx],
						searchResultCount[inx], searchLogSumTime[inx]
						/ searchLogCount[inx], searchLogMaxTime[inx]);
			}
		}
		return result;
	}
}
