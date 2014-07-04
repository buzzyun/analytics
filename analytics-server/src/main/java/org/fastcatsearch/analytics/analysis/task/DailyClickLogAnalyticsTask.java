package org.fastcatsearch.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.DailyRawLogger;
import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.DailyClickKeywordHitCalculator;
import org.fastcatsearch.analytics.analysis.log.ClickLog;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;

/**
 * 월별 클릭로그 계산 task
 * 
 * */
public class DailyClickLogAnalyticsTask extends AnalyticsTask<ClickLog> {

	private static final long serialVersionUID = 4212969890908932929L;
	
	public DailyClickLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super("DAILY_CLICK", "DailyClickLogAnalyticsTask", siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		File baseDir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
		
		// calc를 카테고리별로 모두 만든다.
		Calculator<ClickLog> popularKeywordCalculator = new DailyClickKeywordHitCalculator("Daily click log calculator", calendar, baseDir, siteId, categoryIdList);
		addCalculator(popularKeywordCalculator);
	}
}