package org.fastcatgroup.analytics.web.controller;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting;
import org.fastcatgroup.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConfigurationAndSettingsController extends AbstractController {
	
	@RequestMapping("/settings/configuration")
	public ModelAndView configuration(HttpSession session, HttpServletRequest request) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/configuration");
		
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
	
	@RequestMapping("/settings/settings")
	public ModelAndView settings(HttpSession session,
		@RequestParam(required=false) String siteId,
		@RequestParam(required=false) String categoryName ) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/settings");
		
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
					categoryList = currentSiteConfig.getStatisticsSettings().getCategoryList();
				}
			}
		} else {
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
		
		modelAndView.addObject("siteList", siteList);
		modelAndView.addObject("siteId", siteId);
		modelAndView.addObject("currentSiteId", currentSiteId);
		modelAndView.addObject("currentSiteName", currentSiteName);
		modelAndView.addObject("categoryList", categoryList);
		return modelAndView;
	}
	
	@RequestMapping("/settings/update-setting")
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
		
		logger.debug("mode : {} / siteId : {} / categoryList : {}", mode, currentSiteId, categoryList);
		
		Writer writer = new StringWriter();
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		
		if("update".equals(mode)) {
			categoryList.clear();
			categoryList.add(new CategorySetting("_root","ALL",true,true,true));
			
			int count = Integer.parseInt(request.getParameter("count"));
			for (int inx = 1; inx <= count; inx++) {
				String categoryIdGet = request.getParameter("categoryId"+inx);
				String categoryNameGet = request.getParameter("categoryName"+inx);
				if(categoryIdGet!=null && !"".equals(categoryIdGet)) {
					categoryList.add(new CategorySetting(categoryIdGet, categoryNameGet, true,true,true));
				}
			}
			statisticsService.writeConfig();
		} else if("add".equals(mode)) {
			boolean found = false;
			CategorySetting categoryConfig = new CategorySetting(categoryId, categoryName, true,true,true);
			for (int inx = 0; inx < categoryList.size(); inx++) {
				if(categoryList.get(inx).getId().equals(categoryId)) {
					categoryList.set(inx, categoryConfig);
					found = true;
					break;
				}
			}
			if(!found) {
				categoryList.add(new CategorySetting(categoryId, categoryName,true,true,true));
			}
			statisticsService.writeConfig();
		} else if("remove".equals(mode)) {
			for (int inx = 0; inx < categoryList.size(); inx++) {
				if(categoryList.get(inx).getId().equals(categoryId)) {
					categoryList.remove(inx);
					break;
				}
			}
			statisticsService.writeConfig();
		} else if("updateSite".equals(mode)) {
			String siteIdNew = request.getParameter("siteIdNew");
			currentSiteConfig.setId(siteIdNew);
			currentSiteConfig.setName(siteName);
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
				SiteSetting siteCategoryConfig = new SiteSetting(siteId, siteName);
				List<CategorySetting> newCategoryList = new ArrayList<CategorySetting>();
				newCategoryList.add(new CategorySetting("_root", "ALL",true,true,true));
				StatisticsSettings statisticsSettings = new StatisticsSettings();
				statisticsSettings.setCategoryList(newCategoryList);
				siteCategoryConfig.setStatisticsSettings(statisticsSettings);
				
				siteList.add(siteCategoryConfig);
				statisticsService.writeConfig();
			}
		} else if("removeSite".equals(mode)) {
			for(int inx=0;inx < siteList.size(); inx++) {
				if(siteList.get(inx).getId().equals(siteId)) {
					siteList.remove(inx);
					break;
				}
			}
			statisticsService.writeConfig();
		}
		responseWriter.object().key("success").value("true").key("status").value(1).endObject();
		
		modelAndView.addObject("content", writer.toString());
		return modelAndView;
	}
}