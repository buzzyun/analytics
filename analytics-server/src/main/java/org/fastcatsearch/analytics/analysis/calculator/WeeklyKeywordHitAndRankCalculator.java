package org.fastcatsearch.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fastcatsearch.analytics.analysis.NullLogHandler;
import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatsearch.analytics.analysis.handler.KeyCountProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.KeywordRankDiffHandler;
import org.fastcatsearch.analytics.analysis.handler.MergeKeyCountProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.PopularKeywordResultHandler;
import org.fastcatsearch.analytics.analysis.handler.ProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateEmptyKeywordHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateKeywordHitHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdatePopularKeywordHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateSearchHitHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateServiceTypeHitHandler;
import org.fastcatsearch.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.service.ServiceManager;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

public class WeeklyKeywordHitAndRankCalculator extends Calculator<SearchLog> {

	private Calendar prevCalendar;
	
	public WeeklyKeywordHitAndRankCalculator(String name, Calendar calendar, Calendar prevCalendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.prevCalendar = prevCalendar;
	}
	
	@Override
	protected CategoryProcess<SearchLog> newCategoryProcess(String categoryId){
		String encoding = StatisticsProperties.encoding;
		
		int diff = StatisticsUtils.getDateDiff(prevCalendar, calendar) + 1;
		
		File workingDir = new File(StatisticsUtils.getWeekDataDir(baseDir, calendar), categoryId);
		File prevWorkingDir = new File(StatisticsUtils.getWeekDataDir(baseDir, prevCalendar), categoryId);
		
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
		
		String timeId = StatisticsUtils.getTimeId(calendar, Calendar.WEEK_OF_YEAR);
		int runKeySize = StatisticsProperties.runKeySize;
		
		//logger.debug("daily calendar : {}", new java.text.SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
		File[] keyCountFiles = new File[diff];
		File[] keyEmptyFiles = new File[diff];
		File[] serviceCountFiles = new File[diff];
		Calendar dailyCalendar = (Calendar) calendar.clone();
		for(int inx=0;inx < diff; inx++) {
			File timeDir = StatisticsUtils.getDayDataDir(baseDir, dailyCalendar);
			keyCountFiles[inx] = new File(new File(timeDir, categoryId), KEY_COUNT_FILENAME);
			keyEmptyFiles[inx] = new File(new File(timeDir, categoryId), KEY_COUNT_EMPTY_FILENAME);
			serviceCountFiles[inx] = new File(new File(timeDir, categoryId), SERVICE_COUNT_FILENAME);
			dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		String dateFrom = StatisticsUtils.getTimeId(dailyCalendar, Calendar.DAY_OF_MONTH);
		String dateTo = StatisticsUtils.getTimeId(calendar, Calendar.DAY_OF_MONTH);
		
		//일주일치의 일자별 key-count log들을 머징한다.
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>(categoryId);
		KeyCountRunEntryParser entryParser = new KeyCountRunEntryParser();
		
		logger.debug("Process Dir = {}", workingDir.getAbsolutePath());
		
		new NullLogHandler<SearchLog>(categoryId).attachLogHandlerTo(categoryProcess);
		
		ProcessHandler mergeKeyCount = new MergeKeyCountProcessHandler(keyCountFiles, workingDir, KEY_COUNT_FILENAME, encoding, entryParser).attachProcessTo(categoryProcess);
		
		mergeKeyCount = new MergeKeyCountProcessHandler(keyEmptyFiles, workingDir, KEY_COUNT_EMPTY_FILENAME, encoding, entryParser).appendTo(mergeKeyCount);
		
		//서비스타입 기록
		if("_root".equals(categoryId)) {
			mergeKeyCount = new MergeKeyCountProcessHandler(serviceCountFiles, workingDir, SERVICE_COUNT_FILENAME, encoding, entryParser).appendTo(mergeKeyCount);
		}
		
		ProcessHandler hitCounter = new KeyCountProcessHandler(siteId,
				categoryId, workingDir, KEY_COUNT_FILENAME, dateFrom, dateTo,
				encoding).appendTo(mergeKeyCount);
		
		/* 0. 갯수를 db로 저장한다. */
		ProcessHandler updateSearchHitHandler = new UpdateSearchHitHandler(siteId, categoryId, timeId).appendTo(hitCounter);
		
		//서비스타입 로그 기록
		if("_root".equals(categoryId)) {
			updateSearchHitHandler = new UpdateServiceTypeHitHandler(siteId, timeId, workingDir, SERVICE_COUNT_FILENAME, encoding).appendTo(updateSearchHitHandler);
		}

		/* 1. count로 정렬하여 key-count-rank.log로 저장. */
		ProcessHandler logSort = new KeyCountLogSortHandler(workingDir, KEY_COUNT_FILENAME, KEY_COUNT_RANK_FILENAME, encoding, runKeySize, entryParser).appendTo(updateSearchHitHandler);
		
		//결과없음 검색순위 정렬
		logSort = new KeyCountLogSortHandler(workingDir, KEY_COUNT_EMPTY_FILENAME, KEY_COUNT_EMPTY_RANK_FILENAME, encoding, runKeySize, entryParser).appendTo(logSort);
		
		/* 2. 이전일과 비교하여 diff 생성. */
		File rankLogFile = new File(workingDir, KEY_COUNT_RANK_FILENAME);
		File compareRankLogFile = new File(prevWorkingDir, KEY_COUNT_RANK_FILENAME);
		File popularKeywordLogFile = new File(workingDir, POPULAR_FILENAME);
		
		File rankEmptyLogFile = new File(workingDir, KEY_COUNT_EMPTY_RANK_FILENAME);
		File compareEmptyRankLogFile = new File(prevWorkingDir, KEY_COUNT_EMPTY_RANK_FILENAME);
		File popularEmptyKeywordLogFile = new File(workingDir, POPULAR_EMPTY_FILENAME);
		
		StatisticsSettings statisticsSettings = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId);
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