package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.NullLogHandler;
import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatgroup.analytics.analysis.handler.ProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.SearchTypeDatabaseProcessHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateSearchTypeHitHandler;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLog;

public class YearlyTypeHitCalculator extends Calculator<TypeSearchLog> {
	
	private List<TypeSetting> typeList;
	private Calendar prevCalendar;
	
	public YearlyTypeHitCalculator(String name, Calendar calendar, Calendar prevCalendar, File baseDir, String siteId, List<String> categoryIdList, List<TypeSetting> typeList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.typeList = typeList;
		this.prevCalendar = prevCalendar;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected CategoryProcess<TypeSearchLog> newCategoryProcess(String categoryId){
		String timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.YEAR);
		
		String prevTimeId = SearchStatisticsProperties.getTimeId(prevCalendar, Calendar.MONTH);
		String currTimeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.MONTH);
		//logger.debug("calculating {} ~ {}", prevTimeId, currTimeId);
		//1년치의 월별 로그를 데이터베이스를 사용하여 머징한다.
		CategoryProcess<TypeSearchLog> categoryProcess = new CategoryProcess<TypeSearchLog>(categoryId);
		
		new NullLogHandler(categoryId).attachLogHandlerTo(categoryProcess);
		
		ProcessHandler mergeTypeHandler = new SearchTypeDatabaseProcessHandler(
				siteId, categoryId, prevTimeId, currTimeId, typeList).attachProcessTo(categoryProcess);
		
		/* 0. 갯수를 db로 저장한다. */
		new UpdateSearchTypeHitHandler(siteId, categoryId, timeId, typeList).appendTo(mergeTypeHandler);
		return categoryProcess;
	}
}
