package org.fastcatsearch.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.WeeklyTypeHitCalculator;
import org.fastcatsearch.analytics.analysis.log.TypeSearchLog;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산. 검색 type별 hit수. 
 * 
 * */
public class WeeklyTypeSearchLogAnalyticsTask extends AnalyticsTask<TypeSearchLog> {

	private static final long serialVersionUID = -1324147495414071499L;

	
	public WeeklyTypeSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super("WEEKLY_TYPE", "WeeklyTypeSearchLogAnalyticsTask", siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		File baseDir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
		
		//주의 최초로 되돌린다.
		Calendar prevCalendar = StatisticsUtils.getLastDayOfWeek(calendar);
		prevCalendar.add(Calendar.DAY_OF_MONTH, -7);

		// calc를 카테고리별로 모두 만든다.
		Calculator<TypeSearchLog> typeHitCalculator = new WeeklyTypeHitCalculator("Weekly type hit calculator", calendar, prevCalendar, baseDir, siteId, categoryIdList);
		addCalculator(typeHitCalculator);
	}
}
