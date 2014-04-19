package org.fastcatgroup.analytics.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.ClickTypeSetting;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.ClickHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatgroup.analytics.db.mapper.SearchPathHitMapper;
import org.fastcatgroup.analytics.db.mapper.SearchTypeHitMapper;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO.RankDiffType;
import org.fastcatgroup.analytics.db.vo.SearchHitVO;
import org.fastcatgroup.analytics.db.vo.SearchPathHitVO;
import org.fastcatgroup.analytics.db.vo.SearchTypeHitVO;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ListableCounter;
import org.json.JSONStringer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report")
public class ReportMainController extends AbstractController {

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
	public ModelAndView dashboard(@PathVariable String siteId) {
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(
				AnalyticsDBService.class);
		
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
			
			//일주일치와 그 전주의 일자별 데이터를 가져온다.
			Calendar calendar = SearchStatisticsProperties.getCalendar();
			Calendar fromDate = SearchStatisticsProperties.getFirstDayOfWeek(calendar);
			Calendar toDate = SearchStatisticsProperties.getLastDayOfWeek(calendar);
			
			String fromTimeId = SearchStatisticsProperties.getTimeId(fromDate, Calendar.DAY_OF_MONTH);
			String toTimeId = SearchStatisticsProperties.getTimeId(toDate, Calendar.DAY_OF_MONTH);
			
			String timeText = SearchStatisticsProperties.toDatetimeString(fromDate)
					+ " - " + SearchStatisticsProperties.toDatetimeString(toDate);
			
			List<SearchHitVO> currentWeek = fillData(
					hitMapper.getEntryListBetween(siteId, categoryId,
							fromTimeId, toTimeId), fromDate, toDate);
			
			fromDate.add(Calendar.DAY_OF_MONTH, -7);
			toDate.add(Calendar.DAY_OF_MONTH, -7);
			fromTimeId = SearchStatisticsProperties.getTimeId(fromDate, Calendar.DAY_OF_MONTH);
			toTimeId = SearchStatisticsProperties.getTimeId(toDate, Calendar.DAY_OF_MONTH);
			
			List<SearchHitVO> lastWeek = fillData(
					hitMapper.getEntryListBetween(siteId, categoryId,
							fromTimeId, toTimeId), fromDate, toDate);
			
			mav.addObject("currentWeekData", currentWeek);
			mav.addObject("lastWeekData", lastWeek);
			
			//인기키워드
			
			Calendar lastDay = (Calendar) calendar.clone();
			lastDay.add(Calendar.DAY_OF_MONTH, -1);
			
			String timeId = SearchStatisticsProperties.getTimeId(lastDay, Calendar.DAY_OF_MONTH);
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
			String[] types = new String[] { "category", "login", "age" };
			String[] typeRateListArray = environment.settingManager().getSystemSettings().getStringArray("dashboard.typeRateList", ",");
			List<String> typeRateList = null;
			if(typeRateListArray == null){
				typeRateList = new ArrayList<String>(0);
			}else{
				typeRateList = Arrays.asList(typeRateListArray);
			}
			@SuppressWarnings("unchecked")
			List<SearchTypeHitVO>[] typeListArray = new List[types.length];
			
			for(int typeInx=0; typeInx < types.length; typeInx++) {
				String typeId = types[typeInx];
				List<SearchTypeHitVO> typeList = typeMapper.getEntryList(siteId, categoryId, timeId, typeId);
				typeListArray[typeInx] = typeList;
			}
			logger.debug(">>>typeRateList {}", typeRateList);
			mav.addObject("typeListArray", typeListArray);
			mav.addObject("timeText", timeText);
			
			fromDate = (Calendar) calendar.clone();
			fromDate.add(Calendar.MONTH, - 6);
			fromDate.set(Calendar.DAY_OF_MONTH, 1);
			
			toDate.add(Calendar.MONTH, 1);
			
			StatisticsSettings statisticsSetting = getStatisticsService().getStatisticsSetting(siteId);
			
			List<ClickTypeSetting> clickTypeList = statisticsSetting.getSiteAttribute().getClickTypeList();
			
			List<String> labelList = new ArrayList<String>();
			List<Integer> searchPvList = new ArrayList<Integer>();
			Map<String, ListableCounter> clickHitList = new HashMap<String, ListableCounter>();
			for(ClickTypeSetting clickType : clickTypeList){
				clickHitList.put(clickType.getId(), new ListableCounter());
			}
			int timeInx = 0;
			for (;fromDate.getTimeInMillis() <= toDate.getTimeInMillis();timeInx++) {
				timeId = SearchStatisticsProperties.getTimeId(fromDate, Calendar.MONTH);
				String label = SearchStatisticsProperties.toDatetimeString(fromDate, Calendar.MONTH);
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
				// 지정된 클릭타입 이외의 타입이 있는지 확인하기 위함.
				//typeHitList.get("_etc").increment(timeInx, totalClick);
				
				fromDate.add(Calendar.MONTH, 1);
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
		}
			
			

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
	
	private List<SearchHitVO> fillData(List<SearchHitVO> list, Calendar from, Calendar to) {
		
		Calendar fromDate = (Calendar) from.clone();
		Calendar toDate = (Calendar) to.clone();
			
		if (list != null && list.size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int timeInx = 0; fromDate.getTimeInMillis() <= toDate.getTimeInMillis(); timeInx++) {
				String timeString = SearchStatisticsProperties.toDatetimeString(fromDate, Calendar.DAY_OF_MONTH);
				logger.trace("timeString > {}", timeString);
				int hit = 0;
				if(timeInx < list.size()) {
					SearchHitVO vo = list.get(timeInx);
					Calendar timeCurrent = SearchStatisticsProperties.parseTimeId(vo.getTimeId());
					if(logger.isTraceEnabled()) {
						logger.trace("startTime > {} : timeCurrent > {}:{}", sdf.format(fromDate.getTime()), 
								sdf.format(timeCurrent.getTime()), vo.getTimeId());
					}

					if (SearchStatisticsProperties.isEquals(fromDate, timeCurrent, Calendar.DAY_OF_MONTH)) {
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

				fromDate.add(Calendar.DAY_OF_MONTH, 1);
				if(logger.isTraceEnabled()) {
					logger.trace("startTime:{}", SearchStatisticsProperties.toDatetimeString(fromDate, Calendar.DAY_OF_MONTH));
					logger.trace("startTime:{} / endTime:{}",sdf.format(fromDate.getTime()), sdf.format(toDate.getTime()));
				}
			}
		}
		
		return list;
	}
}
