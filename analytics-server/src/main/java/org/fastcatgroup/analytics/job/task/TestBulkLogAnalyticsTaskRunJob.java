package org.fastcatgroup.analytics.job.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.StatisticsUtils;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatgroup.analytics.analysis.schedule.SimpleTaskRunner;
import org.fastcatgroup.analytics.analysis.schedule.TimeSchedule;
import org.fastcatgroup.analytics.analysis.task.DailyClickLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.DailySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.DailyTypeSearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.HourlySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.MonthlyClickLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.MonthlySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.MonthlyTypeSearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.NDaysClickLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.WeeklySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.WeeklyTypeSearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.YearlySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.YearlyTypeSearchLogAnalyticsTask;
import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.job.Job;
import org.fastcatgroup.analytics.service.ServiceManager;

public class TestBulkLogAnalyticsTaskRunJob extends Job {
	
	private static final long serialVersionUID = 8365827926796537780L;
	
	private String siteId;
	private String timeId1;
	private String timeId2;
	
	public TestBulkLogAnalyticsTaskRunJob(){
	}
	
	public TestBulkLogAnalyticsTaskRunJob(String siteId, String timeId1, String timeId2){
		this.siteId = siteId;
		this.timeId1 = timeId1;
		this.timeId2 = timeId2;
	}
	
	@Override
	public JobResult doRun() throws AnalyticsException {

		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		SiteListSetting siteCategoryListConfig = statisticsService.getSiteListSetting();

		List<SiteSetting> list = siteCategoryListConfig.getSiteList();

		List<String> categoryIdList = new ArrayList<String>();
		
		for (SiteSetting config : list) {
			if (config.getId().equals(siteId)) {
				for (CategorySetting categoryConfig : config.getStatisticsSettings().getCategoryList()) {
					categoryIdList.add(categoryConfig.getId());
				}
			}
		}
		
		if (timeId1 != null && timeId2 != null) {
			
			Calendar currentDay = StatisticsUtils.parseTimeId(timeId1);
			Calendar lastDay = StatisticsUtils.parseTimeId(timeId2);
			
			StatisticsUtils.setTimeFrom(currentDay);
			StatisticsUtils.setTimeTo(lastDay);
			
			while(currentDay.compareTo(lastDay) <= 0) {
				long st = System.currentTimeMillis();
				logger.info("#####################################");
				logger.info("####");
				logger.info("#### CALCULATING.{} / {}", StatisticsUtils.toDatetimeString(currentDay), StatisticsUtils.toDatetimeString(lastDay));
				logger.info("####");
				logger.info("#####################################");
				SimpleTaskRunner taskRunner = new SimpleTaskRunner("search-log-task-runner", JobService.getInstance(), environment);
				/* 1. raw.log */
				TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				HourlySearchLogAnalyticsTask task = new HourlySearchLogAnalyticsTask(siteId, categoryIdList, schedule, 0, null);
				taskRunner.addTask(task);
			
				/* 1. raw.log */
				TimeSchedule schedule1 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				DailySearchLogAnalyticsTask task1 = new DailySearchLogAnalyticsTask(siteId, categoryIdList, schedule1, 1, null);
				taskRunner.addTask(task1);
				
				/* 2. type_raw.log */
				TimeSchedule schedule2 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				DailyTypeSearchLogAnalyticsTask task2 = new DailyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule2, 2, null);
				taskRunner.addTask(task2);
				
				//
				//last day of week
				//
				if (currentDay.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				
					/* weekly log */
					TimeSchedule schedule3 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
					WeeklySearchLogAnalyticsTask task3 = new WeeklySearchLogAnalyticsTask(siteId, categoryIdList, schedule3, 3);
					taskRunner.addTask(task3);
					
					/* weekly type log */
					TimeSchedule schedule4 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
					WeeklyTypeSearchLogAnalyticsTask task4 = new WeeklyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule4, 4);
					taskRunner.addTask(task4);
				}
				
				//
				//last day of month
				//
				if(currentDay.getActualMaximum(Calendar.DAY_OF_MONTH) == currentDay.get(Calendar.DAY_OF_MONTH)){
				
					/* monthly log */
					TimeSchedule schedule5 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
					MonthlySearchLogAnalyticsTask task5 = new MonthlySearchLogAnalyticsTask(siteId, categoryIdList, schedule5, 5);
					taskRunner.addTask(task5);
					
					/* monthly type log */
					TimeSchedule schedule6 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
					MonthlyTypeSearchLogAnalyticsTask task6 = new MonthlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule6, 6);
					taskRunner.addTask(task6);
					
					
					//
					//last day of year
					//
					if(currentDay.getActualMaximum(Calendar.MONTH) == currentDay.get(Calendar.MONTH)){
						/* yearly log */
						TimeSchedule schedule7 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
						YearlySearchLogAnalyticsTask task7 = new YearlySearchLogAnalyticsTask(siteId, categoryIdList, schedule7, 7);
						taskRunner.addTask(task7);
						
						/* yearly type log */
						TimeSchedule schedule8 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
						YearlyTypeSearchLogAnalyticsTask task8 = new YearlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule8, 8);
						taskRunner.addTask(task8);
					}
				}
			
				/* click log */
				TimeSchedule schedule9 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				DailyClickLogAnalyticsTask task9 = new DailyClickLogAnalyticsTask(siteId, categoryIdList, schedule9, 9);
				taskRunner.addTask(task9);
				
				//
				//last day of month
				//
				if(currentDay.getActualMaximum(Calendar.DAY_OF_MONTH) == currentDay.get(Calendar.DAY_OF_MONTH)){
					TimeSchedule schedule10 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
					MonthlyClickLogAnalyticsTask task10 = new MonthlyClickLogAnalyticsTask(siteId, categoryIdList, schedule10, 10);
					taskRunner.addTask(task10);
				}
				
				TimeSchedule schedule11 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				NDaysClickLogAnalyticsTask task11 = new NDaysClickLogAnalyticsTask(siteId, categoryIdList, schedule11, 11);
				taskRunner.addTask(task11);
				
				taskRunner.start();
				try {
					taskRunner.join();
				} catch (InterruptedException e) {
					logger.error("", e);
				}
				logger.info("#####################################");
				logger.info("#### Done {}, time = {}s", StatisticsUtils.toDatetimeString(currentDay), (System.currentTimeMillis() -st) / 1000);
				logger.info("#####################################");
				
				currentDay.add(Calendar.DAY_OF_MONTH, 1);
				
			}
		}
		
		return new JobResult(true);
	}

}
