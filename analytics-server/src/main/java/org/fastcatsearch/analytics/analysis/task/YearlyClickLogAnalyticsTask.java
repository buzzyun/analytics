package org.fastcatsearch.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.NDaysClickKeywordHitCalculator;
import org.fastcatsearch.analytics.analysis.calculator.YearlyClickTypeHitCalculator;
import org.fastcatsearch.analytics.analysis.log.ClickLog;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;

/**
 * 월별 클릭로그 계산 task
 * 
 * */
public class YearlyClickLogAnalyticsTask extends AnalyticsTask<ClickLog> {

	private static final long serialVersionUID = 4212969890908932929L;
	
	public YearlyClickLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super("CTR_CLICK_FILE", "NDaysClickLogAnalyticsTask", siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		File baseDir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
		
		//해당년도의 마지막 일자로 되돌린다.
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.add(Calendar.YEAR, 1);
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.YEAR, -1);
		
		// calc를 카테고리별로 모두 만든다.
		Calculator<ClickLog> popularKeywordCalculator = new YearlyClickTypeHitCalculator ("Yearly click log calculator", calendar, prevCalendar, baseDir, siteId, categoryIdList);
		addCalculator(popularKeywordCalculator);
	}
}