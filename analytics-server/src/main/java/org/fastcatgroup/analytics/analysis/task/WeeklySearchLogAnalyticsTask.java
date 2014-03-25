package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.WeeklyKeywordHitAndRankCalculator;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.log.SearchLogReader;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산 task 내부에 인기검색어, 검색횟수 calculator를 가지고 있다.
 * 
 * */
public class WeeklySearchLogAnalyticsTask extends AnalyticsTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;
	
	public WeeklySearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/W##/data/{siteId} 경로
		File dir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		//주의 최초로 되돌린다.
		calendar.add(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_WEEK) * -1 + 7);
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.DAY_OF_MONTH, -7);
		File baseDir = new File(SearchStatisticsProperties.getWeekDataDir(dir, calendar), siteId);
		File prevDir = new File(SearchStatisticsProperties.getWeekDataDir(dir, prevCalendar), siteId);
		Set<String> banWords = null;
		int minimumHitCount = 1;
		int topCount = 10;
		
		String encoding = SearchStatisticsProperties.encoding;
		
		int diff = SearchStatisticsProperties.getDateDiff(prevCalendar, calendar);
		
		//일주일치의 일자별 raw.log를 머징한다.
		File[] files = new File[diff];
		Calendar dailyCalendar = (Calendar) calendar.clone();
		for(int inx=0;inx < diff; inx++) {
			File dailyBaseDir = new File(SearchStatisticsProperties.getDayDataDir(dir, dailyCalendar), siteId);
			files[inx] = new File(dailyBaseDir, "raw.log");
			dailyCalendar.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		logger.debug("calculating{} {}", "", files);
		
		try {
			logReader = new SearchLogReader(files, encoding);
		} catch (IOException e) {
			logger.error("", e);
		}
		
		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> popularKeywordCalculator = new WeeklyKeywordHitAndRankCalculator("Weekly popular keyword calculator", calendar, baseDir, prevDir, siteId, categoryIdList, banWords, minimumHitCount, topCount);
		addCalculator(popularKeywordCalculator);
	}
}