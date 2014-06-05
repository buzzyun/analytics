package org.fastcatsearch.analytics.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.ClickTypeSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.ServiceSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.ClickHitMapper;
import org.fastcatsearch.analytics.db.mapper.ClickKeywordHitMapper;
import org.fastcatsearch.analytics.db.mapper.ClickKeywordTargetHitMapper;
import org.fastcatsearch.analytics.db.mapper.SearchHitMapper;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordHitMapper;
import org.fastcatsearch.analytics.db.mapper.SearchPathHitMapper;
import org.fastcatsearch.analytics.db.vo.ClickKeywordHitVO;
import org.fastcatsearch.analytics.db.vo.ClickKeywordTargetHitVO;
import org.fastcatsearch.analytics.db.vo.SearchHitVO;
import org.fastcatsearch.analytics.db.vo.SearchPathHitVO;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.ListableCounter;
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
		
		SiteAttribute siteAttribute = this.getStatisticsService().getStatisticsSetting(siteId).getSiteAttribute();
		List<ClickTypeSetting> clickTypeSettingList = siteAttribute.getClickTypeList();
		List<ServiceSetting> serviceSettingList = siteAttribute.getServiceList();
		
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
			startTime = StatisticsUtils.parseDatetimeString(timeFrom, true);
			endTime = StatisticsUtils.parseDatetimeString(timeTo, false);
		} else {
			endTime = Calendar.getInstance();
			startTime = Calendar.getInstance();
			startTime.add(Calendar.MONTH, -6);
			startTime.set(Calendar.DAY_OF_MONTH, 1);
			
			endTime.add(Calendar.MONTH, 1);
			endTime.set(Calendar.DAY_OF_MONTH, -1);
			
			String timeFrom = StatisticsUtils.toDatetimeString(startTime);
			String timeTo = StatisticsUtils.toDatetimeString(endTime);
			timeText = timeFrom + " - " + timeTo;
		}
		
		Calendar startTime2 = (Calendar) startTime.clone();
		
		int timeTypeCode = Calendar.MONTH;
		String startTimeId = StatisticsUtils.getTimeId(startTime, timeTypeCode);
		String endTimeId = StatisticsUtils.getTimeId(endTime, timeTypeCode);
		logger.debug("New time id >> {} ~ {}", startTimeId, endTimeId);
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<ClickHitMapper> clickMapperSession = dbService.getMapperSession(ClickHitMapper.class);
		
		//
		//search PV 리스트를 가져온다.
		//
		List<Integer> searchPvList = new ArrayList<Integer>();
		List<String> labelList = new ArrayList<String>();
		MapperSession<SearchPathHitMapper> searchPathHitMapperSession = dbService.getMapperSession(SearchPathHitMapper.class);
		SearchPathHitMapper searchPathHitMapper = searchPathHitMapperSession.getMapper();
		Map<String, ListableCounter> pathCounter = new HashMap<String, ListableCounter>();
		
		try {
			for(ServiceSetting service : serviceSettingList) {
				pathCounter.put(service.getId(), new ListableCounter());
			}
			pathCounter.put("_etc", new ListableCounter());
			
			startTime = (Calendar) startTime2.clone();
			int timeInx = 0;
			for (;startTime.getTimeInMillis() <= endTime.getTimeInMillis();timeInx++) {
				String timeId = StatisticsUtils.getTimeId(startTime, timeTypeCode);
				String label = StatisticsUtils.toDatetimeString(startTime, Calendar.MONTH);
				labelList.add(label);
				List<SearchPathHitVO> list = searchPathHitMapper.getEntryByTimeId(siteId, timeId);
				if (list != null) {
					int cnt = 0;
					for(int svcInx=0;svcInx<list.size();svcInx++) {
						
						SearchPathHitVO pathHitVO = list.get(svcInx);
						if(pathCounter.containsKey(pathHitVO.getSearchId())) {
							pathCounter.get(
									pathHitVO.getSearchId()).increment(timeInx,
									pathHitVO.getHit());
						} else {
							pathCounter.get("_etc").increment(timeInx, pathHitVO.getHit());
						}
						
						cnt += pathHitVO.getHit();
					}
					searchPvList.add(cnt);
				} else {
					for(ServiceSetting service : serviceSettingList) {
						pathCounter.get(service.getId()).increment(timeInx, 0);
					}
					searchPvList.add(0);
				}
				startTime.add(timeTypeCode, 1);
			}
			
			
			//모든 서비스 내 배열 갯수를 맞춰 준다.
			for(String key : pathCounter.keySet()) {
				if(pathCounter.get(key).list().size() < timeInx) {
					pathCounter.get(key).increment(timeInx - 1, 0);
				}
			}
			
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (searchPathHitMapperSession != null) {
				searchPathHitMapperSession.closeSession();
			}
		}
		mav.addObject("serviceList", serviceSettingList);
		mav.addObject("searchPathCounter", pathCounter);
		mav.addObject("searchPvList", searchPvList);
		try {
			ClickHitMapper clickMapper = clickMapperSession.getMapper();
			for(ClickTypeSetting clickType : clickTypeSettingList){
				String clickTypeId =  clickType.getId();
				startTime = (Calendar) startTime2.clone();
				List<Integer> typeHitList = new ArrayList<Integer>();
				while (startTime.getTimeInMillis() <= endTime.getTimeInMillis()) {
					String timeId = StatisticsUtils.getTimeId(startTime, timeTypeCode);
					Integer hit = clickMapper.getTypeHit(siteId, timeId, clickTypeId);
					typeHitList.add(hit);
					startTime.add(timeTypeCode, 1);
				}
				mav.addObject("clickType_"+clickTypeId, typeHitList);
			}
			
			startTime = (Calendar) startTime2.clone();
			List<Integer> clickHitList = new ArrayList<Integer>();
			while (startTime.getTimeInMillis() <= endTime.getTimeInMillis()) {
				String timeId = StatisticsUtils.getTimeId(startTime, timeTypeCode);
				Integer hit = clickMapper.getHit(siteId, timeId);
				clickHitList.add(hit);
				startTime.add(timeTypeCode, 1);
			}
			
			mav.addObject("clickHitList", clickHitList);
			mav.addObject("timeText", timeText);
			mav.addObject("labelList", labelList);
			mav.addObject("clickTypeSettingList", clickTypeSettingList);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (clickMapperSession != null) {
				clickMapperSession.closeSession();
			}
		}
		
		return mav;
	}
	
	@RequestMapping("/detail")
	public ModelAndView keyword(@PathVariable String siteId, @RequestParam(required=false) String timeText) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/detail");
		
		SiteAttribute siteAttribute = this.getStatisticsService().getStatisticsSetting(siteId).getSiteAttribute();
		List<ClickTypeSetting> clickTypeSettingList = siteAttribute.getClickTypeList();
		List<String[]> clickTypeList = new ArrayList<String[]>();
		for(ClickTypeSetting clickType : clickTypeSettingList) {
			clickTypeList.add(new String[] { clickType.getId(), clickType.getName() });
		}
		
		Calendar calendar = null;
		if(timeText != null) {
			calendar = StatisticsUtils.parseDatetimeString(timeText, true);
		} else {
			calendar = Calendar.getInstance();
			timeText = StatisticsUtils.toDatetimeString(calendar);
		}
		
		int timeTypeCode = Calendar.MONTH;
		String timeId = StatisticsUtils.getTimeId(calendar, timeTypeCode);
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
		
		try {
			
			ClickHitMapper mapper = clickHitMapperSession.getMapper();
			
			Integer hit = mapper.getHit(siteId, timeId);
			int ctCount = 0;
			if(hit != null){
				ctCount = hit.intValue();
			}
			mav.addObject("ctCount", String.format("%,d", ctCount));
			
			if(searchPv == 0){
				mav.addObject("ctRate", "0%");
			}else{
				float ctr = ((float) ctCount / (float) searchPv) * 100.0f;
				mav.addObject("ctRate", String.format("%.1f", ctr) + "%");
			}
			
			for(String[] clickType : clickTypeList){
				int typeHitValue = 0;
				Integer typeHit = mapper.getTypeHit(siteId, timeId, clickType[0]);
				if(typeHit != null){
					typeHitValue = typeHit;
				}
				mav.addObject("ctCount_"+clickType[0], String.format("%,d", typeHitValue));
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
				for(String[] clickType : clickTypeList){
					try {
						logger.debug("clickKeywordHitMapper:{}", clickKeywordHitMapper);
						logger.debug("siteId:{} / timeId:{} / keyword:{} / clickType:{}", siteId, timeId, keyword, clickType);
						int keywordClickTypeCount = clickKeywordHitMapper.getKeywordTypeClickCount(siteId, timeId, keyword, clickType[0]);
						typeCountMap.put(clickType[0], String.format("%,d", keywordClickTypeCount));
					} catch (NullPointerException e) {
						logger.error("error:{}",e.getMessage());
						typeCountMap.put(clickType[0], String.format("%,d", 0));
					}
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
	public ModelAndView searchKeyword(@PathVariable String siteId, @RequestParam(required=false) String timeText, @RequestParam(required=false) String keyword) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/keyword");
		Calendar calendar = null;
		if(timeText != null) {
			calendar = StatisticsUtils.parseDatetimeString(timeText, true);
		} else {
			calendar = Calendar.getInstance();
			timeText = StatisticsUtils.toDatetimeString(calendar);
		}
		
		int timeTypeCode = Calendar.MONTH;
		String timeId = StatisticsUtils.getTimeId(calendar, timeTypeCode);
		logger.debug("New time id >> {}", timeId);
		
		SiteAttribute siteAttribute = this.getStatisticsService().getStatisticsSetting(siteId).getSiteAttribute();
		List<ClickTypeSetting> clickTypeSettingList = siteAttribute.getClickTypeList();
		List<String[]> clickTypeList = new ArrayList<String[]>();
		for(ClickTypeSetting clickType : clickTypeSettingList) {
			clickTypeList.add(new String[] { clickType.getId(), clickType.getName() });
		}
		
		mav.addObject("timeText", timeText);
		mav.addObject("clickTypeList", clickTypeList);
		
		if(keyword == null || keyword.trim().length() == 0) {
			return mav;
		}
		keyword = keyword.trim();
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<ClickKeywordHitMapper> clickKeywordHitMapperSession = dbService.getMapperSession(ClickKeywordHitMapper.class);
		
		//
		//search PV 리스트를 가져온다.
		//
		int searchPv = 0;
		MapperSession<SearchKeywordHitMapper> searchKeywordHitMapperSession = dbService.getMapperSession(SearchKeywordHitMapper.class);
		try {
			SearchKeywordHitMapper searchKeywordHitMapper = searchKeywordHitMapperSession.getMapper();
			SearchHitVO vo = searchKeywordHitMapper.getEntry(siteId, "_root", timeId, keyword);
			if (vo != null) {
				searchPv = vo.getHit();
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (searchKeywordHitMapperSession != null) {
				searchKeywordHitMapperSession.closeSession();
			}
		}
		
		mav.addObject("searchPv", String.format("%,d", searchPv));
		
		try {
			
			ClickKeywordHitMapper clickKeywordHitMapper = clickKeywordHitMapperSession.getMapper();
			Integer clickCount = clickKeywordHitMapper.getKeywordClickCount(siteId, timeId, keyword);
			int ctCount = clickCount != null ? clickCount.intValue() : 0;
			mav.addObject("ctCount", String.format("%,d", ctCount));
			if(searchPv == 0){
				mav.addObject("ctRate", "0%");
			}else{
				float ctr = ((float) ctCount / (float) searchPv) * 100.0f;
				mav.addObject("ctRate", String.format("%.1f", ctr) + "%");
			}
			for(String[] clickType : clickTypeList){
				int typeHitValue = 0;
				Integer typeHit = clickKeywordHitMapper.getKeywordTypeClickCount(siteId, timeId, keyword, clickType[0]);
				if(typeHit != null){
					typeHitValue = typeHit;
				}
				mav.addObject("ctCount_"+clickType[0], String.format("%,d", typeHitValue));
			}

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (clickKeywordHitMapperSession != null) {
				clickKeywordHitMapperSession.closeSession();
			}
		}
		List<ClickKeywordTargetHitVO> targetHitList = null;
		MapperSession<ClickKeywordTargetHitMapper> clickKeywordTargetHitMapperSession = dbService.getMapperSession(ClickKeywordTargetHitMapper.class);
		try {
			ClickKeywordTargetHitMapper clickKeywordTargetHitMapper = clickKeywordTargetHitMapperSession.getMapper();
			int topCount = 20;
			targetHitList = clickKeywordTargetHitMapper.getEntryList(siteId, timeId, keyword, topCount);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (clickKeywordTargetHitMapperSession != null) {
				clickKeywordTargetHitMapperSession.closeSession();
			}
		}
		
		mav.addObject("targetHitList", targetHitList);
		return mav;
	}
}
