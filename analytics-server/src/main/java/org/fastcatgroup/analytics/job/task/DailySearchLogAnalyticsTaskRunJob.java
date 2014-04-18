package org.fastcatgroup.analytics.job.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.CategoryConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
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
			
			//dayDiff 가 0 이면, 즉 일일 실행이면 모두 실행한다.
			//다수일을 지정한 경우 에는 선택적으로 실행할 수 있도록 한다.
			int dayDiff = SearchStatisticsProperties.getDateDiff(calendar1, calendar2);
			
			SearchStatisticsProperties.setTimeFrom(calendar1);
			SearchStatisticsProperties.setTimeTo(calendar2);
			
			while(calendar1.before(calendar2)) {
				logger.info("CALCULATING..{}", SearchStatisticsProperties.toDatetimeString(calendar1));
				
				Calendar weekEnd = (Calendar) calendar1.clone();
				Calendar monthEnd = (Calendar) calendar1.clone();
				
				weekEnd.set(Calendar.DAY_OF_WEEK, 7);
				weekEnd.add(Calendar.DAY_OF_MONTH, 1);
				
				monthEnd.add(Calendar.MONTH, 1);
				monthEnd.add(Calendar.DAY_OF_MONTH, -1);
			
				SimpleTaskRunner taskRunner = new SimpleTaskRunner("search-log-task-runner", JobService.getInstance(), environment);
				/* 1. raw.log */
				TimeSchedule schedule = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				HourlySearchLogAnalyticsTask task = new HourlySearchLogAnalyticsTask(siteId, categoryIdList, schedule, 0, null);
				taskRunner.addTask(task);
			
				/* 1. raw.log */
				TimeSchedule schedule1 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				DailySearchLogAnalyticsTask task1 = new DailySearchLogAnalyticsTask(siteId, categoryIdList, schedule1, 1, null);
				taskRunner.addTask(task1);
				
				/* 2. type_raw.log */
				TimeSchedule schedule2 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				DailyTypeSearchLogAnalyticsTask task2 = new DailyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule2, 2, null);
				taskRunner.addTask(task2);
				
				if (weekEnd.equals(calendar1) || dayDiff == 0) {
				
					/* weekly log */
					TimeSchedule schedule3 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
					WeeklySearchLogAnalyticsTask task3 = new WeeklySearchLogAnalyticsTask(siteId, categoryIdList, schedule3, 3);
					taskRunner.addTask(task3);
					
					/* weekly type log */
					TimeSchedule schedule4 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
					WeeklyTypeSearchLogAnalyticsTask task4 = new WeeklyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule4, 4);
					taskRunner.addTask(task4);
				}
				
				if (monthEnd.equals(calendar1) || dayDiff == 0) {
				
					/* monthly log */
					TimeSchedule schedule5 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
					MonthlySearchLogAnalyticsTask task5 = new MonthlySearchLogAnalyticsTask(siteId, categoryIdList, schedule5, 5);
					taskRunner.addTask(task5);
					
					/* monthly type log */
					TimeSchedule schedule6 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
					MonthlyTypeSearchLogAnalyticsTask task6 = new MonthlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule6, 6);
					taskRunner.addTask(task6);
					
					if(calendar1.get(Calendar.MONTH) == 11 || dayDiff == 0) {
						/* yearly log */
						TimeSchedule schedule7 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
						YearlySearchLogAnalyticsTask task7 = new YearlySearchLogAnalyticsTask(siteId, categoryIdList, schedule7, 7);
						taskRunner.addTask(task7);
						
						/* yearly type log */
						TimeSchedule schedule8 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
						YearlyTypeSearchLogAnalyticsTask task8 = new YearlyTypeSearchLogAnalyticsTask(siteId, categoryIdList, schedule8, 8);
						taskRunner.addTask(task8);
					}
				}
			
				/* click log */
				TimeSchedule schedule9 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				DailyClickLogAnalyticsTask task9 = new DailyClickLogAnalyticsTask(siteId, categoryIdList, schedule9, 9);
				taskRunner.addTask(task9);
				
				if (monthEnd.equals(calendar1) || dayDiff == 0) {
					TimeSchedule schedule10 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
					MonthlyClickLogAnalyticsTask task10 = new MonthlyClickLogAnalyticsTask(siteId, categoryIdList, schedule10, 10);
					taskRunner.addTask(task10);
				}
				
				TimeSchedule schedule11 = new TimeSchedule(calendar1.getTimeInMillis(), 0);
				NDaysClickLogAnalyticsTask task11 = new NDaysClickLogAnalyticsTask(siteId, categoryIdList, schedule11, 11);
				taskRunner.addTask(task11);
				
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
