package org.fastcatgroup.analytics.analysis.calculator;

import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.KEY_COUNT_EMPTY_FILENAME;
import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.KEY_COUNT_EMPTY_RANK_FILENAME;
import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.KEY_COUNT_FILENAME;
import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.KEY_COUNT_RANK_FILENAME;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.NullLogHandler;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.handler.KeyCountLogSortHandler;
import org.fastcatgroup.analytics.analysis.handler.KeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.MergeKeyCountProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateSearchHitHandler;
import org.fastcatgroup.analytics.analysis.log.ClickLog;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;

/**
 * 클릭로그 통계계산.
 * 
 * */
public class MonthlyClickKeywordHitCalculator extends Calculator<ClickLog> {
	
	private int topCount;
	
	public MonthlyClickKeywordHitCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
	}
	
	@Override
	protected CategoryProcess<ClickLog> newCategoryProcess(String categoryId){
		String encoding = SearchStatisticsProperties.encoding;
		
		
		File workingDir = new File(new File(SearchStatisticsProperties.getMonthDataDir(baseDir, calendar), siteId), categoryId);
		
		if(!workingDir.exists()) {
			try {
				FileUtils.forceMkdir(workingDir);
			} catch (IOException ignore) { }
		}
		
		String timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.MONTH);
		int runKeySize = SearchStatisticsProperties.runKeySize;
		
		//logger.debug("daily calendar : {}", new java.text.SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));

		//
		//TODO calendar의 1일부터 calendar까지.
		//
		int diff = 0; //int diff = SearchStatisticsProperties.getDateDiff(prevCalendar, calendar);
		
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
		CategoryProcess<ClickLog> categoryProcess = new CategoryProcess<ClickLog>(categoryId);
		KeyCountRunEntryParser entryParser = new KeyCountRunEntryParser();
		
		logger.debug("Process Dir = {}, topCount = {}", workingDir.getAbsolutePath(), topCount);
		
		new NullLogHandler<ClickLog>(categoryId).attachLogHandlerTo(categoryProcess);
		
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
		
		//카테고리가 _root이면 10000개, 나머지는 100개씩.
		if(categoryId.equals("_root")){
			topCount = 10000;
		}else{
			topCount = 100;
		}
		
		//키워드별 count 를 바로 저장한다.
//		new UpdateKeywordHitHandler(siteId, categoryId, timeId, rankLogFile, topCount, encoding, entryParser).appendTo(logSort);
//		
//		ProcessHandler rankDiff = new KeywordRankDiffHandler(rankLogFile, compareRankLogFile, topCount, encoding, entryParser).appendTo(logSort);
//		
//		/* 3. 구해진 인기키워드를 저장한다. */
//		ProcessHandler popularKeywordResultHandler = new PopularKeywordResultHandler(popularKeywordLogFile, encoding).appendTo(rankDiff);
		
		//결과없음 순위결정
//		new UpdateEmptyKeywordHandler(siteId, categoryId, timeId).appendTo(popularKeywordResultHandler);
		
		return categoryProcess;
	}
	
}
