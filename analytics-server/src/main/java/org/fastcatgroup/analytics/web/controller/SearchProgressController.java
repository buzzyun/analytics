package org.fastcatgroup.analytics.web.controller;

import java.util.Calendar;
import java.util.List;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordHitMapper;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;
import org.fastcatgroup.analytics.db.vo.SearchKeywordHitVO;
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
				
			Calendar startTime = SearchStatisticsProperties.parseTimeId(timeFrom);
			Calendar endTime = SearchStatisticsProperties.parseTimeId(timeTo);
			
			list = mapper.getEntryListBetween(siteId, categoryId, timeFrom, timeTo);
			
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
			List<SearchKeywordHitVO> list = null;
			
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
					SearchKeywordHitVO vo = null;

					SearchKeywordHitVO newVO = null;
					
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
								newVO = new SearchKeywordHitVO();
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
							newVO = new SearchKeywordHitVO();
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

}
