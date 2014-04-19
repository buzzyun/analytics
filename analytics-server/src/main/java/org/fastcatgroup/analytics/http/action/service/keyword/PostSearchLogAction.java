package org.fastcatgroup.analytics.http.action.service.keyword;

import java.util.ArrayList;
import java.util.List;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.TypeSetting;
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
		SiteListSetting siteConfig = service.getSiteListSetting();
		
		//siteid 가 지정되지 않았을 경우 자동으로 입력해 줌.
		if (siteConfig.getSiteList().size() == 1 && (siteId == null || "".equals(siteId))) {
			SiteSetting cateConfig = siteConfig.getSiteList().get(0);
			siteId = cateConfig.getId();
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
			String serviceId = request.getParameter("service");
			
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
			if(serviceId == null || serviceId.length() == 0){
				serviceId = "-";
			}
			service.addLog(type, siteId, categoryId, keyword, prevKeyword, resultCount, reponseTime, serviceId);
			
			SiteAttribute siteAttribute = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId).getSiteAttribute();
			List<TypeSetting> typeList = siteAttribute.getTypeList();
			String[] types = new String[typeList.size() + 2];
			//카테고리 리스트를 설정에 따라 변동될수 있도록.
			for(int inx=0;inx<typeList.size();inx++) {
				String typeData = request.getParameter(typeList.get(inx).getId());
				if(typeData==null || typeData.length() == 0) {
					typeData = "-";
				}
				types[2 + inx] = typeData;
			}
			types[0] = categoryId;
			types[1] = keyword;

			service.addTypeLog(type, siteId, types);
			
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
