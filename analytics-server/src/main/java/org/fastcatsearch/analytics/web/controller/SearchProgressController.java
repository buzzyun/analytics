package org.fastcatsearch.analytics.web.controller;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SearchHitMapper;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordHitMapper;
import org.fastcatsearch.analytics.db.vo.SearchHitVO;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report/progress")
public class SearchProgressController extends AbstractController {

	@RequestMapping("/hitCount")
	public ModelAndView index(@PathVariable String siteId,
			@RequestParam(defaultValue = "_root") String categoryId,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String timeText, //2014.03.20 - 2014.03.25
			@RequestParam(required = false) String timeViewType) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/hit");
		
		if(timeText == null){
			Calendar calendar = StatisticsUtils.getCalendar();
			calendar.add(Calendar.DATE, -7);
			String timeFrom = StatisticsUtils.toDatetimeString(calendar);
			
			calendar = StatisticsUtils.getCalendar();
			calendar.add(Calendar.DATE, -1);
			String timeTo = StatisticsUtils.toDatetimeString(calendar);
			
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
		Calendar startTime = StatisticsUtils.parseDatetimeString(timeFrom,true);
		Calendar endTime = StatisticsUtils.parseDatetimeString(timeTo,false);
		String startTimeId = StatisticsUtils.getTimeId(startTime, timeTypeCode);
		String endTimeId = StatisticsUtils.getTimeId(endTime, timeTypeCode);
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
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for(int inx=0;inx<list.size();inx++) {
					SearchHitVO vo = list.get(inx);
					logger.trace("vo time:{}", vo.getTimeId());
				}
				for (int timeInx = 0; startTime.getTimeInMillis() <= endTime.getTimeInMillis(); timeInx++) {
					String timeString = StatisticsUtils.getTimeId(startTime, timeTypeCode);
					
					logger.trace("timeString > {}", timeString);
					int hit = 0;
					
					if(timeInx < list.size()) {

						SearchHitVO vo = list.get(timeInx);
						Calendar timeCurrent = StatisticsUtils.parseTimeId(vo.getTimeId());
						
						if(timeString.equals(vo.getTimeId())) {
							hit = vo.getHit();
							vo.setTimeId(timeString);
							list.set(timeInx, vo);
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
					if(logger.isTraceEnabled()) {
						logger.trace("startTime:{}", StatisticsUtils.toDatetimeString(startTime, timeTypeCode));
						logger.trace("startTime:{} / endTime:{}",sdf.format(startTime.getTime()), sdf.format(endTime.getTime()));
					}
				}
				for(int inx=0;inx<list.size();inx++) {
					SearchHitVO vo = list.get(inx);
					Calendar parsedTime = StatisticsUtils.parseTimeId(vo.getTimeId());
					String timeStr = StatisticsUtils.toDatetimeString(parsedTime, timeTypeCode);
					logger.trace("vo time:{}={} / {}", vo.getTimeId(), timeStr, parsedTime.getTime());
					vo.setTimeId(timeStr);
				}
			}
			mav.addObject("categoryId", categoryId);
			mav.addObject("timeText", timeText);
			mav.addObject("timeViewType", timeViewType);
			mav.addObject("list", list);
			
			
			mav.addObject("today", StatisticsUtils.toDatetimeString( StatisticsUtils.getCalendar() ));
			
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
	
	@RequestMapping("/hit/download")
	public void downloadHitProgress(HttpServletResponse response, 
			@PathVariable String siteId,
			@RequestParam(defaultValue = "_root") String categoryId,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String timeText, //2014.03.20 - 2014.03.25
			@RequestParam(required = false) String timeViewType,
			@RequestParam(required = false) Boolean forView) {

		
		if(timeText == null){
			Calendar calendar = StatisticsUtils.getCalendar();
			calendar.add(Calendar.DATE, -7);
			String timeFrom = StatisticsUtils.toDatetimeString(calendar);
			
			calendar = StatisticsUtils.getCalendar();
			calendar.add(Calendar.DATE, -1);
			String timeTo = StatisticsUtils.toDatetimeString(calendar);
			
			timeText = timeFrom + " - " + timeTo;
		}
		
		String encodedKeyword = keyword;
		
		if(keyword!=null && !"".equals(keyword)) {
			try {
				keyword = URLDecoder.decode(keyword, "utf-8");
				encodedKeyword = URLEncoder.encode(keyword, "utf-8");
			} catch (UnsupportedEncodingException ignore) { }
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
		Calendar startTime = StatisticsUtils.parseDatetimeString(timeFrom,true);
		Calendar endTime = StatisticsUtils.parseDatetimeString(timeTo,false);
		String startTimeId = StatisticsUtils.getTimeId(startTime, timeTypeCode);
		String endTimeId = StatisticsUtils.getTimeId(endTime, timeTypeCode);
		logger.debug("New time id >> {} ~ {} > {}", startTimeId, endTimeId, timeViewType);
		
		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SearchHitMapper> hitMapperSession = null;
		MapperSession<SearchKeywordHitMapper> keywordHitMapperSession = null;
		PrintWriter writer = null;
		
		Settings settings = environment.settingManager().getSystemSettings();
		String charEncoding = settings.getString("download.characterEncoding", "utf-8");
		String fileExt = settings.getString("download.fileExt", "txt");
		String delimiter = settings.getString("download.delimiter", "\t");
		String timeIdStr = startTimeId+"-"+endTimeId;
		
		response.setContentType("text/plain");
		response.setCharacterEncoding(charEncoding);
		if (forView != null && forView.booleanValue()) {
			// 다운로드 하지 않고 웹페이지에서 보여준다.
		} else {
			
			if("W".equals(timeViewType)) {
				timeIdStr = 
				StatisticsUtils.getTimeId(startTime, Calendar.DATE) +"-"+
				StatisticsUtils.getTimeId(endTime, Calendar.DATE);
			}
			
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ siteId + "_" + categoryId + "_hit_" + timeIdStr + "_"
					+ encodedKeyword + "."+fileExt+"\"");
		}
		
		try {
			
			writer = response.getWriter();
			
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
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for(int inx=0;inx<list.size();inx++) {
					SearchHitVO vo = list.get(inx);
					logger.trace("vo time:{}", vo.getTimeId());
				}
				for (int timeInx = 0; startTime.getTimeInMillis() <= endTime.getTimeInMillis(); timeInx++) {
					String timeString = StatisticsUtils.getTimeId(startTime, timeTypeCode);
					
					logger.trace("timeString > {}", timeString);
					int hit = 0;
					
					if(timeInx < list.size()) {

						SearchHitVO vo = list.get(timeInx);
						Calendar timeCurrent = StatisticsUtils.parseTimeId(vo.getTimeId());
						
						if(timeString.equals(vo.getTimeId())) {
							hit = vo.getHit();
							vo.setTimeId(timeString);
							list.set(timeInx, vo);
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
					if(logger.isTraceEnabled()) {
						logger.trace("startTime:{}", StatisticsUtils.toDatetimeString(startTime, timeTypeCode));
						logger.trace("startTime:{} / endTime:{}",sdf.format(startTime.getTime()), sdf.format(endTime.getTime()));
					}
				}
				
				//print top
				writer.append("Click-through Detail").append("\n")
					.append("Date : ").append(timeIdStr).append("\n")
					.append("Category : ").append(categoryId).append("\n")
					.append("\n");
				
				//print header
				writer.append("no").append(delimiter)
					.append("date").append(delimiter)
					.append("count").append(delimiter)
					.append("max time").append(delimiter)
					.append("average time").append("\n");
				
				
				for(int inx=0;inx<list.size();inx++) {
					SearchHitVO vo = list.get(inx);
					Calendar parsedTime = StatisticsUtils.parseTimeId(vo.getTimeId());
					String timeStr = StatisticsUtils.toDatetimeString(parsedTime, timeTypeCode);
					logger.trace("vo time:{}={} / {}", vo.getTimeId(), timeStr, parsedTime.getTime());
					vo.setTimeId(timeStr);
					writer.append(String.valueOf(inx+1)).append(delimiter);
					writer.append(String.valueOf(vo.getTimeId())).append(delimiter);
					writer.append(String.valueOf(vo.getHit())).append(delimiter);
					writer.append(String.valueOf(vo.getMaxTime())).append(delimiter);
					writer.append(String.valueOf(vo.getAvgTime()));
					writer.append("\n");
					
				}
			}
			
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
			if (keywordHitMapperSession != null) {
				keywordHitMapperSession.closeSession();
			}
			if (hitMapperSession != null) {
				hitMapperSession.closeSession();
			}
		}
	}
}
