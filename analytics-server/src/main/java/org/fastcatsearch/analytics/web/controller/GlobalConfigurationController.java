package org.fastcatsearch.analytics.web.controller;

import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.TaskResultMapper;
import org.fastcatsearch.analytics.db.vo.TaskResultVO;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.service.ServiceManager;
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
		Calendar calendar = Calendar.getInstance();
		
		if (date != null && !"".equals(date)) {
			try {
				calendar.setTime(monthFormat.parse(date));
			} catch (ParseException ignore) {
				
			}
		}
		calendar.set(Calendar.DATE, 1);
		Calendar nextCalendar = (Calendar)calendar.clone();
		nextCalendar.add(Calendar.MONTH, 1);

		calendar = StatisticsUtils.getFirstDayOfWeek(calendar);
		
		Calendar dataCalendar = (Calendar)calendar.clone();
		
		AnalyticsDBService service = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<TaskResultMapper> mapperSession = service.getMapperSession(TaskResultMapper.class);
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
		
		modelAndView.addObject("siteId", siteId);
		modelAndView.addObject("siteList", siteList);
		modelAndView.addObject("calendar", calendar);
		modelAndView.addObject("taskResult", monthlyTaskResult);
		return modelAndView;
		
	}
	
	@RequestMapping("/systemError")
	public ModelAndView taskResult(HttpSession session, @RequestParam(required=false) Integer pageNo ) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/systemError");
		return modelAndView;
		
	}
}
