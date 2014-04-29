package org.fastcatsearch.analytics.job.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatsearch.analytics.analysis.schedule.SimpleTaskRunner;
import org.fastcatsearch.analytics.analysis.schedule.TimeSchedule;
import org.fastcatsearch.analytics.analysis.task.DailyClickLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.DailySearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.DailyTypeSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.HourlySearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.MonthlyClickLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.MonthlySearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.MonthlyTypeSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.NDaysClickLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.RelateSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.WeeklySearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.WeeklyTypeSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.YearlySearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.YearlyTypeSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.control.JobService;
import org.fastcatsearch.analytics.exception.AnalyticsException;
import org.fastcatsearch.analytics.job.Job;
import org.fastcatsearch.analytics.service.ServiceManager;

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
		
		if (timeId1 != null && !"".equals(timeId1)) {
			
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
				
				Calendar weekEnd = (Calendar) currentDay.clone();
				Calendar monthEnd = (Calendar) currentDay.clone();
				
				weekEnd.set(Calendar.DAY_OF_WEEK, 7);
				weekEnd.add(Calendar.DAY_OF_MONTH, 1);
				
				monthEnd.add(Calendar.MONTH, 1);
				monthEnd.add(Calendar.DAY_OF_MONTH, -1);
			
				SimpleTaskRunner taskRunner = new SimpleTaskRunner("search-log-task-runner", JobService.getInstance(), environment);
				
				int taskSequence = 0;
				/********************
				 **  search log 
				 ********************/
				/* 1. Hourly */
				TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				HourlySearchLogAnalyticsTask task = new HourlySearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++, null);
				taskRunner.addTask(task);
			
				/* 2. Daily */
				TimeSchedule schedule1 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				DailySearchLogAnalyticsTask task1 = new DailySearchLogAnalyticsTask(siteId, categoryIdList, schedule1, taskSequence++, null);
				taskRunner.addTask(task1);
				
				TimeSchedule schedule2 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				DailyTypeSearchLogAnalyticsTask task2 = new DailyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule2, taskSequence++, null);
				taskRunner.addTask(task2);
				
				
				/* 3. Weekly */
				TimeSchedule schedule3 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				WeeklySearchLogAnalyticsTask task3 = new WeeklySearchLogAnalyticsTask(siteId, categoryIdList, schedule3, taskSequence++);
				taskRunner.addTask(task3);
				
				TimeSchedule schedule4 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				WeeklyTypeSearchLogAnalyticsTask task4 = new WeeklyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule4, taskSequence++);
				taskRunner.addTask(task4);
				
				
				/* 4. Monthly */
				TimeSchedule schedule5 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				MonthlySearchLogAnalyticsTask task5 = new MonthlySearchLogAnalyticsTask(siteId, categoryIdList, schedule5, taskSequence++);
				taskRunner.addTask(task5);
				
				TimeSchedule schedule6 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				MonthlyTypeSearchLogAnalyticsTask task6 = new MonthlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule6, taskSequence++);
				taskRunner.addTask(task6);
				
				/* 5. Yearly */
				TimeSchedule schedule7 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				YearlySearchLogAnalyticsTask task7 = new YearlySearchLogAnalyticsTask(siteId, categoryIdList, schedule7, taskSequence++);
				taskRunner.addTask(task7);
				
				TimeSchedule schedule8 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				YearlyTypeSearchLogAnalyticsTask task8 = new YearlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule8, taskSequence++);
				taskRunner.addTask(task8);
			
				/********************
				 ** 연관검색어
				 ********************/
				TimeSchedule schedule12 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				RelateSearchLogAnalyticsTask task12 = new RelateSearchLogAnalyticsTask(siteId, categoryIdList, schedule12, taskSequence++);
				taskRunner.addTask(task12);
				
				taskRunner.start();
				try {
					taskRunner.join();
				} catch (InterruptedException e) {
					logger.error("", e);
				}
				
				/********************
				 **  click log 
				 ********************/
				
				/* 1. Daily */
				TimeSchedule schedule9 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				DailyClickLogAnalyticsTask task9 = new DailyClickLogAnalyticsTask(siteId, categoryIdList, schedule9, taskSequence++);
				taskRunner.addTask(task9);
				
				/* 2. Monthly */
				TimeSchedule schedule10 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				MonthlyClickLogAnalyticsTask task10 = new MonthlyClickLogAnalyticsTask(siteId, categoryIdList, schedule10, taskSequence++);
				taskRunner.addTask(task10);
				
				/* 3. N-Days */
				TimeSchedule schedule11 = new TimeSchedule(currentDay.getTimeInMillis(), 0);
				NDaysClickLogAnalyticsTask task11 = new NDaysClickLogAnalyticsTask(siteId, categoryIdList, schedule11, taskSequence++);
				taskRunner.addTask(task11);
				
				logger.info("#####################################");
				logger.info("#### Done {}, time = {}s", StatisticsUtils.toDatetimeString(currentDay), (System.currentTimeMillis() -st) / 1000);
				logger.info("#####################################");
				
				currentDay.add(Calendar.DAY_OF_MONTH, 1);
				
			}
		}
		
		return new JobResult(true);
	}

}
