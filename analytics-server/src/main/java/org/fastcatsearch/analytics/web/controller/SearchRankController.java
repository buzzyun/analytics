package org.fastcatsearch.analytics.web.controller;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.analysis.vo.RankKeyword;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordEmptyMapper;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatsearch.analytics.db.vo.RankKeywordVO;
import org.fastcatsearch.analytics.db.vo.RankKeywordVO.RankDiffType;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report/rank")
public class SearchRankController extends AbstractController {

	@RequestMapping("/realtimeSearchKeyword")
	public ModelAndView realtimeSearchKeyword(@PathVariable String siteId, @RequestParam(required = false) String categoryId) {
		ModelAndView mav = new ModelAndView();

		List<SiteSetting> siteCategoryList = getSiteCategoryListConfig();
		mav.addObject("siteCategoryList", siteCategoryList);
		List<RankKeyword> rankList = null;
		if (siteId != null) {
			rankList = getStatisticsService().getRealtimePopularKeywordList(siteId, categoryId);
		}
		mav.addObject("rankList", rankList);
		mav.setViewName("report/rank/realtimeSearchKeyword");
		return mav;
	}

	@RequestMapping("/searchKeyword")
	public ModelAndView searchKeywordAll(@PathVariable String siteId, @RequestParam(defaultValue="_root") String categoryId
			, @RequestParam(required=false) String timeText, @RequestParam(required=false) String keywordType, @RequestParam(defaultValue="1") int pageNo
			, @RequestParam(defaultValue="0") int start, @RequestParam(defaultValue="10") int length, @RequestParam(required = false) String timeViewType) {
		
		String[] typeArray = environment.settingManager().getSystemSettings().getStringArray("db.rankList", ",");
		
		int rankDiffOver = 0;
		String rankDiffType = null;
		if(keywordType == null || keywordType.length() == 0){
			keywordType = "all";
		}else if(keywordType.equals("new")){
			rankDiffOver = 0;
			rankDiffType = RankDiffType.NEW.name();
		}else if(keywordType.equals("hot")){
			rankDiffOver = 30;
			rankDiffType = RankDiffType.UP.name();
		}else if(keywordType.equals("down")){
			rankDiffOver = 30;
			rankDiffType = RankDiffType.DN.name();
		}else if(keywordType.equals("empty")){
			//do nothing
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
		
		if(timeText == null){
			Calendar calendar = StatisticsUtils.getNowCalendar();
			calendar.add(Calendar.DATE, -1);
			timeText = StatisticsUtils.toDatetimeString(calendar);
		} else if (timeText.length() == 0 || timeText == "") {
			Calendar calendar = StatisticsUtils.getNowCalendar();
			calendar.add(Calendar.DATE, -1);
			timeText = StatisticsUtils.toDatetimeString(calendar);
		}
		
		Calendar calendar = StatisticsUtils.parseDatetimeString(timeText, true);
		String timeId = StatisticsUtils.getTimeId(calendar, timeTypeCode);
		logger.debug(">> timeText> {}, timeId > {}", timeText, timeId);
		MapperSession<SearchKeywordRankMapper> keywordRankMapperSession = null;
		MapperSession<SearchKeywordEmptyMapper> keywordEmptyMapperSession = null;
		int totalCount = 0;
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		ModelAndView mav = new ModelAndView();
		try {
			start = (pageNo - 1) * length;
			List<RankKeywordVO> list = null;
			if("empty".equals(keywordType)){
				keywordEmptyMapperSession = dbService.getMapperSession(SearchKeywordEmptyMapper.class);
				SearchKeywordEmptyMapper mapper = keywordEmptyMapperSession.getMapper();
				totalCount = mapper.getCount(siteId, categoryId, timeId);
				list = mapper.getEntryList(siteId, categoryId, timeId, start, length);
			}else{
				keywordRankMapperSession = dbService.getMapperSession(SearchKeywordRankMapper.class);
				SearchKeywordRankMapper mapper = keywordRankMapperSession.getMapper();
				totalCount = mapper.getCount(siteId, categoryId, timeId, rankDiffType, rankDiffOver);
				list = mapper.getEntryList(siteId, categoryId, timeId, rankDiffType, rankDiffOver, start, length);
			}
			
			String timeTextBlock = "";
			if(timeTypeCode == Calendar.DAY_OF_MONTH) {
				timeTextBlock = timeText;
			} else if(timeTypeCode == Calendar.WEEK_OF_YEAR) {
				timeTextBlock = "" +
				StatisticsUtils.toDatetimeString(
				StatisticsUtils.getFirstDayOfWeek(StatisticsUtils.parseDatetimeString(timeText, true))) + " - " +
				StatisticsUtils.toDatetimeString(
				StatisticsUtils.getLastDayOfWeek(StatisticsUtils.parseDatetimeString(timeText, false)));
			} else if(timeTypeCode == Calendar.MONTH) {
				timeTextBlock = "" +
				StatisticsUtils.toDatetimeString(
				StatisticsUtils.getFirstDayOfMonth(StatisticsUtils.parseDatetimeString(timeText, true))) + " - " +
				StatisticsUtils.toDatetimeString(
				StatisticsUtils.getLastDayOfMonth(StatisticsUtils.parseDatetimeString(timeText, false)));
			} else if(timeTypeCode == Calendar.YEAR) {
				timeTextBlock = "" +
				StatisticsUtils.toDatetimeString(
				StatisticsUtils.getFirstDayOfYear(StatisticsUtils.parseDatetimeString(timeText, true))) + " - " +
				StatisticsUtils.toDatetimeString(
				StatisticsUtils.getLastDayOfYear(StatisticsUtils.parseDatetimeString(timeText, false)));
			}
			
			mav.setViewName("report/rank/searchKeyword");
			mav.addObject("typeArray", typeArray);
			mav.addObject("categoryId", categoryId);
			mav.addObject("start", start);
			mav.addObject("length", length);
			mav.addObject("pageNo", pageNo);
			mav.addObject("timeText", timeText);
			mav.addObject("timeTextBlock",timeTextBlock);
			mav.addObject("categoryId", categoryId);
			mav.addObject("keywordType", keywordType);
			mav.addObject("totalCount", totalCount);
			mav.addObject("timeViewType", timeViewType);
			mav.addObject("list", list);
			mav.addObject("today", StatisticsUtils.toDatetimeString(
					StatisticsUtils.getNowCalendar()));
			
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (keywordRankMapperSession != null) {
				keywordRankMapperSession.closeSession();
			}
			if (keywordEmptyMapperSession != null) {
				keywordEmptyMapperSession.closeSession();
			}
		}
		
		return mav;
	}
	
	@RequestMapping("/searchKeyword/download")
	public void downloadSearchKeyword(HttpServletResponse response, @PathVariable String siteId, 
			@RequestParam String categoryId,
			@RequestParam String timeViewType, 
			@RequestParam String timeText,
			@RequestParam(required = false) String timeText2,
			@RequestParam(required = false, defaultValue="ALL") String keywordType,
			@RequestParam(required = false) Boolean forView, 
			@RequestParam(required = false, defaultValue="0") Integer maxLength) throws Exception {
		
		int rankDiffOver = 0;
		String rankDiffType = null;
		if(keywordType == null || keywordType.length() == 0){
			keywordType = "all";
		}else if(keywordType.equals("new")){
			rankDiffOver = 0;
			rankDiffType = RankDiffType.NEW.name();
		}else if(keywordType.equals("hot")){
			rankDiffOver = 30;
			rankDiffType = RankDiffType.UP.name();
		}else if(keywordType.equals("down")){
			rankDiffOver = 30;
			rankDiffType = RankDiffType.DN.name();
		}else if(keywordType.equals("empty")){
			//do nothing
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
		
		if(timeText == null){
			Calendar calendar = StatisticsUtils.getNowCalendar();
			calendar.add(Calendar.DATE, -1);
			timeText = StatisticsUtils.toDatetimeString(calendar);
		}
		
		if(timeText2 == null) {
			timeText2 = timeText;
		}
		
		Calendar calendar = StatisticsUtils.parseDatetimeString(timeText, true);
		String timeId = StatisticsUtils.getTimeId(calendar, timeTypeCode);
		logger.debug(">> timeText> {}, timeId > {}", timeText, timeId);
		MapperSession<SearchKeywordRankMapper> keywordRankMapperSession = null;
		MapperSession<SearchKeywordEmptyMapper> keywordEmptyMapperSession = null;
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		PrintWriter writer = null;
		
		try {
			
			Settings settings = environment.settingManager().getSystemSettings();
			String charEncoding = settings.getString("download.characterEncoding", "utf-8");
			String fileExt = settings.getString("download.fileExt", "txt");
			String delimiter = settings.getString("download.delimiter", "\t");
			String timeIdStr = timeId;
			response.setContentType("text/plain");
			response.setCharacterEncoding(charEncoding);
			if (forView != null && forView.booleanValue()) {
				// 다운로드 하지 않고 웹페이지에서 보여준다.
			} else {
				if("W".equalsIgnoreCase(timeViewType)) {
					Calendar c1 = StatisticsUtils.getFirstDayOfWeek(calendar);
					Calendar c2 = StatisticsUtils.getLastDayOfWeek(calendar);
					timeIdStr = "W"+StatisticsUtils.getTimeId(c1, Calendar.DATE).substring(1);
					timeIdStr += "-"+StatisticsUtils.getTimeId(c2, Calendar.DATE).substring(1);
					
				}
				response.setHeader("Content-disposition", "attachment; filename=\"" + 
					siteId + "_" + categoryId + "_rank_" + timeIdStr + "."+fileExt+"\"");
			}
			writer = response.getWriter();
			
			
			List<RankKeywordVO> list = null;
			if("empty".equals(keywordType)){
				keywordEmptyMapperSession = dbService.getMapperSession(SearchKeywordEmptyMapper.class);
				SearchKeywordEmptyMapper mapper = keywordEmptyMapperSession.getMapper();
				list = mapper.getEntryList(siteId, categoryId, timeId, 0, 0);
			}else{
				keywordRankMapperSession = dbService.getMapperSession(SearchKeywordRankMapper.class);
				SearchKeywordRankMapper mapper = keywordRankMapperSession.getMapper();
				list = mapper.getEntryList(siteId, categoryId, timeId, rankDiffType, rankDiffOver, 0, 0);
			}
			
			//print top
			
			writer.append("Search Keyword").append("\n")
				.append("Date : ").append(timeIdStr).append("\n")
				.append("Category : ").append(categoryId).append("\n")
				.append("\n");
			
			
			//print header
			writer.append("rank").append(delimiter)
				.append("keyword").append(delimiter)
				.append("count").append(delimiter)
				//.append("type").append(delimiter)
				.append("rank changes").append(delimiter)
				.append("count changes").append(delimiter)
				.append("\n");
			
			int size = list.size();
			
			if (maxLength != null && maxLength > 0) {
				size = maxLength;
			}
			
			for (int i = 0; i < size; i++) {
				RankKeywordVO entry = list.get(i);
				
				String keywordStr = StringEscapeUtils.escapeCsv(entry.getKeyword());
				keywordStr = keywordStr.replaceAll(delimiter, "\\"+delimiter);
				writer.append(String.valueOf(i+1)).append(delimiter);
				writer.append(keywordStr).append(delimiter);
				writer.append(String.valueOf(entry.getCount())).append(delimiter);
				String rankDiffStr = String.valueOf(entry.getRankDiff());
				if ("UP".equalsIgnoreCase(String.valueOf(entry.getRankDiffType()))) {
					rankDiffStr = "+" + String.valueOf(entry.getRankDiff());
				} else if ("DN".equalsIgnoreCase(String.valueOf(entry.getRankDiffType()))) {
					rankDiffStr = "-" + String.valueOf(entry.getRankDiff());
				} else if ("EQ".equals(String.valueOf(entry.getRankDiffType()))) {
					rankDiffStr = String.valueOf(entry.getRankDiff());
				} else if ("NEW".equals(String.valueOf(entry.getRankDiffType()))) {
					rankDiffStr = "NEW";
				}
				writer.append(rankDiffStr).append(delimiter);
				writer.append(String.valueOf(entry.getCountDiff()));
				writer.append("\n");
			}
			
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
			if (keywordRankMapperSession != null) {
				keywordRankMapperSession.closeSession();
			}
			if (keywordEmptyMapperSession != null) {
				keywordEmptyMapperSession.closeSession();
			}
		}
	}
}
