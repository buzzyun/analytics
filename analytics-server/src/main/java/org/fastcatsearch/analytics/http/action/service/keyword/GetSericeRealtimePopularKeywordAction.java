package org.fastcatsearch.analytics.http.action.service.keyword;

import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.vo.RankKeyword;
import org.fastcatsearch.analytics.http.ActionMapping;
import org.fastcatsearch.analytics.http.action.ActionRequest;
import org.fastcatsearch.analytics.http.action.ActionResponse;
import org.fastcatsearch.analytics.http.action.ServiceAction;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.ResponseWriter;

@ActionMapping("/service/keyword/popular/rt")
public class GetSericeRealtimePopularKeywordAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {

		writeHeader(response);
		ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());
		responseWriter.object();

		String siteId = request.getParameter("siteId");
		String categoryId = request.getParameter("categoryId");
		String errorMessage = null;

		try {
			StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
			List<RankKeyword> list = statisticsService.getRealtimePopularKeywordList(siteId, categoryId);
			
			responseWriter.key("siteId").value(siteId);
			responseWriter.key("categoryId").value(categoryId);
			
			responseWriter.key("list").array();
			if (list != null) {
				
				for (RankKeyword vo : list) {
					responseWriter.object();
					responseWriter.key("rank").value(vo.getRank());
					responseWriter.key("word").value(vo.getKeyword());
					responseWriter.key("diffType").value(vo.getRankDiffType().name());
					responseWriter.key("diff").value(vo.getRankDiff());
					responseWriter.key("count").value(vo.getCount());
					responseWriter.key("countDiff").value(vo.getCountDiff());
					responseWriter.endObject();
				}
			}
			responseWriter.endArray();
			
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
