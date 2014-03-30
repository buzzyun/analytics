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
public class PostSearchLogAction extends ServiceAction {

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
			String resultCount = request.getParameter("resultCount");
			String reponseTime = request.getParameter("resptime");
			
			//keyword가 null이거나 20보다 크면 버린다.
			//tab글자는 space로 치환한다.
			if(keyword == null || keyword.length() > 20){
				keyword = "";
			}else{
				if(keyword.contains("\t")){
					keyword = keyword.replaceAll("\t", " ");
				}
			}
			if(prevKeyword == null || prevKeyword.length() > 20){
				prevKeyword = "";
			}else{
				if(prevKeyword.contains("\t")){
					prevKeyword = prevKeyword.replaceAll("\t", " ");
				}
			}
			if(resultCount == null || resultCount.length() == 0){
				resultCount = "0";
			}
			if(reponseTime == null || reponseTime.length() == 0){
				reponseTime = "0";
			}
			service.addLog(type, siteId, categoryId, keyword, prevKeyword, resultCount, reponseTime);

			/* 2. type_raw.log */
			String typeCategory = request.getParameter("category");
			String typePage = request.getParameter("page");
			String typeSort = request.getParameter("sort");
			String typeAge = request.getParameter("age");
			String typeService = request.getParameter("service");
			String typeLogin = request.getParameter("login");
			String typeGender = request.getParameter("gender");
			//- 로 전달시 해당 값은 통계에 넣지 않도록 함.
			if(typeCategory == null || typeCategory.length() == 0){
				typeCategory = "-";
			}
			if(typePage == null || typePage.length() == 0){
				typePage = "-";
			}
			if(typeSort == null || typeSort.length() == 0){
				typeSort = "-";
			}
			if(typeAge == null || typeAge.length() == 0){
				typeAge = "-";
			}
			if(typeService == null || typeService.length() == 0){
				typeService = "-";
			}
			if(typeLogin == null || typeLogin.length() == 0){
				typeLogin = "-";
			}
			if(typeGender == null || typeGender.length() == 0){
				typeGender = "-";
			}
			
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
