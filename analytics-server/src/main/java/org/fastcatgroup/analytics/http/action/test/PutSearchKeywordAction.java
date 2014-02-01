package org.fastcatgroup.analytics.http.action.test;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.management.Query;

import org.fastcatgroup.analytics.analysis.CategoryStatistics;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping("/test/put-search-keyword")
public class PutSearchKeywordAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {
		String category = request.getParameter("category");
		String keyword = request.getParameter("keyword");
		String prevKeyword = request.getParameter("prevKeyword");
		
		StatisticsService searchStatisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		
		searchStatisticsService.searchStatistics().add(keyword);
		
		CategoryStatistics categoryStatistics = searchStatisticsService.categoryStatistics(category);
		
		int lastCount = categoryStatistics.getLastCount();
		logger.debug("categoryStatistics.getLastCount() > {}", categoryStatistics.getLastCount());
		
		Writer writer = response.getWriter();
		writeHeader(response);
		
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		responseWriter.object()
		.key("success").value(true)
		.key("lastCount").value(lastCount)
		.endObject();
		responseWriter.done();
	}

}
