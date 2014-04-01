package org.fastcatgroup.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.calculator.Calculator;
import org.fastcatgroup.analytics.analysis.calculator.MonthlyTypeHitCalculator;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLog;
import org.fastcatgroup.analytics.analysis.schedule.Schedule;

/**
 * 일별 검색로그 계산. 검색 type별 hit수. 
 * 
 * */
public class MonthlyTypeSearchLogAnalyticsTask extends AnalyticsTask<TypeSearchLog> {

	private static final long serialVersionUID = -1324147495414071499L;
	
	public MonthlyTypeSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/W##/data/{siteId} 경로
		File baseDir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		String[] typeList = environment.settingManager().getSystemSettings().getStringArray("db.typeList", ",");
		
		//월의 최초로 되돌린다.
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) * -1);
		Calendar prevCalendar = (Calendar) calendar.clone();
		prevCalendar.add(Calendar.MONTH, -1);

		// calc를 카테고리별로 모두 만든다.
		Calculator<TypeSearchLog> yearlyTypeHitCalculator = new MonthlyTypeHitCalculator("Monthly type hit calculator", calendar, prevCalendar, baseDir, siteId, categoryIdList, typeList);
		addCalculator(yearlyTypeHitCalculator);
	}
}
