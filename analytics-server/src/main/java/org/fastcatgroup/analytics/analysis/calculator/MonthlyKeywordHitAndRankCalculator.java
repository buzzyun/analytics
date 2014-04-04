package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.NullLogHandler;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis.handler.KeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.KeywordRankDiffHandler;
import org.fastcatgroup.analytics.analysis.handler.MergeKeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.PopularKeywordResultHandler;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateEmptyKeywordHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdatePopularKeywordHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateKeywordHitHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateSearchHitHandler;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.fastcatgroup.analytics.analysis.log.SearchLog;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

public class MonthlyKeywordHitAndRankCalculator extends Calculator<SearchLog> {
	
	private Calendar prevCalendar;
	private int topCount;
	
	public MonthlyKeywordHitAndRankCalculator(String name, Calendar calendar, Calendar prevCalendar, File baseDir, String siteId, List<String> categoryIdList, int topCount) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.prevCalendar = prevCalendar;
		this.topCount = topCount;
	}
	
	@Override
	protected CategoryProcess<SearchLog> newCategoryProcess(String categoryId){
		String encoding = SearchStatisticsProperties.encoding;
		
		int diff = SearchStatisticsProperties.getDateDiff(prevCalendar, calendar);
		
		File workingDir = new File(new File(SearchStatisticsProperties.getMonthDataDir(baseDir, calendar), siteId), categoryId);
		File prevWorkingDir = new File(new File(SearchStatisticsProperties.getMonthDataDir(baseDir, prevCalendar), siteId), categoryId);
		
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
		
		String timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.MONTH);
		int runKeySize = SearchStatisticsProperties.runKeySize;
		
		//logger.debug("daily calendar : {}", new java.text.SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
		File[] keyCountFiles = new File[diff];
		File[] keyEmptyFiles = new File[diff];
		Calendar dailyCalendar = (Calendar) calendar.clone();
		for(int inx=0;inx < diff; inx++) {
			File timeDir = SearchStatisticsProperties.getDayDataDir(baseDir, dailyCalendar);
			keyCountFiles[inx] = new File(new File(new File( timeDir, siteId), categoryId), KEY_COUNT_FILENAME);
			keyEmptyFiles[inx] = new File(new File(new File( timeDir, siteId), categoryId), KEY_COUNT_EMPTY_FILENAME);
			dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		String dateFrom = SearchStatisticsProperties.getTimeId(dailyCalendar, Calendar.DAY_OF_MONTH);
		String dateTo = SearchStatisticsProperties.getTimeId(calendar, Calendar.DAY_OF_MONTH);
		
		//1달치의 일자별 key-count log들을 머징한다.
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>(categoryId);
		KeyCountRunEntryParser entryParser = new KeyCountRunEntryParser();
		
		logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
		
		new NullLogHandler<SearchLog>(categoryId).attachLogHandlerTo(categoryProcess);
		
		ProcessHandler mergeKeyCount = new MergeKeyCountProcessHandler(keyCountFiles, workingDir, KEY_COUNT_FILENAME, encoding, entryParser).attachProcessTo(categoryProcess);
		
		mergeKeyCount = new MergeKeyCountProcessHandler(keyEmptyFiles, workingDir, KEY_COUNT_EMPTY_FILENAME, encoding, entryParser).appendTo(mergeKeyCount);
		
		ProcessHandler hitCounter = new KeyCountProcessHandler(siteId,
				categoryId, workingDir, KEY_COUNT_FILENAME, dateFrom, dateTo,
				encoding).appendTo(mergeKeyCount);
		
		/* 0. 갯수를 db로 저장한다. */
		ProcessHandler updateSearchHitHandler = new UpdateSearchHitHandler(siteId, categoryId, timeId).appendTo(hitCounter);
		
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
		
		//카테고리가 _root이면 10000개, 나머지는 100개씩.
		if(categoryId.equals("_root")){
			topCount = 10000;
		}else{
			topCount = 100;
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
