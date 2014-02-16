package org.fastcatgroup.analytics.http.action.service.management;

import java.util.ArrayList;
import java.util.List;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.CategoryConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.analysis.schedule.TimeSchedule;
import org.fastcatgroup.analytics.analysis.task.RealtimeSearchLogAnalyticsTask;
import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping("/management/run/realtime-search-log-analytics-task")
public class RealtimeSearchLogAnalyticsTaskRunAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {

		String siteId = request.getParameter("siteId");
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

			TimeSchedule schedule = new TimeSchedule(System.currentTimeMillis(), 0);
			RealtimeSearchLogAnalyticsTask task = new RealtimeSearchLogAnalyticsTask(siteId, categoryIdList, schedule, 0, null);
			task.setEnvironment(environment);

			JobService.getInstance().offer(task);
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
