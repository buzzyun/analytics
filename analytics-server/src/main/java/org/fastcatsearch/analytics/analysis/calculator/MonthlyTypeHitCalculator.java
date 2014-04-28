package org.fastcatsearch.analytics.analysis.calculator;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.NullLogHandler;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatsearch.analytics.analysis.handler.ProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.SearchTypeDatabaseProcessHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateSearchTypeHitHandler;
import org.fastcatsearch.analytics.analysis.log.TypeSearchLog;

public class MonthlyTypeHitCalculator extends Calculator<TypeSearchLog> {
	
	private List<TypeSetting> typeList;
	private Calendar prevCalendar;
	
	public MonthlyTypeHitCalculator(String name, Calendar calendar, Calendar prevCalendar, File baseDir, String siteId, List<String> categoryIdList, List<TypeSetting> typeList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.typeList = typeList;
		this.prevCalendar = prevCalendar;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected CategoryProcess<TypeSearchLog> newCategoryProcess(String categoryId){
		String timeId = StatisticsUtils.getTimeId(calendar, Calendar.MONTH);
		
		String prevTimeId = StatisticsUtils.getTimeId(prevCalendar, Calendar.DAY_OF_MONTH);
		String currTimeId = StatisticsUtils.getTimeId(calendar, Calendar.DAY_OF_MONTH);
		//logger.debug("calculating {} ~ {}", prevTimeId, currTimeId);
		//1달치의 일별 로그를 데이터베이스를 사용하여 머징한다.
		CategoryProcess<TypeSearchLog> categoryProcess = new CategoryProcess<TypeSearchLog>(categoryId);
		
		new NullLogHandler(categoryId).attachLogHandlerTo(categoryProcess);
		
		ProcessHandler mergeTypeHandler = new SearchTypeDatabaseProcessHandler(
				siteId, categoryId, prevTimeId, currTimeId, typeList).attachProcessTo(categoryProcess);
		
		/* 0. 갯수를 db로 저장한다. */
		new UpdateSearchTypeHitHandler(siteId, categoryId, timeId, typeList).appendTo(mergeTypeHandler);
		return categoryProcess;
	}
}