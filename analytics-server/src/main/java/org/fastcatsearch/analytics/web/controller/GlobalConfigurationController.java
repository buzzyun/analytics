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
import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CategorySetting;
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
	public ModelAndView taskResult(HttpSession session, @RequestParam(required=false) String date ) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/taskResult");
		
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM");
		//년, 월 입력.
		Calendar calendar = Calendar.getInstance();
		
		if(date != null && !"".equals(date)) {
			try {
				calendar.setTime(dateFormat.parse(date));
			} catch (ParseException ignore) {
				
			}
		}
		calendar.set(Calendar.DATE, 1);
		calendar.add(Calendar.DATE, - calendar.get(Calendar.DAY_OF_WEEK) + 1);
		//1일이 해당하는 주의 시작일자를 구한다. 
		modelAndView.addObject("calendar", calendar);
		return modelAndView;
		
	}
	
	@RequestMapping("/systemError")
	public ModelAndView taskResult(HttpSession session, @RequestParam(required=false) Integer pageNo ) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/systemError");
		return modelAndView;
		
	}
}
