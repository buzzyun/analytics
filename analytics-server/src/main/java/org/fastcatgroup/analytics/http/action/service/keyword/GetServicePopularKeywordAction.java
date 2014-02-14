package org.fastcatgroup.analytics.http.action.service.keyword;

import java.text.SimpleDateFormat;
import java.util.List;

import org.fastcatgroup.analytics.db.vo.RankKeywordVO;
import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.keyword.KeywordDictionary;
import org.fastcatgroup.analytics.keyword.KeywordDictionary.KeywordDictionaryType;
import org.fastcatgroup.analytics.keyword.KeywordService;
import org.fastcatgroup.analytics.keyword.PopularKeywordDictionary;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping("/service/keyword/popular")
public class GetServicePopularKeywordAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {
		KeywordService keywordService = ServiceManager.getInstance().getService(KeywordService.class);

		writeHeader(response);
		ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());
		responseWriter.object();

		String type = request.getParameter("type");
		String categoryId = request.getParameter("category");
		int interval = request.getIntParameter("interval", 1);
		String errorMessage = null;

		try {
			KeywordDictionaryType keywordDictionaryType = KeywordDictionaryType.POPULAR_KEYWORD_REALTIME;
			
			if ("D".equalsIgnoreCase(type)) {
				keywordDictionaryType = KeywordDictionaryType.POPULAR_KEYWORD_DAY;
			} else if ("W".equalsIgnoreCase(type)) {
				keywordDictionaryType = KeywordDictionaryType.POPULAR_KEYWORD_WEEK;
			} else if ("M".equalsIgnoreCase(type)) {
				keywordDictionaryType = KeywordDictionaryType.POPULAR_KEYWORD_MONTH;
			}

			responseWriter.key("category").value(categoryId);
			responseWriter.key("type").value(keywordDictionaryType.name());
			
			
			KeywordDictionary keywordDictionary = keywordService.getKeywordDictionary(categoryId, keywordDictionaryType, interval);

			PopularKeywordDictionary popularKeywordDictionary = (PopularKeywordDictionary) keywordDictionary;
			if (popularKeywordDictionary != null) {

				
				//FIXME 
				
				
				
				List<RankKeywordVO> keywordList = null;// popularKeywordDictionary.getKeywordList();
				if (popularKeywordDictionary.getCreateTime() != null) {
					responseWriter.key("time").value(new SimpleDateFormat("yyyy.MM.dd HH:mm").format(popularKeywordDictionary.getCreateTime()));
				}
				responseWriter.key("list").array();
				for (RankKeywordVO vo : keywordList) {
					responseWriter.object();
					responseWriter.key("word").value(vo.getKeyword());
					responseWriter.key("rank").value(vo.getRank());
					responseWriter.key("diffType").value(vo.getRankDiffType().name());
					responseWriter.key("diff").value(vo.getRankDiff());
					responseWriter.endObject();
				}
				responseWriter.endArray();

			} else {
				throw new Exception("Request popular keyword is not in service");
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
