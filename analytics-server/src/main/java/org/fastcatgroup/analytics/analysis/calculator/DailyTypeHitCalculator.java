package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatgroup.analytics.analysis.handler.TypeSearchLogKeyCountHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateSearchTypeHitHandler;
import org.fastcatgroup.analytics.analysis.log.TypeSearchLog;

public class DailyTypeHitCalculator extends Calculator<TypeSearchLog> {
	
	List<TypeSetting> typeList;
	
	public DailyTypeHitCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList, List<TypeSetting> typeList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.typeList = typeList;
	}
	
	@Override
	protected CategoryProcess<TypeSearchLog> newCategoryProcess(String categoryId){
		String timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.DATE);
		
		CategoryProcess<TypeSearchLog> categoryProcess = new CategoryProcess<TypeSearchLog>(categoryId);
		new TypeSearchLogKeyCountHandler(categoryId, typeList).attachLogHandlerTo(categoryProcess);
		
		/* 0. 갯수를 db로 저장한다. */
		new UpdateSearchTypeHitHandler(siteId, categoryId, timeId, typeList).attachProcessTo(categoryProcess);
		
		return categoryProcess;
	}
	
}
