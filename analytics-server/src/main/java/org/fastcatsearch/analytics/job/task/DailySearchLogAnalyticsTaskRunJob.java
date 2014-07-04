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
import org.fastcatsearch.analytics.analysis.task.WeeklyClickLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.WeeklySearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.WeeklyTypeSearchLogAnalyticsTask;
import org.fastcatsearch.analytics.analysis.task.YearlyClickLogAnalyticsTask;
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
	private String[] taskType;
	
	public DailySearchLogAnalyticsTaskRunJob(){
	}
	
	public DailySearchLogAnalyticsTaskRunJob(String siteId, String timeId1, String timeId2, String[] taskType){
		this.siteId = siteId;
		this.timeId1 = timeId1;
		this.timeId2 = timeId2;
		this.taskType = taskType;
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
				
				for(String type : taskType) {
					logger.debug("TaskType > {}", type);
					
					if(type.equals("hour_sp")){
						/* 1. Hourly */
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						HourlySearchLogAnalyticsTask task = new HourlySearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("day_sp")){
						/* 2. Daily */
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						DailySearchLogAnalyticsTask task = new DailySearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("week_sp")){
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						WeeklySearchLogAnalyticsTask task = new WeeklySearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("month_sp")){
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						MonthlySearchLogAnalyticsTask task = new MonthlySearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("year_sp")){
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						YearlySearchLogAnalyticsTask task = new YearlySearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("day_type")){
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						DailyTypeSearchLogAnalyticsTask task = new DailyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("week_type")){
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						WeeklyTypeSearchLogAnalyticsTask task = new WeeklyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("month_type")){
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						MonthlyTypeSearchLogAnalyticsTask task = new MonthlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("year_type")){
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						YearlyTypeSearchLogAnalyticsTask task = new YearlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("day_relate")){
						/********************
						 ** 연관검색어
						 ********************/
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						RelateSearchLogAnalyticsTask task = new RelateSearchLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("day_ctr")){
						/* 1. Daily */
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						DailyClickLogAnalyticsTask task = new DailyClickLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("week_ctr")){
						/* 2. Weekly */
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						WeeklyClickLogAnalyticsTask task = new WeeklyClickLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("month_ctr")){
						/* 3. Monthly */
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						MonthlyClickLogAnalyticsTask task = new MonthlyClickLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("year_ctr")){
						/* 4. Yearly */
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						YearlyClickLogAnalyticsTask task = new YearlyClickLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}else if(type.equals("day_ctr_file")){
						/* 5. N-Days */
						TimeSchedule schedule = new TimeSchedule(currentDay.getTimeInMillis(), 0, false);
						NDaysClickLogAnalyticsTask task = new NDaysClickLogAnalyticsTask(siteId, categoryIdList, schedule, taskSequence++);
						taskRunner.addTask(task);
					}
					
				}
				
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
