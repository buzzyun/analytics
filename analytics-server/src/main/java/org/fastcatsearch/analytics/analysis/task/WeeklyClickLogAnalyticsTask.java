package org.fastcatsearch.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.WeeklyClickTypeHitCalculator;
import org.fastcatsearch.analytics.analysis.log.ClickLog;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;

/**
 * 주별 클릭로그 계산 task
 * 
 * */
public class WeeklyClickLogAnalyticsTask extends AnalyticsTask<ClickLog> {

	private static final long serialVersionUID = 4212969890908932929L;
	
	public WeeklyClickLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super("CTR_CLICK_FILE", "WeeklyClickTypeHitCalculator", siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		File baseDir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
		
		//주의 최초로 되돌린다.
		Calendar prevCalendar = StatisticsUtils.getLastDayOfWeek(calendar);
		prevCalendar.add(Calendar.DAY_OF_MONTH, -7);
		
		// calc를 카테고리별로 모두 만든다.
		Calculator<ClickLog> clickTypeCalculator = new WeeklyClickTypeHitCalculator("Weekly click log calculator", calendar, prevCalendar, baseDir, siteId, categoryIdList);
		addCalculator(clickTypeCalculator);
	}
}