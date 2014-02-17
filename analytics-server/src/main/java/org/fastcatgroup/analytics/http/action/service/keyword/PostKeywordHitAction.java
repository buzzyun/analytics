package org.fastcatgroup.analytics.http.action.service.keyword;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
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
		
		StatisticsService service = ServiceManager.getInstance().getService(StatisticsService.class);
		SiteCategoryListConfig siteConfig = service.getSiteCategoryListConfig();
		
		//siteid 가 지정되지 않았을 경우 자동으로 입력해 줌.
		if (siteConfig.getList().size() == 1 && (siteId == null || "".equals(siteId))) {
			SiteCategoryConfig cateConfig = siteConfig.getList().get(0);
			siteId = cateConfig.getSiteId();
		}
		
		if(type == null || type.trim().length() == 0 || siteId == null || siteId.trim().length() == 0 ){
			return;
		}

		String errorMessage = null;
		try {
			/* 1. raw.log */
			String categoryId = request.getParameter("categoryId");
			if(categoryId == null){
				categoryId = "";
			}
			String keyword = request.getParameter("keyword");
			String prevKeyword = request.getParameter("prev");
			String reponseTime = request.getParameter("resptime");
			service.addLog(type, siteId, categoryId, keyword, prevKeyword, reponseTime);

			/* 2. type_raw.log */
			String typeCategory = categoryId;
			String typePage = request.getParameter("page");
			String typeSort = request.getParameter("sort");
			String typeAge = request.getParameter("age");
			String typeService = request.getParameter("service");
			String typeLogin = request.getParameter("login");
			String typeGender = request.getParameter("gender");
			service.addTypeLog(type, siteId, categoryId, keyword, typeCategory, typePage, typeSort, typeAge, typeService, typeLogin, typeGender);
			
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
