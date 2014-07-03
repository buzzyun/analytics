package org.fastcatsearch.analytics.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.ClickTypeSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.ClickHitMapper;
import org.fastcatsearch.analytics.db.mapper.SearchHitMapper;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatsearch.analytics.db.mapper.SearchPathHitMapper;
import org.fastcatsearch.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatsearch.analytics.db.vo.RankKeywordVO;
import org.fastcatsearch.analytics.db.vo.SearchHitVO;
import org.fastcatsearch.analytics.db.vo.SearchPathHitVO;
import org.fastcatsearch.analytics.db.vo.SearchTypeHitVO;
import org.fastcatsearch.analytics.db.vo.RankKeywordVO.RankDiffType;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.ListableCounter;
import org.json.JSONStringer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report")
public class ReportMainController extends AbstractController {
	
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@RequestMapping("/index")
	public ModelAndView index(@PathVariable String siteId) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/index");
		return mav;
	}
	
	@RequestMapping("/configuration")
	public ModelAndView configuration(@PathVariable String siteId) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/configuration");
		return mav;
	}
	
	
	@RequestMapping("/dashboard")
	public ModelAndView dashboard(@PathVariable String siteId
			, @RequestParam(required=false) String timeText
			, @RequestParam(required = false) String timeViewType) {
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		StatisticsSettings statisticsSetting = getStatisticsService().getStatisticsSetting(siteId);
		
		MapperSession<SearchHitMapper> hitSession = null;
		MapperSession<SearchKeywordRankMapper> rankSession = null;
		MapperSession<SearchTypeHitMapper> typeSession = null;
		MapperSession<SearchPathHitMapper> pathSession = null;
		MapperSession<ClickHitMapper> clickSession = null;
		
		String categoryId = "_root";
		
		ModelAndView mav = new ModelAndView();
		
		try {
		
			hitSession = dbService.getMapperSession(SearchHitMapper.class);
			rankSession = dbService.getMapperSession(SearchKeywordRankMapper.class);
			typeSession = dbService.getMapperSession(SearchTypeHitMapper.class);
			pathSession = dbService.getMapperSession(SearchPathHitMapper.class);
			clickSession = dbService.getMapperSession(ClickHitMapper.class);
			
			SearchHitMapper hitMapper = hitSession.getMapper();
			SearchKeywordRankMapper rankMapper = rankSession.getMapper();
			SearchTypeHitMapper typeMapper = typeSession.getMapper();
			SearchPathHitMapper pathMapper = pathSession.getMapper();
			ClickHitMapper clickMapper = clickSession.getMapper();
			
			int timeTypeCode = Calendar.WEEK_OF_YEAR;
			
			if("W".equalsIgnoreCase(timeViewType)) {
				timeTypeCode = Calendar.WEEK_OF_YEAR;
			} else if("M".equalsIgnoreCase(timeViewType)) {
				timeTypeCode = Calendar.MONTH;
			} else if("Y".equalsIgnoreCase(timeViewType)) {
				timeTypeCode = Calendar.YEAR;
			} else {
				timeViewType = "W";
			}
			
			
			//일주일치와 그 전주의 일자별 데이터를 가져온다.
			//Calendar calendar = StatisticsUtils.getCalendar();
			Calendar calendar = null;
			Calendar fromDate = null;
			Calendar toDate = null;
			
			if(timeText!=null && !"".equals(timeText)) {
				calendar = StatisticsUtils.parseDatetimeString(timeText.split(" - ")[0], true);
			} else {
				calendar = StatisticsUtils.getCalendar();
			}
			
			String fromTimeId = "";
			String toTimeId = "";
			if(timeTypeCode == Calendar.WEEK_OF_YEAR) {
				fromDate = StatisticsUtils.getFirstDayOfWeek(calendar);
				toDate = StatisticsUtils.getLastDayOfWeek(calendar);
				fromTimeId = StatisticsUtils.getTimeId(fromDate, Calendar.DAY_OF_MONTH);
				toTimeId = StatisticsUtils.getTimeId(toDate, Calendar.DAY_OF_MONTH);
			} else if(timeTypeCode == Calendar.MONTH) {
				fromDate = (Calendar) calendar.clone();
				toDate = (Calendar) calendar.clone();
				fromDate.set(Calendar.DATE, 1);
				toDate.add(Calendar.MONTH, 1);
				toDate.set(Calendar.DATE, 0);
				fromTimeId = StatisticsUtils.getTimeId(fromDate, Calendar.DAY_OF_MONTH);
				toTimeId = StatisticsUtils.getTimeId(toDate, Calendar.DAY_OF_MONTH);
			} else if(timeTypeCode == Calendar.YEAR) {
				fromDate = (Calendar) calendar.clone();
				toDate = (Calendar) calendar.clone();
				fromDate.set(Calendar.MONTH, 0);
				fromDate.set(Calendar.DATE, 1);
				toDate.set(Calendar.MONTH, 11);
				toDate.set(Calendar.DATE, 31);
				fromTimeId = StatisticsUtils.getTimeId(fromDate, Calendar.MONTH);
				toTimeId = StatisticsUtils.getTimeId(toDate, Calendar.MONTH);
			}
			
			timeText = StatisticsUtils.toDatetimeString(fromDate);
					//+ " - " + StatisticsUtils.toDatetimeString(toDate);
			
			logger.trace("getData [current] {}~{}", fromTimeId, toTimeId);
		
			List<SearchHitVO> currentWeek = fillData(
					hitMapper.getEntryListBetween(siteId, categoryId,
							fromTimeId, toTimeId), fromDate, toDate, timeTypeCode);
			
			if(timeTypeCode == Calendar.WEEK_OF_YEAR) {
				fromDate.add(Calendar.DAY_OF_MONTH, -7);
				toDate.add(Calendar.DAY_OF_MONTH, -7);
				fromTimeId = StatisticsUtils.getTimeId(fromDate, Calendar.DAY_OF_MONTH);
				toTimeId = StatisticsUtils.getTimeId(toDate, Calendar.DAY_OF_MONTH);
			} else if(timeTypeCode == Calendar.MONTH) {
				fromDate.add(Calendar.MONTH, -1);
				toDate.add(Calendar.MONTH, -1);
				fromTimeId = StatisticsUtils.getTimeId(fromDate, Calendar.DAY_OF_MONTH);
				toTimeId = StatisticsUtils.getTimeId(toDate, Calendar.DAY_OF_MONTH);
			} else if(timeTypeCode == Calendar.YEAR) {
				fromDate.add(Calendar.YEAR, -1);
				toDate.add(Calendar.YEAR, -1);
				fromTimeId = StatisticsUtils.getTimeId(fromDate, Calendar.MONTH);
				toTimeId = StatisticsUtils.getTimeId(toDate, Calendar.MONTH);
			}
			
			logger.trace("getData [last] {}~{}", fromTimeId, toTimeId);
			
			List<SearchHitVO> lastWeek = fillData(
					hitMapper.getEntryListBetween(siteId, categoryId,
							fromTimeId, toTimeId), fromDate, toDate, timeTypeCode);
			
			mav.addObject("currentWeekData", currentWeek);
			mav.addObject("lastWeekData", lastWeek);
			
			//인기키워드
			
			Calendar lastDay = (Calendar) calendar.clone();
			lastDay.add(timeTypeCode, -1);
			
			String timeId = StatisticsUtils.getTimeId(calendar, timeTypeCode);
			
			List<RankKeywordVO> popularKeywordList = rankMapper.getEntryList(
					siteId, categoryId, timeId, null, 0, 0, 10);
			List<RankKeywordVO> hotKeywordList = rankMapper.getEntryList(
					siteId, categoryId, timeId, RankDiffType.UP.name(), 30, 0, 10);
			List<RankKeywordVO> newKeywordList = rankMapper.getEntryList(
					siteId, categoryId, timeId, RankDiffType.NEW.name(), 0, 0, 10);
			
			mav.addObject("popularKeywordList", popularKeywordList);
			mav.addObject("hotKeywordList", hotKeywordList);
			mav.addObject("newKeywordList", newKeywordList);
			
			//타입별
			List<TypeSetting> primeTypeList = new ArrayList<TypeSetting>();
			for(TypeSetting typeSetting : statisticsSetting.getSiteAttribute().getTypeList()){
				if(typeSetting.isPrime()){
					primeTypeList.add(typeSetting);
				}
			}
			logger.debug(">>>primeTypeList > {}", primeTypeList);
			
			@SuppressWarnings("unchecked")
			List<SearchTypeHitVO>[] typeHitListArray = new List[primeTypeList.size()];
			
			for (int typeInx = 0; typeInx < primeTypeList.size(); typeInx++) {
				String typeId = primeTypeList.get(typeInx).getId();
				List<SearchTypeHitVO> typeList = typeMapper.getEntryList(siteId, categoryId, timeId, typeId);
				typeHitListArray[typeInx] = typeList;
			}
			logger.debug(">>>typeHitListArray {}, {}", "", typeHitListArray);
			mav.addObject("primeTypeList", primeTypeList);
			mav.addObject("typeHitListArray", typeHitListArray);
			mav.addObject("timeText", timeText);
			
			
			int dateUnit = Calendar.DAY_OF_MONTH;
			//6개월차이가 아니라 실질적인 주비교, 월비교가 들어간다.
			if(timeTypeCode == Calendar.WEEK_OF_YEAR) {
				fromDate = StatisticsUtils.getFirstDayOfWeek(calendar);
				toDate = StatisticsUtils.getLastDayOfWeek(calendar);
				//toDate.add(Calendar.WEEK_OF_YEAR, 1);
				dateUnit = Calendar.DAY_OF_MONTH;
			} else if(timeTypeCode == Calendar.MONTH) {
				fromDate = (Calendar) calendar.clone();
				toDate = (Calendar) calendar.clone();
				fromDate.set(Calendar.DATE, 1);
				toDate.add(Calendar.MONTH, 1);
				toDate.set(Calendar.DATE, 0);
				dateUnit = Calendar.DAY_OF_MONTH;
			} else if(timeTypeCode == Calendar.YEAR) {
				fromDate = (Calendar) calendar.clone();
				toDate = (Calendar) calendar.clone();
				fromDate.set(Calendar.MONTH, 0);
				fromDate.set(Calendar.DATE, 1);
				toDate.set(Calendar.MONTH, 11);
				toDate.set(Calendar.DATE, 31);
				dateUnit = Calendar.MONTH;
			}
			
			
			List<ClickTypeSetting> clickTypeList = statisticsSetting.getSiteAttribute().getClickTypeList();
		
			List<String> labelList = new ArrayList<String>();
			List<Integer> searchPvList = new ArrayList<Integer>();
			Map<String, ListableCounter> clickHitList = new HashMap<String, ListableCounter>();
			for(ClickTypeSetting clickType : clickTypeList){
				clickHitList.put(clickType.getId(), new ListableCounter());
			}
			int timeInx = 0;
			
			for (;fromDate.getTimeInMillis() <= toDate.getTimeInMillis();timeInx++) {
				timeId = StatisticsUtils.getTimeId(fromDate, dateUnit);
				String label = StatisticsUtils.toDatetimeString(fromDate, dateUnit);
				labelList.add(label);
				List<SearchPathHitVO> pvList = pathMapper.getEntryByTimeId(siteId, timeId);
				if (pvList != null) {
					int cnt = 0;
					for(int svcInx=0;svcInx<pvList.size();svcInx++) {
						SearchPathHitVO pathHitVO = pvList.get(svcInx);
						cnt += pathHitVO.getHit();
					}
					searchPvList.add(cnt);
				} else {
					searchPvList.add(0);
				}
				
				Integer totalClick = clickMapper.getHit(siteId, timeId);
				if(totalClick==null) { totalClick=0; }
				for(ClickTypeSetting clickType : clickTypeList){
					Integer typeHit = clickMapper.getTypeHit(siteId, timeId, clickType.getId());
					if(typeHit==null) { typeHit = 0; }
					clickHitList.get(clickType.getId()).increment(timeInx, typeHit);
					totalClick -= typeHit;
				}
				fromDate.add(dateUnit, 1);
			}
			
			//모든 타입 내 배열 갯수를 맞춰 준다.
			for(String key : clickHitList.keySet()) {
				if(clickHitList.get(key).list().size() < timeInx) {
					clickHitList.get(key).increment(timeInx - 1, 0);
				}
			}
			
			mav.addObject("clickTypeList", clickTypeList);
			mav.addObject("searchPvList", searchPvList);
			mav.addObject("clickHitList", clickHitList);
			mav.addObject("labelList", labelList);
			mav.addObject("timeViewType", timeViewType);
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if(hitSession != null) try {
				hitSession.closeSession();
			} catch (Exception ignore) { 
			}
			if(rankSession != null) try {
				rankSession.closeSession();
			} catch (Exception ignore) { 
			}
			if(typeSession != null) try {
				typeSession.closeSession();
			} catch (Exception ignore) { 
			}
			if(pathSession != null) try {
				pathSession.closeSession();
			} catch (Exception ignore) { 
			}
			if(clickSession != null) try {
				clickSession.closeSession();
			} catch (Exception ignore) { 
			}
		}
		
		mav.addObject("today", StatisticsUtils.toDatetimeString(
				StatisticsUtils.getCalendar()));

		mav.setViewName("report/dashboard");
		return mav;
	}
	
	@RequestMapping("/categoryList")
	public ModelAndView getCategoryList(@PathVariable String siteId) {
		List<SiteSetting> siteCategoryList = getSiteCategoryListConfig();
		JSONStringer s = new JSONStringer();
		s.array();
		for(SiteSetting config : siteCategoryList){
			if(config.getId().equals(siteId)){
				List<CategorySetting> list = config.getStatisticsSettings().getCategoryList();
				for(CategorySetting categoryConfig : list){
					s.object().key(categoryConfig.getId()).value(categoryConfig.getName()).endObject();
				}
				break;
			}
		}
		s.endArray();
		ModelAndView mav = new ModelAndView();
		mav.addObject("content", s.toString());
		mav.setViewName("text");
		return mav;
	}
	
	private List<SearchHitVO> fillData(List<SearchHitVO> list, Calendar from, Calendar to, int timeTypeCode) {
		int usingTimeType = Calendar.DAY_OF_MONTH;
		if (timeTypeCode == Calendar.WEEK_OF_YEAR || timeTypeCode == Calendar.MONTH) {
			usingTimeType = Calendar.DAY_OF_MONTH;
		}
		
		Calendar fromDate = (Calendar) from.clone();
		Calendar toDate = (Calendar) to.clone();
		if (list != null && list.size() > 0) {
			for (int timeInx = 0; fromDate.getTimeInMillis() <= toDate.getTimeInMillis(); timeInx++) {
				String timeString = "";
				timeString = StatisticsUtils.toDatetimeString(fromDate, usingTimeType);
				logger.trace("timeString > {}", timeString);
				int hit = 0;
				if(timeInx < list.size()) {
					SearchHitVO vo = list.get(timeInx);
					Calendar timeCurrent = StatisticsUtils.parseTimeId(vo.getTimeId());
					if(logger.isTraceEnabled()) {
						logger.trace("startTime > {} : timeCurrent > {}:{}", sdf.format(fromDate.getTime()), 
								sdf.format(timeCurrent.getTime()), vo.getTimeId());
					}
					if (StatisticsUtils.isEquals(fromDate, timeCurrent, usingTimeType)) {
						hit = vo.getHit();
						vo.setTimeId(timeString);
						list.set(timeInx, vo);
					} else {
						SearchHitVO newVO = new SearchHitVO();
						newVO.setTimeId(timeString);
						newVO.setHit(hit);
						if (fromDate.before(timeCurrent)) {
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
				
				fromDate.add(usingTimeType, 1);

				if(logger.isTraceEnabled()) {
					logger.trace("startTime:{}", StatisticsUtils.toDatetimeString(fromDate, usingTimeType));
					logger.trace("startTime:{} / endTime:{}",sdf.format(fromDate.getTime()), sdf.format(toDate.getTime()));
				}
			}
		}
		
		return list;
	}
}
