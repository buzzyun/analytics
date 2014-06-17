package org.fastcatsearch.analytics.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.SystemErrorMapper;
import org.fastcatsearch.analytics.db.mapper.TaskResultMapper;
import org.fastcatsearch.analytics.db.vo.SystemErrorVO;
import org.fastcatsearch.analytics.db.vo.TaskResultVO;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.Formatter;
import org.fastcatsearch.analytics.util.ResponseWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/settings")
public class GlobalConfigurationController extends AbstractController {
	
	@RequestMapping("/system")
	public ModelAndView configuration(HttpSession session, HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/system");
		
		Settings systemSettings = environment.settingManager().getSystemSettings();
		
		Pattern findKey = Pattern.compile("key([0-9]+)");
		
		if("update".equals(request.getParameter("mode"))) {
			systemSettings.properties().clear();
			Enumeration<String> parameterNames = request.getParameterNames();
			for(;parameterNames.hasMoreElements();) {
				String entry = parameterNames.nextElement();
				String inx = "";
				Matcher matcher = findKey.matcher(entry);
				if (matcher.find()) {
					inx = matcher.group(1);
				}
				
				String key = request.getParameter(entry);
				String value = request.getParameter("value"+inx);
				
				if(value!=null) {
					systemSettings.properties().setProperty(key, value);
				}
			}
			environment.settingManager().storeSystemSettings(systemSettings);
		}
		
		Set<Object> keySet = new TreeSet<Object>();
		keySet.addAll(systemSettings.properties().keySet());
		modelAndView.addObject("configKeys", keySet.iterator());
		modelAndView.addObject("configProperties", systemSettings);
		
		return modelAndView;
	}
	
	@RequestMapping("/sites")
	public ModelAndView settings(HttpSession session,
		@RequestParam(required=false) String siteId,
		@RequestParam(required=false) String categoryName ) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/sites");
		
		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		
		SiteListSetting siteCategoryListConfig = statisticsService.getSiteListSetting();
		
		List<SiteSetting> siteList = siteCategoryListConfig.getSiteList();
		
		List<CategorySetting> categoryList = new ArrayList<CategorySetting>();
		
		SiteSetting currentSiteConfig = null;
		
		String currentSiteId = null;
		String currentSiteName = "";
		
		if(siteId!=null && !"".equals(siteId)) {
			for (int inx = 0; inx < siteList.size(); inx++) {
				SiteSetting config = siteList.get(inx);
				if(siteId.equals(config.getId())) {
					currentSiteId = config.getId();
					currentSiteName = config.getName();
					currentSiteConfig = config;
					
					if(currentSiteConfig.getStatisticsSettings()!=null) {
						categoryList = currentSiteConfig.getStatisticsSettings().getCategoryList();
					}
				}
			}
		} else {
			if(siteList.size() > 0) {
				currentSiteConfig = siteList.get(0);
				if(currentSiteConfig!=null) {
					currentSiteId = currentSiteConfig.getId();
					currentSiteName = currentSiteConfig.getName();
					StatisticsSettings statisticsSettings = currentSiteConfig.getStatisticsSettings();
					if(statisticsSettings.getCategoryList()!=null) {
						categoryList = statisticsSettings.getCategoryList();
					}
				}
			}
		}
		
