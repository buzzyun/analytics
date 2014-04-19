package org.fastcatgroup.analytics.job.task;

import java.util.ArrayList;
import java.util.List;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.CategorySetting;
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
