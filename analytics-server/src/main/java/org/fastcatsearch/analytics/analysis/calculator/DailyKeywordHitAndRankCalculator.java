package org.fastcatsearch.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.fastcatsearch.analytics.analysis.KeyCountEmptyLogAggregator;
import org.fastcatsearch.analytics.analysis.KeyCountLogAggregator;
import org.fastcatsearch.analytics.analysis.LogAggregatorContainer;
import org.fastcatsearch.analytics.analysis.SearchLogValidator;
import org.fastcatsearch.analytics.analysis.ServiceCountLogAggregator;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.ServiceSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatsearch.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatsearch.analytics.analysis.handler.KeywordRankDiffHandler;
import org.fastcatsearch.analytics.analysis.handler.PopularKeywordResultHandler;
import org.fastcatsearch.analytics.analysis.handler.ProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.SearchLogKeyCountHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateEmptyKeywordHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateKeywordHitHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdatePopularKeywordHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateSearchHitHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateServiceTypeHitHandler;
import org.fastcatsearch.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.service.ServiceManager;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

public class DailyKeywordHitAndRankCalculator extends Calculator<SearchLog> {
	
	private File prevDir;
	
	public DailyKeywordHitAndRankCalculator(String name, Calendar calendar, File baseDir, File prevDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.prevDir = prevDir;
	}
	
	@Override
	protected CategoryProcess<SearchLog> newCategoryProcess(String categoryId){
		String encoding = StatisticsProperties.encoding;
		File workingDir = new File(baseDir, categoryId);
		File prevWorkingDir = new File(prevDir, categoryId);
		
		if(!workingDir.exists()) {
			try {
				FileUtils.forceMkdir(workingDir);
			} catch (IOException ignore) { }
		}
		
		if(!prevWorkingDir.exists()) {
			try {
				FileUtils.forceMkdir(prevWorkingDir);
			} catch (IOException ignore) { }
		}
		
		String timeId = StatisticsUtils.getTimeId(calendar, Calendar.DATE);
		
		StatisticsSettings statisticsSettings = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId);
		int maxKeywordLength = statisticsSettings.getSiteProperties().getMaxKeywordLength();
		int runKeySize = StatisticsProperties.runKeySize;

		Set<String> banWords = statisticsSettings.getSiteProperties().getBanwordSet();
		int minimumHitCount = statisticsSettings.getPopularKeywordSetting().getMinimumHitCount();
		SiteAttribute siteAttribute = statisticsSettings.getSiteAttribute();
		List<ServiceSetting> serviceTypeList = siteAttribute.getServiceList();
		
		logger.debug("Process Dir = {}", workingDir.getAbsolutePath());
		KeyCountRunEntryParser entryParser = new KeyCountRunEntryParser();
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>(categoryId);
		SearchLogValidator logValidator = new SearchLogValidator(banWords, maxKeywordLength);
		
		LogAggregatorContainer<SearchLog> aggregator = new LogAggregatorContainer<SearchLog>();
		aggregator.addAggregator(new KeyCountLogAggregator<SearchLog>(workingDir, KEY_COUNT_FILENAME, runKeySize, encoding, minimumHitCount, entryParser));
		aggregator.addAggregator(new KeyCountEmptyLogAggregator<SearchLog>(workingDir, KEY_COUNT_EMPTY_FILENAME, runKeySize, encoding, minimumHitCount, entryParser));
		
		/* 서비스별 갯수. */
		if("_root".equals(categoryId)) {
			aggregator.addAggregator(new ServiceCountLogAggregator<SearchLog>(workingDir, SERVICE_COUNT_FILENAME, encoding, serviceTypeList));
		}
		
		new SearchLogKeyCountHandler(categoryId, aggregator, logValidator, entryParser).attachLogHandlerTo(categoryProcess);
		
		/* 0. 갯수를 db로 저장한다. */
		ProcessHandler updateSearchHitHandler = new UpdateSearchHitHandler(siteId, categoryId, timeId).attachProcessTo(categoryProcess);
		
		//서비스타입 로그 기록
		if("_root".equals(categoryId)) {
			updateSearchHitHandler = new UpdateServiceTypeHitHandler(siteId, timeId, workingDir, SERVICE_COUNT_FILENAME, encoding).appendTo(updateSearchHitHandler);
		}

		/* 1. count로 정렬하여 key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, KEY_COUNT_FILENAME, KEY_COUNT_RANK_FILENAME, encoding, runKeySize, entryParser).appendTo(updateSearchHitHandler);
		
		//결과없음 검색순위 정렬
		new KeyCountLogSortHandler(workingDir, KEY_COUNT_EMPTY_FILENAME, KEY_COUNT_EMPTY_RANK_FILENAME, encoding, runKeySize, entryParser).appendTo(updateSearchHitHandler);
		
		/* 2. 이전일과 비교하여 diff 생성. */
		File rankLogFile = new File(workingDir, KEY_COUNT_RANK_FILENAME);
		File compareRankLogFile = new File(prevWorkingDir, KEY_COUNT_RANK_FILENAME);
		File popularKeywordLogFile = new File(workingDir, POPULAR_FILENAME);
		
		File rankEmptyLogFile = new File(workingDir, KEY_COUNT_EMPTY_RANK_FILENAME);
		File compareEmptyRankLogFile = new File(prevWorkingDir, KEY_COUNT_EMPTY_RANK_FILENAME);
		File popularEmptyKeywordLogFile = new File(workingDir, POPULAR_EMPTY_FILENAME);
		
		statisticsSettings.getPopularKeywordSetting().getRootStoreCount();
		int topCount = 0;
		//카테고리가 _root이면 10000개, 나머지는 100개씩.
		if(categoryId.equals("_root")){
			topCount = statisticsSettings.getPopularKeywordSetting().getRootStoreCount();
		}else{
			topCount = statisticsSettings.getPopularKeywordSetting().getCategoryStoreCount();
		}
		
		//키워드별 count 를 바로 저장한다.
		new UpdateKeywordHitHandler(siteId, categoryId, timeId, rankLogFile, topCount, encoding, entryParser).appendTo(logSort);
		
		ProcessHandler rankDiff = new KeywordRankDiffHandler(rankLogFile, compareRankLogFile, topCount, encoding, entryParser).appendTo(logSort);
		
		/* 3. 구해진 인기키워드를 저장한다. */
		ProcessHandler popularKeywordResultHandler = new PopularKeywordResultHandler(popularKeywordLogFile, encoding).appendTo(rankDiff);
		
		/* 4. 인기검색어 객체 업데이트 */
		ProcessHandler updatePopularKeywordHandler = new UpdatePopularKeywordHandler(siteId, categoryId, timeId).appendTo(popularKeywordResultHandler);
		
		//결과없음 순위결정
		rankDiff = new KeywordRankDiffHandler(rankEmptyLogFile, compareEmptyRankLogFile, topCount, encoding, entryParser).appendTo(updatePopularKeywordHandler);
		popularKeywordResultHandler = new PopularKeywordResultHandler(popularEmptyKeywordLogFile, encoding).appendTo(rankDiff);
		new UpdateEmptyKeywordHandler(siteId, categoryId, timeId).appendTo(popularKeywordResultHandler);
		
		return categoryProcess;
	}
}