		modelAndView.addObject("siteList", siteList);
		modelAndView.addObject("siteId", siteId);
		modelAndView.addObject("currentSiteId", currentSiteId);
		modelAndView.addObject("currentSiteName", currentSiteName);
		modelAndView.addObject("categoryList", categoryList);
		return modelAndView;
	}
	
	@RequestMapping("/update-setting")
	public ModelAndView updateSettings(HttpSession session, HttpServletRequest request,
		@RequestParam(required=false) String mode,
		@RequestParam(required=false) String siteId,
		@RequestParam(required=false) String siteName,
		@RequestParam(required=false) String categoryId,
		@RequestParam(required=false) String categoryName ) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("text");
		
		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		
		SiteListSetting siteCategoryListConfig = statisticsService.getSiteListSetting();
		
		List<SiteSetting> siteList = siteCategoryListConfig.getSiteList();
		
		List<CategorySetting> categoryList = new ArrayList<CategorySetting>();
		
		SiteSetting currentSiteConfig = null;
		
		String currentSiteId = null;
		
		if(siteId!=null && !"".equals(siteId)) {
			for (int inx = 0; inx < siteList.size(); inx++) {
				SiteSetting config = siteList.get(inx);
				if(siteId.equals(config.getId())) {
					currentSiteId = config.getName();
					currentSiteConfig = config;
					categoryList = currentSiteConfig.getStatisticsSettings().getCategoryList();
				}
			}
		} else {
			currentSiteConfig = siteList.get(0);
			if(currentSiteConfig!=null) {
				currentSiteId = currentSiteConfig.getId();
				StatisticsSettings statisticsSettings = currentSiteConfig.getStatisticsSettings();
				if(statisticsSettings.getCategoryList()!=null) {
					categoryList = statisticsSettings.getCategoryList();
				}
			}
		}
		
		logger.trace("mode : {} / siteId : {} / categoryList : {}", mode, currentSiteId, categoryList);
		
		Writer writer = new StringWriter();
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		
		if("updateSite".equals(mode)) {
			String siteIdNew = request.getParameter("siteIdNew");
			//currentSiteConfig.setId(siteIdNew);
			currentSiteConfig.setName(siteName);
			//statisticsService.removeSite(siteId);
			//statisticsService.addSite(siteIdNew, currentSiteConfig);
			statisticsService.writeConfig();
		} else if("addSite".equals(mode)) {
			boolean found = false;
			for(int inx=0;inx < siteList.size(); inx++) {
				if(siteList.get(inx).getId().equals(siteId)) {
					found = true;
					break;
				}
			}
			if(!found) {
				statisticsService.addSite(siteId, statisticsService.newDefaultSite(siteId, siteName));
				statisticsService.writeConfig();
			}
		} else if("removeSite".equals(mode)) {
			statisticsService.removeSite(siteId);
			statisticsService.deleteConfig(siteId);
			statisticsService.writeConfig();
		}
		responseWriter.object().key("success").value("true").key("status").value(1).endObject();
		
		modelAndView.addObject("content", writer.toString());
		return modelAndView;
	}
	
	@RequestMapping("/taskResult")
	public ModelAndView taskResult(HttpSession session, 
			@RequestParam(required=false) String siteId, @RequestParam(required=false) String date ) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/taskResult");
		
		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		SiteListSetting siteCategoryListConfig = statisticsService.getSiteListSetting();
		List<SiteSetting> siteList = siteCategoryListConfig.getSiteList();
		
		if(siteId == null && siteList.size() > 0) {
			siteId = siteList.get(0).getId();
		}
		
		
		final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy.MM");
		final SimpleDateFormat targetFormat = new SimpleDateFormat("yyyyMMdd");
		
		//년, 월 입력.
		Calendar calendar = Calendar.getInstance(Locale.GERMAN);
		
		if (date != null && !"".equals(date)) {
			try {
				calendar.setTime(monthFormat.parse(date));
			} catch (ParseException ignore) {
				
			}
		}
		
		calendar.set(Calendar.DATE, 1);
		Calendar nextCalendar = (Calendar) calendar.clone();
		nextCalendar.add(Calendar.MONTH, 1);

		calendar = StatisticsUtils.getFirstDayOfWeek(calendar);
		calendar.add(Calendar.DATE, -1);
		
		Calendar dataCalendar = (Calendar)calendar.clone();
		
		AnalyticsDBService service = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<TaskResultMapper> mapperSession = service.getMapperSession(TaskResultMapper.class);
		try{
			TaskResultMapper mapper = mapperSession.getMapper();
			List<List<TaskResultVO>> monthlyTaskResult = new ArrayList<List<TaskResultVO>>();
			
			for (; dataCalendar.getTimeInMillis() < nextCalendar.getTimeInMillis();) {
				for (int weekInx = 0; weekInx < 7; weekInx++) {
					List<TaskResultVO> taskResult = null;
					dataCalendar.add(Calendar.DATE, 1);
					try {
						taskResult = mapper.getEntryList(siteId, targetFormat.format(dataCalendar.getTime()));
					} catch (Exception e) { 
						logger.error("",e);
					}
					if(taskResult==null) {
						taskResult = new ArrayList<TaskResultVO>();
					}
					monthlyTaskResult.add(taskResult);
				}
			}
			
			modelAndView.addObject("siteList", siteList);
			modelAndView.addObject("calendar", calendar);
			modelAndView.addObject("taskResult", monthlyTaskResult);
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		return modelAndView;
		
	}
	
	@RequestMapping("/systemError")
	public ModelAndView taskResult(HttpSession session, @RequestParam(required=false) Integer pageNo ) throws Exception {
		int pageSize = 5;
		
		if(pageNo == null || pageNo < 1) {
			pageNo = 1;
		}
		
		int start=(pageNo - 1) * pageSize;
		int len = pageSize;
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/systemError");
		AnalyticsDBService service = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<SystemErrorMapper> mapperSession = service.getMapperSession(SystemErrorMapper.class);
		try{
			SystemErrorMapper mapper = mapperSession.getMapper();
			int totalSize = mapper.getCount();
			List<SystemErrorVO> systemErrorList = mapper.getEntryList(start, len);
			
			modelAndView.addObject("pageNo", pageNo);
			modelAndView.addObject("pageSize", pageSize);
			modelAndView.addObject("totalSize", totalSize);
			modelAndView.addObject("systemErrorList", systemErrorList);
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		return modelAndView;
		
	}
	
	@RequestMapping("/rawLogFileData")
	public void rawLogFileData(HttpServletResponse response, 
			@RequestParam String siteId, @RequestParam String date, @RequestParam String timeViewType, @RequestParam String fileName) throws Exception {
		
		Writer writer = null;
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy.MM.dd");
		Calendar calendar = Calendar.getInstance(Locale.GERMAN);
		if (date != null && !"".equals(date)) {
			try {
				calendar.setTime(timeFormat.parse(date));
			} catch (ParseException ignore) {
			}
		}
		
		
		File statisticsHome = environment.filePaths().getStatisticsRoot().file();
		File dateKeywordBaseDir = new File(new File(statisticsHome, siteId), "date");
		File dataDir = null;
		if("W".equalsIgnoreCase(timeViewType)) {
			dataDir = StatisticsUtils.getWeekDataDir(dateKeywordBaseDir, calendar);
		} else if("M".equalsIgnoreCase(timeViewType)) {
			dataDir = StatisticsUtils.getMonthDataDir(dateKeywordBaseDir, calendar);
		} else if("Y".equalsIgnoreCase(timeViewType)) {
			dataDir = StatisticsUtils.getYearDataDir(dateKeywordBaseDir, calendar);
		} else {
			dataDir = StatisticsUtils.getDayDataDir(dateKeywordBaseDir, calendar);
		}
		
		try {
			
			File logFile = new File(dataDir, fileName);
			
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/plain");
			char[] buf = new char[1024];
			writer = response.getWriter();
			Reader reader = null;
			try{
				reader = new InputStreamReader(new FileInputStream(logFile), "utf-8");
				while(true){
					int n = reader.read(buf);
					if(n > 0) {
						writer.write(buf, 0, n);
					}else if(n < 0) {
						//끝.
						break;
					}
				}
			}finally{
				if(reader!=null) try {
					reader.close();
				} catch (Exception ignore) { }
			}
		} finally {
			writer.close();
		}
	}
	
	@RequestMapping("/rawLogFile")
	public ModelAndView rawLogFile(HttpSession session, 
			@RequestParam(required=false) String siteId, @RequestParam(required=false) String date ) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/rawLogFile");
		
		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		SiteListSetting siteCategoryListConfig = statisticsService.getSiteListSetting();
		List<SiteSetting> siteList = siteCategoryListConfig.getSiteList();
		
		if(siteId == null && siteList.size() > 0) {
			siteId = siteList.get(0).getId();
		}
		
		File statisticsHome = environment.filePaths().getStatisticsRoot().file();
		File dateKeywordBaseDir = new File(new File(statisticsHome, siteId), "date");
		
		final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy.MM");
		
		//년, 월 입력.
		Calendar calendar = Calendar.getInstance(Locale.GERMAN);
		
		if (date != null && !"".equals(date)) {
			try {
				calendar.setTime(monthFormat.parse(date));
			} catch (ParseException ignore) {
				
			}
		}
		
		calendar.set(Calendar.DATE, 1);
		Calendar nextCalendar = (Calendar) calendar.clone();
		nextCalendar.add(Calendar.MONTH, 1);

		calendar = StatisticsUtils.getFirstDayOfWeek(calendar);
		calendar.add(Calendar.DATE, -1);
		
		Calendar dataCalendar = (Calendar)calendar.clone();
		
		List<List<String[]>> dailyFileInfoList = new ArrayList<List<String[]>>();
		
		String[] fileList = new String[]{ "raw.log", "type_raw.log", "click_raw.log" };
		for (; dataCalendar.getTimeInMillis() < nextCalendar.getTimeInMillis();) {
			for (int weekInx = 0; weekInx < 7; weekInx++) {
				List<String[]> fileInfoList = new ArrayList<String[]>();
				dataCalendar.add(Calendar.DATE, 1);
				
				File dataDir = StatisticsUtils.getDayDataDir(dateKeywordBaseDir, dataCalendar);
				
				for(String fileName : fileList) {
					File logFile = new File(dataDir, fileName);
					String fileSize = "-";
					if(logFile.exists()) {
						fileSize = Formatter.getFormatSize(logFile.length());
					}
					
					fileInfoList.add(new String[] { fileName, fileSize});
				}
				
				dailyFileInfoList.add(fileInfoList);
			}
		}
		
		modelAndView.addObject("siteList", siteList);
		modelAndView.addObject("calendar", calendar);
		modelAndView.addObject("dailyFileInfoList", dailyFileInfoList);
		return modelAndView;
		
	}
}
