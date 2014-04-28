package org.fastcatsearch.analytics.analysis.task;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.calculator.Calculator;
import org.fastcatsearch.analytics.analysis.calculator.WeeklyTypeHitCalculator;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatsearch.analytics.analysis.log.TypeSearchLog;
import org.fastcatsearch.analytics.analysis.schedule.Schedule;
import org.fastcatsearch.analytics.service.ServiceManager;

/**
 * 일별 검색로그 계산. 검색 type별 hit수. 
 * 
 * */
public class WeeklyTypeSearchLogAnalyticsTask extends AnalyticsTask<TypeSearchLog> {

	private static final long serialVersionUID = -1324147495414071499L;

	
	public WeeklyTypeSearchLogAnalyticsTask(String siteId, List<String> categoryIdList, Schedule schedule, int priority) {
		super(siteId, categoryIdList, schedule, priority);
	}

	@Override
	protected void prepare(Calendar calendar) {
		// baseDir : statistics/search/date/Y####/W##/data/{siteId} 경로
		File baseDir = environment.filePaths().getStatisticsRoot().file("search", "date");
		
		SiteAttribute siteAttribute = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId).getSiteAttribute();
		List<TypeSetting> typeList = siteAttribute.getTypeList();
		//주의 최초로 되돌린다.
		Calendar prevCalendar = StatisticsUtils.getLastDayOfWeek(calendar);
		prevCalendar.add(Calendar.DAY_OF_MONTH, -7);

		// calc를 카테고리별로 모두 만든다.
		Calculator<TypeSearchLog> typeHitCalculator = new WeeklyTypeHitCalculator("Weekly type hit calculator", calendar, prevCalendar, baseDir, siteId, categoryIdList, typeList);
		addCalculator(typeHitCalculator);
	}
}