package org.fastcatgroup.analytics.web.controller;

import java.util.List;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/report/rank")
public class SearchRankController extends AbstractController {

	@RequestMapping("/realtimeSearchKeyword")
	public ModelAndView realtimeSearchKeyword(@RequestParam(required=false) String siteId, @RequestParam(required=false) String categoryId) {
		ModelAndView mav = new ModelAndView();
		
		List<SiteCategoryConfig> siteCategoryList = getSiteCategoryListConfig();
		mav.addObject("siteCategoryList", siteCategoryList);
		List<RankKeyword> rankList = null;
		if(siteId != null){
			rankList = getStatisticsService().getRealtimePopularKeywordList(siteId, categoryId);
		}
		mav.addObject("rankList", rankList);
		mav.setViewName("report/rank/realtimeSearchKeyword");
		return mav;
	}
	
	@RequestMapping("/searchKeyword")
	public ModelAndView searchKeyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/rank/searchKeyword");
		return mav;
	}
	
	
	//관심어순위
	@RequestMapping("/myKeyword")
	public ModelAndView myKeyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/rank/myKeyword");
		return mav;
	}
	
	//급상승검색어순위
	@RequestMapping("/hotKeyword")
	public ModelAndView hotKeyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/rank/hotKeyword");
		return mav;
	}
	
	//신규검색어순위
	@RequestMapping("/newKeyword")
	public ModelAndView newKeyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/rank/newKeyword");
		return mav;
	}
		
		
}
