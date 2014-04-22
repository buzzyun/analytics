package org.fastcatgroup.analytics.analysis.calculator;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.fastcatgroup.analytics.analysis.SearchLogValidator;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings;
import org.fastcatgroup.analytics.analysis.handler.SearchHourLogKeyCountHandler;
import org.fastcatgroup.analytics.analysis.handler.UpdateHourlySearchHitHandler;
import org.fastcatgroup.analytics.analysis.log.SearchLog;
import org.fastcatgroup.analytics.service.ServiceManager;

public class HourlyKeywordHitAndRankCalculator extends Calculator<SearchLog> {
	
	private Set<String> banWords;
	
	public HourlyKeywordHitAndRankCalculator(String name, Calendar calendar, File baseDir, String siteId, List<String> categoryIdList, Set<String> banWords) {
		super(name, calendar, baseDir, siteId, categoryIdList);
		this.banWords = banWords;
	}
	
	@Override
	protected CategoryProcess<SearchLog> newCategoryProcess(String categoryId){
		
		StatisticsSettings statisticsSettings = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId);
		int maxKeywordLength = statisticsSettings.getSiteProperties().getMaxKeywordLength();
		
		CategoryProcess<SearchLog> categoryProcess = new CategoryProcess<SearchLog>(categoryId);
		SearchLogValidator logValidator = new SearchLogValidator(banWords, maxKeywordLength);
		new SearchHourLogKeyCountHandler(categoryId, logValidator).attachLogHandlerTo(categoryProcess);
		
		new UpdateHourlySearchHitHandler(siteId, categoryId, calendar).attachProcessTo(categoryProcess);
		
		return categoryProcess;
	}
	
}
