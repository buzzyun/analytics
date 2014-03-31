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
import org.fastcatgroup.analytics.analysis.task.MonthlySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.MonthlyTypeSearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.WeeklySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.WeeklyTypeSearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.YearlySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.YearlyTypeSearchLogAnalyticsTask;
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
		
		if (timeId != null && !"".equals(timeId)) {
			
			Calendar calendar = SearchStatisticsProperties.parseTimeId(timeId);
			
//			switch (timeId.toUpperCase().charAt(0)) {
//			
//				case 'D':
				/* 1. raw.log */
				TimeSchedule schedule = new TimeSchedule(calendar.getTimeInMillis(), 0);
				DailySearchLogAnalyticsTask task = new DailySearchLogAnalyticsTask(siteId, categoryIdList, schedule, 0, null);
				task.setEnvironment(environment);
				JobService.getInstance().offer(task);
				
				/* 2. type_raw.log */
				TimeSchedule schedule2 = new TimeSchedule(calendar.getTimeInMillis(), 0);
				DailyTypeSearchLogAnalyticsTask task2 = new DailyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule2, 1, null);
				task2.setEnvironment(environment);
				JobService.getInstance().offer(task2);
				
//				break;
//				
//				case 'W':
				/* weekly log */
				TimeSchedule schedule3 = new TimeSchedule(calendar.getTimeInMillis(), 0);
				WeeklySearchLogAnalyticsTask task3 = new WeeklySearchLogAnalyticsTask(siteId, categoryIdList, schedule3, 2);
				task3.setEnvironment(environment);
				JobService.getInstance().offer(task3);
//				
//				/* weekly type log */
//				TimeSchedule schedule4 = new TimeSchedule(calendar.getTimeInMillis(), 0);
//				WeeklyTypeSearchLogAnalyticsTask task4 = new WeeklyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule4, 3);
//				task4.setEnvironment(environment);
//				JobService.getInstance().offer(task4);
//				
////				break;
////				
////				case 'M':
//				/* monthly log */
//				TimeSchedule schedule5 = new TimeSchedule(calendar.getTimeInMillis(), 0);
//				MonthlySearchLogAnalyticsTask task5 = new MonthlySearchLogAnalyticsTask(siteId, categoryIdList, schedule5, 4);
//				task5.setEnvironment(environment);
//				JobService.getInstance().offer(task5);
//				
//				/* monthly type log */
//				TimeSchedule schedule6 = new TimeSchedule(calendar.getTimeInMillis(), 0);
//				MonthlyTypeSearchLogAnalyticsTask task6 = new MonthlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule6, 5);
//				task6.setEnvironment(environment);
//				JobService.getInstance().offer(task6);
//				
////				break;
//				
////				case 'Y':
//				/* yearly log */
//				TimeSchedule schedule7 = new TimeSchedule(calendar.getTimeInMillis(), 0);
//				YearlySearchLogAnalyticsTask task7 = new YearlySearchLogAnalyticsTask(siteId, categoryIdList, schedule7, 6);
//				task7.setEnvironment(environment);
//				JobService.getInstance().offer(task7);
//				
//				/* yearly type log */
//				TimeSchedule schedule8 = new TimeSchedule(calendar.getTimeInMillis(), 0);
//				YearlyTypeSearchLogAnalyticsTask task8 = new YearlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule8, 7);
//				task8.setEnvironment(environment);
//				JobService.getInstance().offer(task8);
////			}
		}
		
		return new JobResult(true);
	}

}
