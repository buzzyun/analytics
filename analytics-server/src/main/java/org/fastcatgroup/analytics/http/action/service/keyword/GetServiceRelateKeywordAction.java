package org.fastcatgroup.analytics.http.action.service.keyword;

import java.util.List;
import java.util.Map;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.keyword.KeywordDictionary.KeywordDictionaryType;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping("/service/keyword/relate")
public class GetServiceRelateKeywordAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {

		writeHeader(response);
		ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());

		String keyword = request.getParameter("keyword");
		String siteId = request.getParameter("siteId");
		String categoryId = request.getParameter("categoryId");
		String errorMessage = null;
		
		StatisticsService service = ServiceManager.getInstance().getService(StatisticsService.class);
		
		SiteCategoryListConfig siteConfig = service.getSiteCategoryListConfig();
		
		//siteid 가 지정되지 않았을 경우 자동으로 입력해 줌.
		if (siteConfig.getList().size() == 1 && (siteId == null || "".equals(siteId))) {
			SiteCategoryConfig cateConfig = siteConfig.getList().get(0);
			siteId = cateConfig.getSiteId();
		}
		
		Map<String, List<String>> relateKeywordMap = service.getRelateKeywordMap(siteId, categoryId);
		logger.debug("relateKeywordMap:{}.{}.{}", siteId, categoryId, relateKeywordMap);
		
		//KeywordDictionaryType keywordDictionaryType = KeywordDictionaryType.RELATE_KEYWORD;

		responseWriter.object();
		responseWriter.key("categoryId").value(categoryId);
		responseWriter.key("service").value(KeywordDictionaryType.RELATE_KEYWORD.name());
		
		try {
			//KeywordDictionary keywordDictionary = keywordService.getKeywordDictionary(categoryId, keywordDictionaryType);
			//RelateKeywordDictionary relateKeywordDictionary = (RelateKeywordDictionary) keywordDictionary;
			
			if (keyword != null && relateKeywordMap != null) {
				//String relateValue = relateKeywordDictionary.getRelateKeyword(keyword);
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
