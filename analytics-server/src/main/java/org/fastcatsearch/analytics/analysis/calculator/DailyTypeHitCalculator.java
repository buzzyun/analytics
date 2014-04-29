package org.fastcatsearch.analytics.analysis.calculator;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatsearch.analytics.analysis.handler.TypeSearchLogKeyCountHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateSearchTypeHitHandler;
import org.fastcatsearch.analytics.analysis.log.TypeSearchLog;
import org.fastcatsearch.analytics.service.ServiceManager;

public class DailyTypeHitCalculator extends Calculator<TypeSearchLog> {
	
	
	public DailyTypeHitCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
	}
	
	@Override
	protected CategoryProcess<TypeSearchLog> newCategoryProcess(String categoryId){
		String timeId = StatisticsUtils.getTimeId(calendar, Calendar.DATE);
		
		SiteAttribute siteAttribute = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId).getSiteAttribute();
		List<TypeSetting> typeList = siteAttribute.getTypeList();
		logger.debug("@@@@typeList > {}", "", typeList);
		
		CategoryProcess<TypeSearchLog> categoryProcess = new CategoryProcess<TypeSearchLog>(categoryId);
		new TypeSearchLogKeyCountHandler(categoryId, typeList).attachLogHandlerTo(categoryProcess);
		
		/* 0. 갯수를 db로 저장한다. */
		new UpdateSearchTypeHitHandler(siteId, categoryId, timeId, typeList).attachProcessTo(categoryProcess);
		
		return categoryProcess;
	}
	
}
