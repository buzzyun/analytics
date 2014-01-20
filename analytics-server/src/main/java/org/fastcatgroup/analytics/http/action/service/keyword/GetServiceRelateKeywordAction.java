package org.fastcatgroup.analytics.http.action.service.keyword;

import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.keyword.KeywordDictionary;
import org.fastcatgroup.analytics.keyword.KeywordDictionary.KeywordDictionaryType;
import org.fastcatgroup.analytics.keyword.KeywordService;
import org.fastcatgroup.analytics.keyword.RelateKeywordDictionary;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping("/service/keyword/relate")
public class GetServiceRelateKeywordAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {
		KeywordService keywordService = ServiceManager.getInstance().getService(KeywordService.class);

		writeHeader(response);
		ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());

		String keyword = request.getParameter("keyword");
		String categoryId = request.getParameter("category");
		String errorMessage = null;
		KeywordDictionaryType keywordDictionaryType = KeywordDictionaryType.RELATE_KEYWORD;

		responseWriter.object();
		responseWriter.key("category").value(categoryId);
		responseWriter.key("type").value(KeywordDictionaryType.RELATE_KEYWORD.name());
		
		try {
			KeywordDictionary keywordDictionary = keywordService.getKeywordDictionary(categoryId, keywordDictionaryType);

			RelateKeywordDictionary relateKeywordDictionary = (RelateKeywordDictionary) keywordDictionary;
			if(keyword != null){
				String relateValue = relateKeywordDictionary.getRelateKeyword(keyword);
				responseWriter.key("keyword").value(keyword);
				responseWriter.key("relate").value(relateValue);
			}
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