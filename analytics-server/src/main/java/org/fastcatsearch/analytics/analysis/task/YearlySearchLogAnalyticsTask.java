package org.fastcatsearch.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.YearlyKeywordHitAndRankCalculator;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산 task 내부에 인기검색어, 검색횟수 calculator를 가지고 있다.
 * 
 * */
public class YearlySearchLogAnalyticsTask extends AnalyticsTask<SearchLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	public YearlySearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/data/{siteId} 경로
		File baseDir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
		
		//해당년도의 마지막 일자로 되돌린다.
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.add(Calendar.YEAR, 1);
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.YEAR, -1);
		
		// calc를 카테고리별로 모두 만든다.
		Calculator<SearchLog> popularKeywordCalculator = new YearlyKeywordHitAndRankCalculator("Yearly popular keyword calculator", calendar, prevCalendar, baseDir, siteId, categoryIdList);
		addCalculator(popularKeywordCalculator);
	}
}