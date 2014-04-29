package org.fastcatsearch.analytics.analysis.calculator;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.NullLogHandler;
import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatsearch.analytics.analysis.handler.ProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.SearchTypeDatabaseProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateSearchTypeHitHandler;
import org.fastcatsearch.analytics.analysis.log.TypeSearchLog;
import org.fastcatsearch.analytics.service.ServiceManager;

public class YearlyTypeHitCalculator extends Calculator<TypeSearchLog> {
	
	private Calendar prevCalendar;
	
	public YearlyTypeHitCalculator(String name, Calendar calendar, Calendar prevCalendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.prevCalendar = prevCalendar;
	}
	
	@Override
	protected CategoryProcess<TypeSearchLog> newCategoryProcess(String categoryId){
		String timeId = StatisticsUtils.getTimeId(calendar, Calendar.YEAR);
		
		String prevTimeId = StatisticsUtils.getTimeId(prevCalendar, Calendar.MONTH);
		String currTimeId = StatisticsUtils.getTimeId(calendar, Calendar.MONTH);
		//logger.debug("calculating {} ~ {}", prevTimeId, currTimeId);
		//1년치의 월별 로그를 데이터베이스를 사용하여 머징한다.
		CategoryProcess<TypeSearchLog> categoryProcess = new CategoryProcess<TypeSearchLog>(categoryId);
		
		new NullLogHandler<TypeSearchLog>(categoryId).attachLogHandlerTo(categoryProcess);
		
		List<TypeSetting> typeList = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId).getSiteAttribute().getTypeList();
		
		ProcessHandler mergeTypeHandler = new SearchTypeDatabaseProcessHandler(
				siteId, categoryId, prevTimeId, currTimeId, typeList).attachProcessTo(categoryProcess);
		
		/* 0. 갯수를 db로 저장한다. */
		new UpdateSearchTypeHitHandler(siteId, categoryId, timeId, typeList).appendTo(mergeTypeHandler);
		return categoryProcess;
	}
}
