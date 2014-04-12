package org.fastcatgroup.analytics.job.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.CategoryConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.analysis.schedule.ScheduledTaskRunner;
import org.fastcatgroup.analytics.analysis.schedule.SimpleTaskRunner;
import org.fastcatgroup.analytics.analysis.schedule.TimeSchedule;
import org.fastcatgroup.analytics.analysis.task.DailySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.DailyTypeSearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.HourlySearchLogAnalyticsTask;
import org.fastcatgroup.analytics.analysis.task.MonthlyClickLogAnalyticsTask;
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
	private String timeId1;
	private String timeId2;
	
	public DailySearchLogAnalyticsTaskRunJob(){
	}
	
	public DailySearchLogAnalyticsTaskRunJob(String siteId, String timeId1, String timeId2){
		this.siteId = siteId;
		this.timeId1 = timeId1;
		this.timeId2 = timeId2;
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
		
		if (timeId1 != null && !"".equals(timeId1)) {
			
			
			Calendar calendar1 = SearchStatisticsProperties.parseTimeId(timeId1);
			Calendar calendar2 = SearchStatisticsProperties.parseTimeId(timeId2);
			
			SearchStatisticsProperties.setTimeFrom(calendar1);
			SearchStatisticsProperties.setTimeTo(calendar2);
			
			while(calendar1.before(calendar2)) {
				logger.info("CALCULATING..{}", SearchStatisticsProperties.toDatetimeString(calendar1));
			
				SimpleTaskRunner taskRunner = new SimpleTaskRunner("search-log-task-runner", JobService.getInstance(), environment);
				/* 1. raw.log */
				TimeSchedule schedule = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				HourlySearchLogAnalyticsTask task = new HourlySearchLogAnalyticsTask(siteId, categoryIdList, schedule, 0, null);
				taskRunner.addTask(task);
			
				/* 1. raw.log */
				TimeSchedule schedule1 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				DailySearchLogAnalyticsTask task1 = new DailySearchLogAnalyticsTask(siteId, categoryIdList, schedule1, 0, null);
				taskRunner.addTask(task1);
				
				/* 2. type_raw.log */
				TimeSchedule schedule2 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				DailyTypeSearchLogAnalyticsTask task2 = new DailyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule2, 1, null);
				taskRunner.addTask(task2);
				
				/* weekly log */
				TimeSchedule schedule3 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				WeeklySearchLogAnalyticsTask task3 = new WeeklySearchLogAnalyticsTask(siteId, categoryIdList, schedule3, 2);
				taskRunner.addTask(task3);
				
				/* weekly type log */
				TimeSchedule schedule4 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				WeeklyTypeSearchLogAnalyticsTask task4 = new WeeklyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule4, 3);
				taskRunner.addTask(task4);
				
				/* monthly log */
				TimeSchedule schedule5 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				MonthlySearchLogAnalyticsTask task5 = new MonthlySearchLogAnalyticsTask(siteId, categoryIdList, schedule5, 4);
				taskRunner.addTask(task5);
				
				/* monthly type log */
				TimeSchedule schedule6 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				MonthlyTypeSearchLogAnalyticsTask task6 = new MonthlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule6, 5);
				taskRunner.addTask(task6);
				
				/* yearly log */
				TimeSchedule schedule7 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				YearlySearchLogAnalyticsTask task7 = new YearlySearchLogAnalyticsTask(siteId, categoryIdList, schedule7, 6);
				taskRunner.addTask(task7);
				
				/* yearly type log */
				TimeSchedule schedule8 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				YearlyTypeSearchLogAnalyticsTask task8 = new YearlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule8, 7);
				taskRunner.addTask(task8);
				
				TimeSchedule schedule9 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				MonthlyClickLogAnalyticsTask task9 = new MonthlyClickLogAnalyticsTask(siteId, categoryIdList, schedule9, 0);
				taskRunner.addTask(task9);
				taskRunner.start();
				
				while(taskRunner.queueSize() > 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ignore) { 
						break;
					}
				}
				
				calendar1.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		
		return new JobResult(true);
	}

}
