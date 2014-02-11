package org.fastcatgroup.analytics.web.controller;

import java.util.List;

import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.CategoryConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.vo.RelateKeywordVO;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.json.JSONStringer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/report")
public class ReportMainController extends AbstractController {

	@RequestMapping("/index")
	public ModelAndView index() {
		logger.debug("ServiceManager111222 > {}", ServiceManager.getInstance());
		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/index");
		return mav;
	}
	
	@RequestMapping("/dashboard")
	public ModelAndView dashboard() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/dashboard");
		return mav;
	}
	
	
	@RequestMapping("/siteList")
	@ResponseBody
	public String getSiteList() {
		List<SiteCategoryConfig> siteCategoryList = getSiteCategoryListConfig();
		JSONStringer s = new JSONStringer();
		s.array();
		for(SiteCategoryConfig config : siteCategoryList){
			s.object().key(config.getSiteId()).value(config.getSiteName()).endObject();
		}
		s.endArray();
		return s.toString();
	}
	
	@RequestMapping("/categoryList")
	@ResponseBody
	public String getCategoryList(@RequestParam String siteId) {
		List<SiteCategoryConfig> siteCategoryList = getSiteCategoryListConfig();
		JSONStringer s = new JSONStringer();
		s.array();
		for(SiteCategoryConfig config : siteCategoryList){
			if(config.getSiteId().equals(siteId)){
				List<CategoryConfig> list = config.getCategoryList();
				for(CategoryConfig categoryConfig : list){
					s.object().key(categoryConfig.getId()).value(categoryConfig.getName()).endObject();
				}
				break;
			}
		}
		s.endArray();
		return s.toString();
	}
	
	
	
	
	@RequestMapping("/keyword/{keywordId}/index")
	public ModelAndView keywordIndex(@PathVariable String keywordId) {
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("report/keyword/index");
		mav.addObject("keywordId", keywordId);
		return mav;
		
	}
	
	@RequestMapping("/keyword/{keywordId}/list")
	public ModelAndView relateKeywordList(@PathVariable String keywordId
			, @RequestParam(defaultValue = "1") Integer pageNo
			, @RequestParam(required = false) String category 
			, @RequestParam(required = false) String keyword
			, @RequestParam(required = false) String searchColumn
			, @RequestParam(required = false) Boolean exactMatch
			, @RequestParam(required = false) Boolean isEditable
			, @RequestParam String targetId, @RequestParam(required = false) String deleteIdList) throws Exception {
		
		JSONStringer stringer = null;;
		int PAGE_SIZE = 10;
		
		ModelAndView mav = new ModelAndView();
		MapperSession<RelateKeywordMapper> mapperSession = null;
		
		try {
			stringer = new JSONStringer();
			AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			mapperSession = dbService.getMapperSession(RelateKeywordMapper.class);
			RelateKeywordMapper mapper = mapperSession.getMapper();
			
			int start = 0;
			int end = 0;
			
			if(pageNo > 0){
				start = (pageNo - 1) * PAGE_SIZE + 1;
			}
			end = start + PAGE_SIZE;
			
			String whereCondition = "";
			
			int totalSize = mapper.getCount(category);
			int filteredSize = mapper.getCountByWhereCondition(category, whereCondition);
			List<RelateKeywordVO> entryList = mapper
				.getEntryListByWhereCondition(category, whereCondition
				, start, end);
			
			stringer.object()
				.key("totalSize").value(totalSize)
				.key("filteredSize").value(filteredSize)
				.key(keywordId).array();
			for(RelateKeywordVO vo : entryList) {
				stringer.object()
				.key("KEYWORD").value(vo.getKeyword())
				.key("VALUE").value(vo.getValue()).endObject();
			}
			stringer.endArray();
			mav.addObject("keywordId",keywordId);
			mav.addObject("list",stringer.toString());
			mav.addObject("start",1);
			mav.addObject("targetId",targetId);
			mav.addObject("pageNo",pageNo);
			mav.addObject("totalSize",totalSize);
			mav.addObject("pageSize",PAGE_SIZE);
			mav.setViewName("report/keyword/"+keywordId+"Keyword");
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		return mav;
	}
}
