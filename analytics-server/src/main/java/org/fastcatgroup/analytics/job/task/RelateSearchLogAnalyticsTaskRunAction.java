package org.fastcatgroup.analytics.job.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.StatisticsUtils;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatgroup.analytics.analysis.schedule.TimeSchedule;
import org.fastcatgroup.analytics.analysis.task.RelateSearchLogAnalyticsTask;
import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping("/management/run/relate-search-log-analytics-task")
public class RelateSearchLogAnalyticsTaskRunAction extends ServiceAction {

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

			/* 연관어. */
			Calendar calendar = StatisticsUtils.parseTimeId(timeId);
			TimeSchedule schedule = new TimeSchedule(calendar.getTimeInMillis(), 0);
			RelateSearchLogAnalyticsTask task = new RelateSearchLogAnalyticsTask(siteId, categoryIdList, schedule, 0);
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
