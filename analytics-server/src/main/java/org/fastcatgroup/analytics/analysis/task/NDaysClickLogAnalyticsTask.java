package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.MonthlyClickKeywordHitCalculator;
import org.fastcatgroup.analytics.analysis.calculator.NDaysClickKeywordHitCalculator;
import org.fastcatgroup.analytics.analysis.log.ClickLog;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 월별 클릭로그 계산 task
 * 
 * */
public class NDaysClickLogAnalyticsTask extends AnalyticsTask<ClickLog> {

	private static final long serialVersionUID = 4212969890908932929L;

	public NDaysClickLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		File baseDir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		// calc를 카테고리별로 모두 만든다.
		Calculator<ClickLog> popularKeywordCalculator = new NDaysClickKeywordHitCalculator("NDays click log calculator", calendar, baseDir, siteId, categoryIdList);
		addCalculator(popularKeywordCalculator);
	}
}