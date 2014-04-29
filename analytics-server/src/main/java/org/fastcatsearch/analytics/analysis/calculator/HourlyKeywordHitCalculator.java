package org.fastcatsearch.analytics.analysis.calculator;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatsearch.analytics.analysis.SearchLogValidator;
import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.handler.SearchHourLogKeyCountHandler;
import org.fastcatsearch.analytics.analysis.handler.UpdateHourlySearchHitHandler;
import org.fastcatsearch.analytics.analysis.log.SearchLog;
import org.fastcatsearch.analytics.service.ServiceManager;

public class HourlyKeywordHitCalculator extends Calculator<SearchLog> {
	
	public HourlyKeywordHitCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList) {
		super(name, calendar, baseDir, siteId, categoryIdList);
	}
	
	@Override
	protected CategoryProcess<SearchLog> newCategoryProcess(String categoryId){
		
		StatisticsSettings statisticsSettings = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId);
		int maxKeywordLength = statisticsSettings.getSiteProperties().getMaxKeywordLength();
		Set<String> banWords = statisticsSettings.getSiteProperties().getBanwordSet();
		
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>(categoryId);
		SearchLogValidator logValidator = new SearchLogValidator(banWords, maxKeywordLength);
		new SearchHourLogKeyCountHandler(categoryId, logValidator).attachLogHandlerTo(categoryProcess);
		
		new UpdateHourlySearchHitHandler(siteId, categoryId, calendar).attachProcessTo(categoryProcess);
		
		return categoryProcess;
	}
	
}
