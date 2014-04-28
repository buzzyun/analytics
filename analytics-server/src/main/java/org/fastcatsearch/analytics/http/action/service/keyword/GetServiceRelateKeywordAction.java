package org.fastcatsearch.analytics.http.action.service.keyword;

import java.util.List;
import java.util.Map;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.http.ActionMapping;
import org.fastcatsearch.analytics.http.action.ActionRequest;
import org.fastcatsearch.analytics.http.action.ActionResponse;
import org.fastcatsearch.analytics.http.action.ServiceAction;
import org.fastcatsearch.analytics.keyword.KeywordDictionary.KeywordDictionaryType;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.ResponseWriter;

@ActionMapping("/service/keyword/relate")
public class GetServiceRelateKeywordAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {

		writeHeader(response);
		ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());

		String keyword = request.getParameter("keyword");
		String siteId = request.getParameter("siteId");
		String errorMessage = null;
		
		StatisticsService service = ServiceManager.getInstance().getService(StatisticsService.class);

		SiteListSetting siteConfig= service.getSiteListSetting();
		
		//siteid 가 지정되지 않았을 경우 자동으로 입력해 줌.
		if (siteConfig.getSiteList().size() == 1 && (siteId == null || "".equals(siteId))) {
			//SiteCategoryConfig cateConfig = siteConfig.getList().get(0);
			SiteSetting cateConfig= siteConfig.getSiteList().get(0);
			siteId = cateConfig.getId();
		}
		
		Map<String, List<String>> relateKeywordMap = service.getRelateKeywordMap(siteId);
		logger.trace("relateKeywordMap:{}.{}.{}", siteId, relateKeywordMap);
		
		responseWriter.object();
		responseWriter.key("siteId").value(siteId);
		responseWriter.key("service").value(KeywordDictionaryType.RELATE_KEYWORD.name());
		
		try {
			if (keyword != null && relateKeywordMap != null) {
				responseWriter.key("keyword").value(keyword);
				responseWriter.key("relate").array();
				List<String> list = relateKeywordMap.get(keyword);
				if(list!=null) {
					for(String value : list) {
						responseWriter.value(value);
					}
				}
				responseWriter.endArray();
			}
		} catch (Exception e) {
			logger.error("",e);
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
