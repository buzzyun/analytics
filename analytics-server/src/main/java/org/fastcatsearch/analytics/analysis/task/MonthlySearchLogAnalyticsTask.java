package org.fastcatsearch.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.MonthlyKeywordHitAndRankCalculator;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산 task 내부에 인기검색어, 검색횟수 calculator를 가지고 있다.
 * 
 * */
public class MonthlySearchLogAnalyticsTask extends AnalyticsTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	public MonthlySearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/M##/data/{siteId} 경로
		File baseDir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
		
		//월의 최초로 되돌린다.
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) * -1);
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.MONTH, -1);
		
		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> popularKeywordCalculator = new MonthlyKeywordHitAndRankCalculator("Weekly popular keyword calculator", calendar, prevCalendar, baseDir, siteId, categoryIdList);
		addCalculator(popularKeywordCalculator);
	}
}