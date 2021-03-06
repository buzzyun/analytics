package org.fastcatsearch.analytics.web.controller;

import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatsearch.analytics.db.vo.SearchTypeHitVO;
import org.fastcatsearch.analytics.service.ServiceManager;
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
		
		SiteAttribute siteAttribute = ServiceManager.getInstance().getService(StatisticsService.class).getStatisticsSetting(siteId).getSiteAttribute();
		List<TypeSetting> typeArray = siteAttribute.getTypeList();


		if(timeText == null || timeText.trim().equals("")){
			Calendar calendar = StatisticsUtils.getNowCalendar();
			calendar.add(Calendar.DATE, -1);
			String timeTo = StatisticsUtils.toDatetimeString(calendar);
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
		Calendar startTime = StatisticsUtils.parseDatetimeString(timeFrom, true);
		Calendar endTime = StatisticsUtils.parseDatetimeString(timeTo, false);
		String startTimeId = StatisticsUtils.getTimeId(startTime, timeTypeCode);
		String endTimeId = StatisticsUtils.getTimeId(endTime, timeTypeCode);
		logger.debug("New time id >> {} ~ {} > {}", startTimeId, endTimeId, timeViewType);
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchTypeHitMapper> mapperSession = dbService.getMapperSession(SearchTypeHitMapper.class);
		
		try {
			SearchTypeHitMapper mapper = mapperSession.getMapper();
			List<SearchTypeHitVO> list = mapper.getTypeCountListBetween(siteId, categoryId, typeId, startTimeId, endTimeId);
			
			mav.addObject("categoryId", categoryId);
			mav.addObject("timeText", timeText);
			mav.addObject("timeViewType", timeViewType);
			mav.addObject("typeId", typeId);
			mav.addObject("list", list);
			mav.addObject("typeArray", typeArray);
			mav.addObject("today", StatisticsUtils.toDatetimeString(
					StatisticsUtils.getNowCalendar()));
			
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
