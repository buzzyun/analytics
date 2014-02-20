package org.fastcatgroup.analytics.http.action.service.management;

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
import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping("/management/run/daily-search-log-analytics-task")
public class DailySearchLogAnalyticsTaskRunAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {

		String siteId = request.getParameter("siteId");
		String timeId = request.getParameter("timeId");
		String errorMessage = null;
		writeHeader(response);
		ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());
		responseWriter.object();
		try {
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
			
		} catch (Exception e) {
			errorMessage = e.getMessage();
		} finally {
			if (errorMessage != null) {
				responseWriter.key("errorMessage").value(errorMessage);
			}
			responseWriter.endObject();
			responseWriter.done();
		}

	}

}