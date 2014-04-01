package org.fastcatgroup.analytics.web.controller;

import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordHitMapper;
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

	@RequestMapping("/hit")
	public ModelAndView index(@PathVariable String siteId,
			@RequestParam(defaultValue = "_root") String categoryId,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String timeText, //2014.03.20 - 2014.03.25
			@RequestParam(required = false) String timeViewType) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/hit");
		
		if(timeText == null){
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -7);
			String timeFrom = SearchStatisticsProperties.toDatetimeString(calendar);
			
			calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			String timeTo = SearchStatisticsProperties.toDatetimeString(calendar);
			
			timeText = timeFrom + " - " + timeTo;
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
		MapperSession<SearchHitMapper> hitMapperSession = null;
		MapperSession<SearchKeywordHitMapper> keywordHitMapperSession = null;
		try {
			
			List<SearchHitVO> list = null;
			
			if(keyword != null && keyword.trim().length() > 0){
				keywordHitMapperSession = dbService.getMapperSession(SearchKeywordHitMapper.class);
				SearchKeywordHitMapper mapper = keywordHitMapperSession.getMapper();
				list = mapper.getEntryListBetween(siteId, categoryId, keyword, startTimeId, endTimeId);
			}else{
				hitMapperSession = dbService.getMapperSession(SearchHitMapper.class);
				SearchHitMapper mapper = hitMapperSession.getMapper();
				list = mapper.getEntryListBetween(siteId, categoryId, startTimeId, endTimeId);
			}
			
			if (list != null && list.size() > 0) {
				
				for (int timeInx = 0; startTime.getTimeInMillis() <= endTime.getTimeInMillis(); timeInx++) {
					String timeString = SearchStatisticsProperties.toDatetimeString(startTime, timeTypeCode);
					
					logger.debug("timeString > {}", timeString);
					int hit = 0;
					
					if(timeInx < list.size()) {

						SearchHitVO vo = list.get(timeInx);
						Calendar timeCurrent = SearchStatisticsProperties.parseTimeId(vo.getTimeId());
						logger.debug("startTime > {} : timeCurrent > {}", startTime.getTime(), timeCurrent.getTime());
						
						if (SearchStatisticsProperties.isEquals(startTime, timeCurrent, timeTypeCode)) {
							hit = vo.getHit();
							vo.setTimeId(timeString);
						} else {
							SearchHitVO newVO = new SearchHitVO();
							newVO.setTimeId(timeString);
							newVO.setHit(hit);
							if (startTime.before(timeCurrent)) {
								// 목적일보다 작으므로 앞에 더해준다.
								list.add(timeInx, newVO);
							} else {
								// 목적일보타 크므로 뒤에 더해준다.
								list.add(timeInx + 1, newVO);
							}
						}
					} else {
						//데이터가 모자라면 빈데이터로 채워준다.
						SearchHitVO newVO = new SearchHitVO();
						newVO.setTimeId(timeString);
						newVO.setHit(hit);
						list.add(newVO);
					}
					
					startTime.add(timeTypeCode, 1);
				}
			}
			mav.addObject("categoryId", categoryId);
			mav.addObject("timeFrom", timeFrom);
			mav.addObject("timeTo", timeTo);
			mav.addObject("timeViewType", timeViewType);
			mav.addObject("list", list);
			
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (keywordHitMapperSession != null) {
				keywordHitMapperSession.closeSession();
			}
			if (hitMapperSession != null) {
				hitMapperSession.closeSession();
			}
		}
		return mav;
	}
	
	/*	
	@RequestMapping("/keyword")
	public ModelAndView keyword(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(required=false) String keyword
			, @RequestParam(required=false) String timeFrom, @RequestParam(required=false) String timeTo) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/keyword");
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchKeywordHitMapper> mapperSession = dbService.getMapperSession(SearchKeywordHitMapper.class);
		
		try {
			SearchKeywordHitMapper mapper = mapperSession.getMapper();
			List<SearchHitVO> list = null;
			
			if(timeFrom == null){
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -7);
				String defaultTimeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.DATE);
				timeFrom = defaultTimeId;
			}
			if(timeTo == null){
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -1);
				String defaultTimeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.DATE);
				timeTo = defaultTimeId;
			}
			
			if(keyword != null && keyword.length() > 0){
				Calendar startTime = SearchStatisticsProperties.parseTimeId(timeFrom);
				Calendar endTime = SearchStatisticsProperties.parseTimeId(timeTo);
				
				list = mapper.getEntryListBetween(siteId, categoryId, keyword, timeFrom, timeTo);
				
				if (list != null && list.size() > 0) {
					Calendar timeCurrent = null;
					SearchHitVO vo = null;

					SearchHitVO newVO = null;
					
					for (int timeInx = 0; startTime.getTimeInMillis() <= endTime.getTimeInMillis(); timeInx++) {
						String timeId = SearchStatisticsProperties.getTimeId(startTime, Calendar.DATE);
						int hit = 0;
						
						if(list.size() > timeInx) {

							vo = list.get(timeInx);
							timeCurrent = SearchStatisticsProperties.parseTimeId(vo.getTimeId());

							long timeStartMillis = startTime.getTimeInMillis();
							long timeCurrentMillis = timeCurrent.getTimeInMillis();

							if (timeStartMillis == timeCurrentMillis) {
								hit = vo.getHit();
								timeCurrent = null;
							} else {
								newVO = new SearchHitVO();
								newVO.setTimeId(timeId);
								newVO.setHit(hit);
								if (timeStartMillis < timeCurrentMillis) {
									// 목적일보다 작으므로 앞에 더해준다.
									list.add(timeInx, newVO);
								} else {
									// 목적일보타 크므로 뒤에 더해준다.
									list.add(timeInx + 1, newVO);
								}
							}
						} else {
							newVO = new SearchHitVO();
							newVO.setTimeId(timeId);
							newVO.setHit(hit);
							list.add(newVO);
						}
						startTime.add(Calendar.DATE, 1);
					}
				}
			}
			
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
*/
}
