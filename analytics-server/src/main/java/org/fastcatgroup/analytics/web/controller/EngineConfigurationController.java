package org.fastcatgroup.analytics.web.controller;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.UserAccountMapper;
import org.fastcatgroup.analytics.db.vo.UserAccountVO;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EngineConfigurationController extends AbstractController {
	
	@RequestMapping("/settings/configuration")
	public ModelAndView configuration(HttpSession session) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/configuration");
		
//		AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
//		MapperSession<UserAccountMapper> mapperSession = null;
//		
//		try {
//				
//			mapperSession = analyticsDBService.getMapperSession(UserAccountMapper.class);
//			UserAccountMapper mapper = mapperSession.getMapper();
//			List<UserAccountVO> entryList = mapper.getEntryList();
//			modelAndView.addObject("userList",entryList);
//		} finally {
//			
//			if(mapperSession!=null) {
//				try { mapperSession.closeSession(); } catch (Exception e) { }
//			}
//		}
//		
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
