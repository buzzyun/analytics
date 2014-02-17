package org.fastcatgroup.analytics.web.controller;

import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatgroup.analytics.db.vo.SearchTypeHitVO;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report/type")
public class SearchTypeController extends AbstractController {

	//카테고리별 비율보기.
	@RequestMapping("/{typeId}")
	public ModelAndView viewCategory(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @PathVariable String typeId, @RequestParam(required=false) String timeFrom, @RequestParam(required=false) String timeTo) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/type/index");
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchTypeHitMapper> mapperSession = dbService.getMapperSession(SearchTypeHitMapper.class);
		
		
		try {
			SearchTypeHitMapper mapper = mapperSession.getMapper();
			List<SearchTypeHitVO> list = null;
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			String defaultTimeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.DATE);
			if(timeFrom == null){
				timeFrom = defaultTimeId;
			}
			if(timeTo == null){
				timeTo = defaultTimeId;
			}
			
			list = mapper.getTypeCountListBetween(siteId, categoryId, typeId, timeFrom, timeTo);
			
			mav.addObject("categoryId", categoryId);
			mav.addObject("timeFrom", timeFrom);
			mav.addObject("timeTo", timeTo);
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
	
	
}
