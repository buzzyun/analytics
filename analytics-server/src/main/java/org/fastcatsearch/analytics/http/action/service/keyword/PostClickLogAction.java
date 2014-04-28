package org.fastcatsearch.analytics.http.action.service.keyword;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.http.ActionMapping;
import org.fastcatsearch.analytics.http.action.ActionRequest;
import org.fastcatsearch.analytics.http.action.ActionResponse;
import org.fastcatsearch.analytics.http.action.ServiceAction;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.ResponseWriter;


/**
 * 검색시 이 액션을 호출하여 클릭로그데이터를 기록한다.
 * 
 * */
@ActionMapping("/service/ctr/click/post")
public class PostClickLogAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {

		writeHeader(response);
		ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());
		responseWriter.object();

		String siteId = request.getParameter("siteId");
		
		String keyword = request.getParameter("keyword");
		String clickId = request.getParameter("clickId");
		String clickType = request.getParameter("clickType");
		
		StatisticsService service = ServiceManager.getInstance().getService(StatisticsService.class);
		SiteListSetting siteConfig = service.getSiteListSetting();
		
		//siteid 가 지정되지 않았을 경우 자동으로 입력해 줌.
		if (siteConfig.getSiteList().size() == 1 && (siteId == null || "".equals(siteId))) {
			SiteSetting cateConfig = siteConfig.getSiteList().get(0);
			siteId = cateConfig.getId();
		}
		
		if(siteId == null || siteId.trim().length() == 0|| keyword == null || keyword.trim().length() == 0 || clickType == null || clickType.trim().length() == 0){
			//siteId, keyword, clickType 중에 하나라도 비어있으면 로그를 남기지 않음.
			return;
		}

		String errorMessage = null;
		try {
			/* click_raw.log */
			
			//keyword가 null이거나 20보다 크면 버린다.
			//tab글자는 space로 치환한다.
			if(keyword == null || keyword.length() > 20){
				keyword = "";
			}else{
				if(keyword.contains("\t")){
					keyword = keyword.replaceAll("\t", " ");
				}
			}
			if(clickId == null || clickId.length() == 0){
				clickId = "-";
			}
			
			service.addClickLog(siteId, keyword, clickId, clickType);
			
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
