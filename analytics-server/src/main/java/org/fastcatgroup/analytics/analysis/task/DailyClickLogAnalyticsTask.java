package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.DailyClickKeywordHitCalculator;
import org.fastcatgroup.analytics.analysis.log.ClickLog;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 월별 클릭로그 계산 task
 * 
 * */
public class DailyClickLogAnalyticsTask extends AnalyticsTask<ClickLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	public DailyClickLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/M##/data/{siteId} 경로
		File baseDir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		// calc를 카테고리별로 모두 만든다.
		Calculator<ClickLog> popularKeywordCalculator = new DailyClickKeywordHitCalculator("Daily click log calculator", calendar, baseDir, siteId, categoryIdList);
		addCalculator(popularKeywordCalculator);
	}
}