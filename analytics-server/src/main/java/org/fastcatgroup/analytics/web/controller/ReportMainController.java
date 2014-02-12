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
	
}
