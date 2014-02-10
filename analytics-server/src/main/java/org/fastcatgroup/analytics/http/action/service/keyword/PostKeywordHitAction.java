package org.fastcatgroup.analytics.http.action.service.keyword;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;


/**
 * 검색시 이 액션을 호출하여 로그데이터를 기록한다.
 * 
 * */
@ActionMapping("/service/keyword/hit/post")
public class PostKeywordHitAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {

		writeHeader(response);
		ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());
		responseWriter.object();

		String type = request.getParameter("type");
		String siteId = request.getParameter("siteId");
		
		if(type == null || type.trim().length() == 0 || siteId == null || siteId.trim().length() == 0 ){
			
			
			return;
		}
		String categoryId = request.getParameter("categoryId");
		if(categoryId == null){
			categoryId = "";
		}
		String keyword = request.getParameter("keyword");
		String prevKeyword = request.getParameter("prev");
		String errorMessage = null;

		try {

			StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
			
			statisticsService.log(type, siteId, categoryId, keyword, prevKeyword);

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
