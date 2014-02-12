package org.fastcatgroup.analytics.http.action.management.keyword;

import java.io.Writer;
import java.util.List;

import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.vo.RelateKeywordVO;
import org.fastcatgroup.analytics.http.ActionAuthority;
import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.AuthAction;
import org.fastcatgroup.analytics.keyword.KeywordService;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping(value="/management/keyword/relate/list", authority=ActionAuthority.Keyword)
public class GetRelateKeyWordListAction extends AuthAction {

	@Override
	public void doAuthAction(ActionRequest request, ActionResponse response) throws Exception {
		String categoryId = request.getParameter("category");
		String search = request.getParameter("search");
		int start = request.getIntParameter("start");
		int length = request.getIntParameter("length");
		
		KeywordService keywordService = ServiceManager.getInstance().getService(KeywordService.class);
		
		RelateKeywordMapper relateKeywordMapper = keywordService.getMapperSession(RelateKeywordMapper.class).getMapper();
		
		String whereCondition = "";
		
		if(search!=null && !"".equals(search)) {
			whereCondition = " and keyword = '"+search+"' ";
		}
		
		Writer writer = response.getWriter();
		ResponseWriter resultWriter = getDefaultResponseWriter(writer);
		
		int totalSize = 0;
		int filteredSize = 0;
		
		resultWriter.object().key("list").array();
		
		if(relateKeywordMapper!=null) {
			totalSize = relateKeywordMapper.getCount(categoryId);
			filteredSize = relateKeywordMapper.getCountByWhereCondition(categoryId, whereCondition);
			
			if(length==-1) {
				length = totalSize;
			}
			
			if(totalSize > 0) {
			
				List<RelateKeywordVO> list = relateKeywordMapper.getEntryListByWhereCondition(categoryId, whereCondition, start, length);
				for(RelateKeywordVO vo : list) {
					
					resultWriter.object().key("ID").value(vo.getId())
						.key("KEYWORD").value(vo.getKeyword())
						.key("VALUE").value(vo.getValues()).endObject();
				}
			}
		}
		
		resultWriter.endArray();
		
		resultWriter.key("totalSize").value(totalSize).key("filteredSize").value(filteredSize);
		resultWriter.endObject();
		resultWriter.done();
	}
}