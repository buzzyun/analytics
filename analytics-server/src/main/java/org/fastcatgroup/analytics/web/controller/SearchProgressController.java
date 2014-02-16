package org.fastcatgroup.analytics.web.controller;

import java.util.List;

import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report/progress")
public class SearchProgressController extends AbstractController {

	@RequestMapping("/index")
	public ModelAndView index(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(required=false) String timeFrom, @RequestParam(required=false) String timeTo) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/index");
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchHitMapper> mapperSession = dbService.getMapperSession(SearchHitMapper.class);
		
		try {
			SearchHitMapper mapper = mapperSession.getMapper();
			List<SearchHitVO> list = null;
			if(timeFrom != null && timeTo != null){
				list = mapper.getEntryListBetween(siteId, categoryId, timeFrom, timeTo);
			}
			mav.addObject("categoryId", categoryId);
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
	
	@RequestMapping("/keyword")
	public ModelAndView keyword(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(required=false) String timeFrom, @RequestParam(required=false) String timeTo) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/keyword");
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchHitMapper> mapperSession = dbService.getMapperSession(SearchHitMapper.class);
		
		try {
			SearchHitMapper mapper = mapperSession.getMapper();
			List<SearchHitVO> list = null;
			if(timeFrom != null && timeTo != null){
				list = mapper.getEntryListBetween(siteId, categoryId, timeFrom, timeTo);
			}
			mav.addObject("categoryId", categoryId);
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
	
	
	//관심어추이.
	@RequestMapping("/myKeyword")
	public ModelAndView myKeyword(@PathVariable String siteId) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/myKeyword");
		return mav;
	}
		
	
	//응답시간추이
	@RequestMapping("/responseTime")
	public ModelAndView responseTime(@PathVariable String siteId) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/responseTime");
		return mav;
	}
	

		

}
