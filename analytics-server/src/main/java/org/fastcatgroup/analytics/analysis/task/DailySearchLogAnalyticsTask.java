package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.DailyKeywordHitAndRankCalculator;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.analysis.log.SearchLogReader;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산 task 내부에 인기검색어, 검색횟수 calculator를 가지고 있다.
 * 
 * */
public class DailySearchLogAnalyticsTask extends AnalyticsTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	public DailySearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/M##/D##/data/{siteId} 경로
		File dir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.DAY_OF_MONTH, -1);
		File baseDir = new File(SearchStatisticsProperties.getDayDataDir(dir, calendar), siteId);
		File prevDir = new File(SearchStatisticsProperties.getDayDataDir(dir, prevCalendar), siteId);
		Set<String> banWords = null;
		int minimumHitCount = 1;
		int topCount = 10;

		File logFile = new File(baseDir, "raw.log");
		String encoding = SearchStatisticsProperties.encoding;
		try {
			logReader = new SearchLogReader(logFile, encoding);
		} catch (IOException e) {
			logger.error("", e.getMessage());
		}

		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> popularKeywordCalculator = new DailyKeywordHitAndRankCalculator("Daily popular keyword calculator", calendar, baseDir, prevDir, siteId, categoryIdList, banWords, minimumHitCount, topCount);
		addCalculator(popularKeywordCalculator);
		
	}

}
