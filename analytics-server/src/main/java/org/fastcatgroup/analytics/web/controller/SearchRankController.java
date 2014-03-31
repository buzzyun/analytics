package org.fastcatgroup.analytics.web.controller;

import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordEmptyMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO.RankDiffType;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report/rank")
public class SearchRankController extends AbstractController {

	@RequestMapping("/realtimeSearchKeyword")
	public ModelAndView realtimeSearchKeyword(@PathVariable String siteId, @RequestParam(required = false) String categoryId) {
		ModelAndView mav = new ModelAndView();

		List<SiteCategoryConfig> siteCategoryList = getSiteCategoryListConfig();
		mav.addObject("siteCategoryList", siteCategoryList);
		List<RankKeyword> rankList = null;
		if (siteId != null) {
			rankList = getStatisticsService().getRealtimePopularKeywordList(siteId, categoryId);
		}
		mav.addObject("rankList", rankList);
		mav.setViewName("report/rank/realtimeSearchKeyword");
		return mav;
	}

	@RequestMapping("/searchKeywordAll")
	public ModelAndView searchKeywordAll(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(required=false) String timeId, @RequestParam(defaultValue="1") int pageNo
			, @RequestParam(defaultValue="0") int start, @RequestParam(defaultValue="10") int length) {
		int rankDiffOver = 0;
		String rankDiffType = null;
		String menuId = "all";
		return searchKeywordPopular(siteId, categoryId, timeId, menuId, rankDiffType, rankDiffOver, start, length, pageNo);
	}
	
	@RequestMapping("/searchKeywordNew")
	public ModelAndView searchKeywordNew(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(required=false) String timeId, @RequestParam(defaultValue="1") int pageNo
			, @RequestParam(defaultValue="0") int start, @RequestParam(defaultValue="10") int length) {
		
		int rankDiffOver = 0;
		String rankDiffType = RankDiffType.NEW.name();
		String menuId = "new";
		return searchKeywordPopular(siteId, categoryId, timeId, menuId, rankDiffType, rankDiffOver, start, length, pageNo);
	}
	
	@RequestMapping("/searchKeywordHot")
	public ModelAndView searchKeywordHot(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(required=false) String timeId, @RequestParam(defaultValue="1") int pageNo
			, @RequestParam(defaultValue="0") int start, @RequestParam(defaultValue="10") int length) {
		int rankDiffOver = 30;
		String rankDiffType = RankDiffType.UP.name();
		String menuId = "hot";
		return searchKeywordPopular(siteId, categoryId, timeId, menuId, rankDiffType, rankDiffOver, start, length, pageNo);
	}
	
	@RequestMapping("/searchKeywordDown")
	public ModelAndView searchKeywordDown(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(required=false) String timeId, @RequestParam(defaultValue="1") int pageNo
			, @RequestParam(defaultValue="0") int start, @RequestParam(defaultValue="10") int length) {
		int rankDiffOver = 30;
		String rankDiffType = RankDiffType.DN.name();
		String menuId = "down";
		return searchKeywordPopular(siteId, categoryId, timeId, menuId, rankDiffType, rankDiffOver, start, length, pageNo);
	}
	
	@RequestMapping("/searchKeywordEmpty")
	public ModelAndView searchKeywordEmpty(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(required=false) String timeId, @RequestParam(defaultValue="1") int pageNo
			, @RequestParam(defaultValue="0") int start, @RequestParam(defaultValue="10") int length) {
		String menuId = "empty";
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/rank/searchKeyword");
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchKeywordEmptyMapper> mapperSession = dbService.getMapperSession(SearchKeywordEmptyMapper.class);
		try {
			SearchKeywordEmptyMapper mapper = mapperSession.getMapper();
			
			start = (pageNo - 1) * length;
			
			if(timeId == null){
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -1);
				timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.DATE);
			}
			
			int totalCount = mapper.getCount(siteId, categoryId, timeId);
			List<RankKeywordVO> list = mapper.getEntryList(siteId, categoryId, timeId, start, length);
			
			mav.addObject("categoryId", categoryId);
			mav.addObject("start", start);
			mav.addObject("length", length);
			mav.addObject("pageNo", pageNo);
			mav.addObject("timeId", timeId);
			mav.addObject("categoryId", categoryId);
			mav.addObject("menuId", menuId);
			mav.addObject("totalCount", totalCount);
			mav.addObject("list", list);
			
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		
		return mav;
	}
	
	
	
	private ModelAndView searchKeywordPopular(String siteId, String categoryId, String timeId, String menuId
			, String rankDiffType, int rankDiffOver, int start, int length, int pageNo) {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/rank/searchKeyword");
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchKeywordRankMapper> mapperSession = dbService.getMapperSession(SearchKeywordRankMapper.class);
		try {
			SearchKeywordRankMapper mapper = mapperSession.getMapper();
			
			start = (pageNo - 1) * length;
			
			if(timeId == null){
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -1);
				timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.DATE);
			}
			
			int totalCount = mapper.getCount(siteId, categoryId, timeId, rankDiffType, rankDiffOver);
			List<RankKeywordVO> list = mapper.getEntryList(siteId, categoryId, timeId, rankDiffType, rankDiffOver, start, length);
			
			mav.addObject("categoryId", categoryId);
			mav.addObject("start", start);
			mav.addObject("length", length);
			mav.addObject("pageNo", pageNo);
			mav.addObject("timeId", timeId);
			mav.addObject("categoryId", categoryId);
			mav.addObject("menuId", menuId);
			mav.addObject("totalCount", totalCount);
			mav.addObject("list", list);
			
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		
		return mav;
	}
	
	//@RequestMapping("/searchKeyword")
	/*
	public ModelAndView searchKeyword(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(required=false) String timeId, @RequestParam(required=false, defaultValue="all") String typeId
			, @RequestParam(defaultValue="0") int start, @RequestParam(defaultValue="10") int length) {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/rank/searchKeyword");
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchKeywordRankMapper> mapperSession = dbService.getMapperSession(SearchKeywordRankMapper.class);
		try {
			SearchKeywordRankMapper mapper = mapperSession.getMapper();
			if(timeId == null){
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -1);
				timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.DATE);
			}
			
			int rankDiffOver = 0;
			String rankDiffType = null;
			if(typeId.equals("all")){
				rankDiffType = null;
			} else if(typeId.equals("new")){
				rankDiffType = RankDiffType.NEW.name();
			} else if(typeId.equals("hot")){
				rankDiffType = RankDiffType.UP.name();
				rankDiffOver = 30;
			} else if(typeId.equals("down")){
				rankDiffType = RankDiffType.DN.name();
				rankDiffOver = 30;
			}
			
			int totalCount = mapper.getCount(siteId, categoryId, timeId, rankDiffType, rankDiffOver);
			List<RankKeywordVO> list = mapper.getEntryList(siteId, categoryId, timeId, rankDiffType, rankDiffOver, start, length);
			
			mav.addObject("categoryId", categoryId);
			mav.addObject("start", start);
			mav.addObject("length", length);
			mav.addObject("timeId", timeId);
			mav.addObject("typeId", typeId);
			mav.addObject("totalCount", totalCount);
			mav.addObject("list", list);
			
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}

		
		return mav;
	}
	*/
	
}
