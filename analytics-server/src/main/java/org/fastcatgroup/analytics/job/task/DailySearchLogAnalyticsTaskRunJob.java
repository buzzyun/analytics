package org.fastcatgroup.analytics.job.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.CategoryConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.analysis.schedule.TimeSchedule;
import org.fastcatgroup.analytics.analysis.task.DailySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.DailyTypeSearchLogAnalyticsTask;
import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.job.Job;
import org.fastcatgroup.analytics.service.ServiceManager;

public class DailySearchLogAnalyticsTaskRunJob extends Job {
	
	private static final long serialVersionUID = 8365827926796537780L;
	
	private String siteId;
	private String timeId;
	
	public DailySearchLogAnalyticsTaskRunJob(){
	}
	
	public DailySearchLogAnalyticsTaskRunJob(String siteId, String timeId){
		this.siteId = siteId;
		this.timeId = timeId;
	}
	
	@Override
	public JobResult doRun() throws AnalyticsException {

		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		SiteCategoryListConfig siteCategoryListConfig = statisticsService.getSiteCategoryListConfig();

		List<SiteCategoryConfig> list = siteCategoryListConfig.getList();

		List<String> categoryIdList = new ArrayList<String>();

		for (SiteCategoryConfig config : list) {
			if (config.getSiteId().equals(siteId)) {
				for (CategoryConfig categoryConfig : config.getCategoryList()) {
					categoryIdList.add(categoryConfig.getId());
				}
			}

		}

		/* 1. raw.log */
		Calendar calendar = SearchStatisticsProperties.parseTimeId(timeId);
		TimeSchedule schedule = new TimeSchedule(calendar.getTimeInMillis(), 0);
		DailySearchLogAnalyticsTask task = new DailySearchLogAnalyticsTask(siteId, categoryIdList, schedule, 0, null);
		task.setEnvironment(environment);
		JobService.getInstance().offer(task);
		
		/* 2. type_raw.log */
		TimeSchedule schedule2 = new TimeSchedule(calendar.getTimeInMillis(), 0);
		DailyTypeSearchLogAnalyticsTask task2 = new DailyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule2, 0, null);
		task2.setEnvironment(environment);
		JobService.getInstance().offer(task2);
			
		return new JobResult(true);
	}

}
