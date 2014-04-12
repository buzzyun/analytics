package org.fastcatgroup.analytics.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.ClickHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report/ctr")
public class CTRController extends AbstractController {

	@RequestMapping("/view")
	public ModelAndView view(@PathVariable String siteId, @RequestParam(required=false) String timeText) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/view");
		
		Calendar startTime = null;
		Calendar endTime = null;
		
		if(timeText != null) {
			
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
			startTime = SearchStatisticsProperties.parseDatetimeString(timeFrom, true);
			endTime = SearchStatisticsProperties.parseDatetimeString(timeTo, false);
		} else {
			endTime = Calendar.getInstance();
			startTime = Calendar.getInstance();
			startTime.set(Calendar.DATE, 1);
			startTime.add(Calendar.MONTH, -6);
			
			String timeFrom = SearchStatisticsProperties.toDatetimeString(startTime);
			String timeTo = SearchStatisticsProperties.toDatetimeString(endTime);
			timeText = timeFrom + " - " + timeTo;
		}
		
		Calendar startTime2 = (Calendar) startTime.clone();
		
		int timeTypeCode = Calendar.MONTH;
		String startTimeId = SearchStatisticsProperties.getTimeId(startTime, timeTypeCode);
		String endTimeId = SearchStatisticsProperties.getTimeId(endTime, timeTypeCode);
		logger.debug("New time id >> {} ~ {}", startTimeId, endTimeId);
		
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<ClickHitMapper> mapperSession = dbService.getMapperSession(ClickHitMapper.class);
		
		//
		//search PV 리스트를 가져온다.
		//
		List<Integer> searchPvList = new ArrayList<Integer>(); 
		MapperSession<SearchHitMapper> searchHitMapperSession = dbService.getMapperSession(SearchHitMapper.class);
		SearchHitMapper searchHitMapper = searchHitMapperSession.getMapper();
		try {
			while (startTime.getTimeInMillis() <= endTime.getTimeInMillis()) {
				String timeId = SearchStatisticsProperties.toDatetimeString(startTime, timeTypeCode);
				SearchHitVO vo = searchHitMapper.getEntry(siteId, "_root", timeId);
				if (vo != null) {
					searchPvList.add(vo.getHit());
				} else {
					searchPvList.add(0);
				}
				startTime.add(timeTypeCode, 1);
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (searchHitMapperSession != null) {
				searchHitMapperSession.closeSession();
			}
		}
		mav.addObject("searchPvList", searchPvList);
		
		List<String> clickTypeList = new ArrayList<String>();
		clickTypeList.add("blog");
		clickTypeList.add("goshop");
		clickTypeList.add("list");
		try {
			
			ClickHitMapper mapper = mapperSession.getMapper();
			
			for(String clickType : clickTypeList){
				List<Integer> typeHitList = new ArrayList<Integer>();
				
				while (startTime.getTimeInMillis() <= endTime.getTimeInMillis()) {
					String timeId = SearchStatisticsProperties.toDatetimeString(startTime, timeTypeCode);
					Integer hit = mapper.getTypeHit(siteId, timeId, clickType);
					typeHitList.add(hit);
					startTime.add(timeTypeCode, 1);
				}
				mav.addObject("clickType_"+clickType, typeHitList);
			}
			
			startTime = startTime2;
			List<Integer> hitList = new ArrayList<Integer>();
			while (startTime.getTimeInMillis() <= endTime.getTimeInMillis()) {
				String timeId = SearchStatisticsProperties.toDatetimeString(startTime, timeTypeCode);
				Integer hit = mapper.getHit(siteId, timeId);
				hitList.add(hit);
				startTime.add(timeTypeCode, 1);
			}
			mav.addObject("hitList", hitList);
			mav.addObject("timeText", timeText);
			mav.addObject("clickTypeList", clickTypeList);
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
	public ModelAndView keyword(@PathVariable String siteId) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/keyword");
		return mav;
	}
	
	@RequestMapping("/searchKeyword")
	public ModelAndView searchKeyword(@PathVariable String siteId) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/searchKeyword");
		return mav;
	}
}
