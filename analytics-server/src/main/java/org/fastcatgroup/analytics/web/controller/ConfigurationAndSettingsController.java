package org.fastcatgroup.analytics.web.controller;

import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import org.fastcatgroup.analytics.env.Settings;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConfigurationAndSettingsController extends AbstractController {
	
	@RequestMapping("/settings/configuration")
	public ModelAndView configuration(HttpSession session) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/configuration");
		
		Settings systemSettings = environment.settingManager().getSystemSettings();
		
		//속성 길이가 긴 경우 textarea 를 사용하기 위해
		//페이지 내에서는 키워드 값을 하드코딩 하여 이용한다. 
		Enumeration<Object> keys = systemSettings.properties().keys();
		
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
