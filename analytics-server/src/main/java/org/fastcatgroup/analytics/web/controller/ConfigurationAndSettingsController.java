package org.fastcatgroup.analytics.web.controller;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
	public ModelAndView settings(HttpSession session) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/settings");
		
		StatisticsService service = ServiceManager.getInstance().getService(StatisticsService.class);
		
		List<SiteCategoryConfig> siteList = service.getSiteCategoryListConfig().getList();
		
		modelAndView.addObject("siteList", siteList);
//		Writer writer = new StringWriter();
//		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
//		
//		AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
//		MapperSession<UserAccountMapper> mapperSession = null;
//		
//		try {
//				
//			mapperSession = analyticsDBService.getMapperSession(UserAccountMapper.class);
//			UserAccountMapper mapper = mapperSession.getMapper();
//			UserAccountVO entry = mapper.getEntry(id);
//			
//			responseWriter.object()
//				.key("id").value(entry.id)
//				.key("name").value(entry.name)
//				.key("userId").value(entry.userId)
//				.key("email").value(entry.email)
//				.key("sms").value(entry.sms)
//			.endObject();
//			
//			responseWriter.done();
//		} finally {
//			if(mapperSession!=null) {
//				try { mapperSession.closeSession(); } catch (Exception e) { }
//			}
//		}
//		
//		modelAndView.addObject("content",writer.toString());
//		
		return modelAndView;
	}
}
