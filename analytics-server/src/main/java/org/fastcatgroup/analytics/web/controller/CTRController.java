package org.fastcatgroup.analytics.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.ClickHitMapper;
import org.fastcatgroup.analytics.db.mapper.ClickKeywordHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordHitMapper;
import org.fastcatgroup.analytics.db.vo.ClickKeywordHitVO;
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
	
	@RequestMapping("/detail")
	public ModelAndView keyword(@PathVariable String siteId, @RequestParam(required=false) String timeText) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/detail");
		
		
		Calendar calendar = null;
		Calendar endTime = null;
		
		if(timeText != null) {
			calendar = SearchStatisticsProperties.parseDatetimeString(timeText, true);
		} else {
			calendar = Calendar.getInstance();
			timeText = SearchStatisticsProperties.toDatetimeString(calendar);
		}
		
		int timeTypeCode = Calendar.MONTH;
		String timeId = SearchStatisticsProperties.getTimeId(calendar, timeTypeCode);
		logger.debug("New time id >> {}", timeId);
		
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<ClickHitMapper> clickHitMapperSession = dbService.getMapperSession(ClickHitMapper.class);
		MapperSession<ClickKeywordHitMapper> clickKeywordHitMapperSession = dbService.getMapperSession(ClickKeywordHitMapper.class);
		
		//
		//search PV 리스트를 가져온다.
		//
		int searchPv = 0;
		MapperSession<SearchHitMapper> searchHitMapperSession = dbService.getMapperSession(SearchHitMapper.class);
		try {
			SearchHitMapper searchHitMapper = searchHitMapperSession.getMapper();
			SearchHitVO vo = searchHitMapper.getEntry(siteId, "_root", timeId);
			if (vo != null) {
				searchPv = vo.getHit();
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (searchHitMapperSession != null) {
				searchHitMapperSession.closeSession();
			}
		}
		
		mav.addObject("searchPv", String.format("%,d", searchPv));
		
		List<String> clickTypeList = new ArrayList<String>();
		clickTypeList.add("blog");
		clickTypeList.add("goshop");
		clickTypeList.add("list");
		try {
			
			ClickHitMapper mapper = clickHitMapperSession.getMapper();
			
			Integer hit = mapper.getHit(siteId, timeId);
			int ctCount = 0;
			if(hit != null){
				ctCount = hit.intValue();
			}
			mav.addObject("ctCount", String.format("%,d", ctCount));
			
			if(searchPv != 0){
				mav.addObject("ctRate", "0%");
			}else{
				float ctr = ((float) ctCount / (float) searchPv) * 100.0f;
				mav.addObject("ctRate", String.format("%.1f", ctr) + "%");
			}
			
			for(String clickType : clickTypeList){
				int typeHitValue = 0;
				Integer typeHit = mapper.getTypeHit(siteId, timeId, clickType);
				if(typeHit != null){
					typeHitValue = typeHit;
				}
				mav.addObject("ctCount_"+clickType, String.format("%,d", typeHitValue));
			}
				
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (clickHitMapperSession != null) {
				clickHitMapperSession.closeSession();
			}
		}
		
		List<ClickKeywordHitVO> keywordList = null;
		List<String> keywordSearchPvList = new ArrayList<String>();
		List<String> kewordCtrList = new ArrayList<String>();
		List<Map<String, String>> typeCountMapList = new ArrayList<Map<String, String>>();
		MapperSession<SearchKeywordHitMapper> searchKeywordHitMapperSession = dbService.getMapperSession(SearchKeywordHitMapper.class);
		try {
			SearchKeywordHitMapper searchKeywordHitMapper = searchKeywordHitMapperSession.getMapper();
			ClickKeywordHitMapper clickKeywordHitMapper = clickKeywordHitMapperSession.getMapper();
			/*
			 * 상위 20개의 키워드 리스트를 얻어온다.
			 * */
			keywordList = clickKeywordHitMapper.getKeywordEntryList(siteId, timeId, 20);
			for(ClickKeywordHitVO vo : keywordList) {
				String keyword = vo.getKeyword();
				int keywordClickCount = vo.getCount();
				SearchHitVO searchHitVO = searchKeywordHitMapper.getEntry(siteId, "_root", timeId, keyword);
				int keywordSearchPv = 0;
				float keywordCtRate = 0.0f;
				if (searchHitVO != null) {
					keywordSearchPv = searchHitVO.getHit();
				}
				if(keywordSearchPv > 0){
					keywordCtRate = ((float) keywordClickCount / (float) keywordSearchPv) * 100.0f;
				}
				keywordSearchPvList.add(String.format("%,d", keywordSearchPv));
				kewordCtrList.add(String.format("%.1f", keywordCtRate)+"%");
				
				Map<String, String> typeCountMap = new HashMap<String, String>();
				for(String clickType : clickTypeList){
					int keywordClickTypeCount = clickKeywordHitMapper.getKeywordClickCount(siteId, timeId, keyword, clickType);
					typeCountMap.put(clickType, String.format("%,d", keywordClickTypeCount));
				}
				typeCountMapList.add(typeCountMap);
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (searchKeywordHitMapperSession != null) {
				searchKeywordHitMapperSession.closeSession();
			}
			if (clickKeywordHitMapperSession != null) {
				clickKeywordHitMapperSession.closeSession();
			}
		}
		
		/* 키워드별 */
		mav.addObject("keywordList", keywordList);
		mav.addObject("keywordSearchPvList", keywordSearchPvList);
		mav.addObject("kewordCtrList", kewordCtrList);
		mav.addObject("typeCountMapList", typeCountMapList);
		
		mav.addObject("timeText", timeText);
		mav.addObject("clickTypeList", clickTypeList);
		logger.debug(">>clickTypeList {} ", clickTypeList);
		return mav;
		
	}
	
	@RequestMapping("/keyword")
	public ModelAndView searchKeyword(@PathVariable String siteId) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/keyword");
		return mav;
	}
}
