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
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.CategoryConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
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
		
		SiteCategoryListConfig siteCategoryListConfig = statisticsService.getSiteCategoryListConfig();
		
		List<SiteCategoryConfig> siteList = siteCategoryListConfig.getList();
		
		List<CategoryConfig> categoryList = new ArrayList<CategoryConfig>();
		
		SiteCategoryConfig currentSiteConfig = null;
		
		String currentSiteId = null;
		
		if(siteId!=null && !"".equals(siteId)) {
			for (int inx = 0; inx < siteList.size(); inx++) {
				SiteCategoryConfig config = siteList.get(inx);
				if(siteId.equals(config.getSiteId())) {
					currentSiteId = config.getSiteId();
					currentSiteConfig = config;
					categoryList = currentSiteConfig.getCategoryList();
				}
			}
		} else {
			currentSiteConfig = siteList.get(0);
			if(currentSiteConfig!=null) {
				currentSiteId = currentSiteConfig.getSiteId();
				if(currentSiteConfig.getCategoryList()!=null) {
					categoryList = currentSiteConfig.getCategoryList();
				}
			}
		}
		
		modelAndView.addObject("siteList", siteList);
		modelAndView.addObject("siteId", siteId);
		modelAndView.addObject("currentSiteId", currentSiteId);
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
		
		SiteCategoryListConfig siteCategoryListConfig = statisticsService.getSiteCategoryListConfig();
		
		List<SiteCategoryConfig> siteList = siteCategoryListConfig.getList();
		
		List<CategoryConfig> categoryList = new ArrayList<CategoryConfig>();
		
		SiteCategoryConfig currentSiteConfig = null;
		
		String currentSiteId = null;
		
		if(siteId!=null && !"".equals(siteId)) {
			for (int inx = 0; inx < siteList.size(); inx++) {
				SiteCategoryConfig config = siteList.get(inx);
				if(siteId.equals(config.getSiteId())) {
					currentSiteId = config.getSiteId();
					currentSiteConfig = config;
					categoryList = currentSiteConfig.getCategoryList();
				}
			}
		} else {
			currentSiteConfig = siteList.get(0);
			if(currentSiteConfig!=null) {
				currentSiteId = currentSiteConfig.getSiteId();
				if(currentSiteConfig.getCategoryList()!=null) {
					categoryList = currentSiteConfig.getCategoryList();
				}
			}
		}
		
		logger.debug("mode : {} / siteId : {} / categoryList : {}", mode, currentSiteId, categoryList);
		
		Writer writer = new StringWriter();
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		
		if("update".equals(mode)) {
			Pattern sitePattern = Pattern.compile("^categoryId([0-9]+)");
			//모든 파라메터를 다 살펴보아야 한다.
			Enumeration<String> paramNames = request.getParameterNames();
			while(paramNames.hasMoreElements()) {
				String paramKey = paramNames.nextElement();
				Matcher matcher = sitePattern.matcher(paramKey);
				if(matcher.find()) {
					String number = matcher.group(1);
					String categoryIdGet = request.getParameter(paramKey);
					String categoryNameGet = request.getParameter("categoryName"+number);
					String categoryIdOrg = request.getParameter("categoryOrg"+number);
					for (int inx = 0; inx < categoryList.size(); inx++) {
						if(categoryList.get(inx).getId().equals(categoryIdOrg)) {
							categoryList.set(inx, new CategoryConfig(categoryIdGet, categoryNameGet));
							break;
						}
					}
				}
			}
		} else if("add".equals(mode)) {
			boolean found = false;
			CategoryConfig categoryConfig = new CategoryConfig(categoryId, categoryName);
			for (int inx = 0; inx < categoryList.size(); inx++) {
				if(categoryList.get(inx).getId().equals(categoryId)) {
					categoryList.set(inx, categoryConfig);
					found = true;
					break;
				}
			}
			if(!found) {
				categoryList.add(new CategoryConfig(categoryId, categoryName));
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
			currentSiteConfig.setSiteId(siteId);
			currentSiteConfig.setSiteName(siteName);
			statisticsService.writeConfig();
		} else if("addSite".equals(mode)) {
			boolean found = false;
			for(int inx=0;inx < siteList.size(); inx++) {
				if(siteList.get(inx).getSiteId().equals(siteId)) {
					found = true;
					break;
				}
			}
			
			if(!found) {
				SiteCategoryConfig siteCategoryConfig = new SiteCategoryConfig(siteId, siteName);
				List<CategoryConfig> newCategoryList = new ArrayList<CategoryConfig>();
				newCategoryList.add(new CategoryConfig("_root", "_root"));
				siteCategoryConfig.setCategoryList(newCategoryList);
				siteList.add(siteCategoryConfig);
				statisticsService.writeConfig();
			}
		} else if("removeSite".equals(mode)) {
			for(int inx=0;inx < siteList.size(); inx++) {
				if(siteList.get(inx).getSiteId().equals(siteId)) {
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
