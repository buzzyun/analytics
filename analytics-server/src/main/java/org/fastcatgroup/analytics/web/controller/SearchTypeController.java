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
	@RequestMapping("/index")
	public ModelAndView viewCategory(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(defaultValue="category") String typeId, @RequestParam(required=false) String timeText
			, @RequestParam(required = false) String timeViewType) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/type/index");
		
		if(timeText == null){
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			String timeTo = SearchStatisticsProperties.toDatetimeString(calendar);
			timeText = timeTo + " - " + timeTo;
		}
		
		int timeTypeCode = Calendar.DATE;
		
		if("H".equalsIgnoreCase(timeViewType)) {
			timeTypeCode = Calendar.HOUR_OF_DAY;
		} else if("W".equalsIgnoreCase(timeViewType)) {
			timeTypeCode = Calendar.WEEK_OF_YEAR;
		} else if("M".equalsIgnoreCase(timeViewType)) {
			timeTypeCode = Calendar.MONTH;
		} else if("Y".equalsIgnoreCase(timeViewType)) {
			timeTypeCode = Calendar.YEAR;
		} else {
			timeViewType = "D";
		}
		
		String[] timeRanges = timeText.split("-");
		logger.debug("timeRanges > {} >> {}", timeRanges, timeText);
		
		String timeFrom = null;
		String timeTo = null;
		if(timeRanges.length == 1){
			timeFrom = timeRanges[0];
			timeTo = timeFrom;
		}else if(timeRanges.length == 2){
			timeFrom = timeRanges[0];
			timeTo = timeRanges[1];
		}else{
			//error
		}
		logger.debug("timeFrom > {} ~~ {}", timeFrom, timeTo);
		Calendar startTime = SearchStatisticsProperties.parseDatetimeString(timeFrom);
		Calendar endTime = SearchStatisticsProperties.parseDatetimeString(timeTo);
		String startTimeId = SearchStatisticsProperties.getTimeId(startTime, timeTypeCode);
		String endTimeId = SearchStatisticsProperties.getTimeId(endTime, timeTypeCode);
		logger.debug("New time id >> {} ~ {} > {}", startTimeId, endTimeId, timeViewType);
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchTypeHitMapper> mapperSession = dbService.getMapperSession(SearchTypeHitMapper.class);
		
		try {
			SearchTypeHitMapper mapper = mapperSession.getMapper();
			List<SearchTypeHitVO> list = mapper.getTypeCountListBetween(siteId, categoryId, typeId, timeFrom, timeTo);
			
			mav.addObject("categoryId", categoryId);
			mav.addObject("timeText", timeText);
			mav.addObject("timeViewType", timeViewType);
			mav.addObject("typeId", typeId);
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
