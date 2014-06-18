package org.fastcatsearch.analytics.web.controller;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.UserAccountMapper;
import org.fastcatsearch.analytics.db.vo.UserAccountVO;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.ResponseWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController extends AbstractController {
	
	@RequestMapping("/settings/index")
	public ModelAndView user(HttpSession session) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/index");
		
		AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<UserAccountMapper> mapperSession = null;
		
		try {
				
			mapperSession = analyticsDBService.getMapperSession(UserAccountMapper.class);
			UserAccountMapper mapper = mapperSession.getMapper();
			List<UserAccountVO> entryList = mapper.getEntryList();
			modelAndView.addObject("userList",entryList);
		} finally {
			
			if(mapperSession!=null) {
				try { mapperSession.closeSession(); } catch (Exception e) { }
			}
		}
		
		return modelAndView;
	}
	
	@RequestMapping("/settings/get-user")
	public ModelAndView getUser(@RequestParam("id") int id ) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("text");
		Writer writer = null;
		MapperSession<UserAccountMapper> mapperSession = null;
		
		try {
			writer = new StringWriter();
			ResponseWriter responseWriter = getDefaultResponseWriter(writer);
			AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
				
			mapperSession = analyticsDBService.getMapperSession(UserAccountMapper.class);
			UserAccountMapper mapper = mapperSession.getMapper();
			UserAccountVO entry = mapper.getEntry(id);
			
			responseWriter.object()
				.key("id").value(entry.id)
				.key("name").value(entry.name)
				.key("userId").value(entry.userId)
				.key("email").value(entry.email)
				.key("sms").value(entry.sms)
			.endObject();
			
			responseWriter.done();
			modelAndView.addObject("content",writer.toString());
		} finally {
			if(mapperSession!=null) {
				try { mapperSession.closeSession(); } catch (Exception e) { }
			}
			
			if(writer!=null) try {
				writer.close();
			} catch (Exception ignore) { }
		}
		
		
		return modelAndView;
	}
	
	@RequestMapping("/settings/update-user")
	public ModelAndView updateUser( @RequestParam("mode") String mode
			, @RequestParam("id") Integer id 
			, @RequestParam("name") String name
			, @RequestParam("userId") String userId
			, @RequestParam("email") String email
			, @RequestParam("sms") String sms
			, @RequestParam("password") String password ) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("text");
		Writer writer = new StringWriter();
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		
		AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<UserAccountMapper> mapperSession = null;

		logger.debug("updating user...");
		
		try {
				
			mapperSession = analyticsDBService.getMapperSession(UserAccountMapper.class);
			UserAccountMapper mapper = mapperSession.getMapper();
			UserAccountVO entry = null;
			String updateMode = null;
			boolean doChangePassword = false;
			
			if("update".equals(mode)) {
				
				if(id!=null) {
					entry = mapper.getEntry(id);
					if(entry!=null) {
						if (password == null || "".equals(password)) {
							doChangePassword = true;
						}
						updateMode = "update";
					} else {
						updateMode = "insert";
					}
				}
				
				if(entry == null) {
					entry = new UserAccountVO();
					entry.id = id;
					updateMode = "insert";
				}
				
				entry.name = name;
				entry.userId = userId;
				if(!doChangePassword) {
					entry.setEncryptedPassword(password);
				}
				
				entry.email = email;
				entry.sms = sms;
				
				
				if("update".equals(updateMode)) {
					mapper.updateEntry(entry);
				} else {
					mapper.putEntry(entry);
				}
			} else if("delete".equals(mode)) {
				if(id!=null) {
					mapper.deleteEntry(id);
				}
			}
			
			responseWriter.object().key("success").value("true").key("status").value(1).endObject();
			
		} catch (Exception e) {
			logger.error("", e);
			responseWriter.object().key("success").value("false").key("status").value(1).endObject();
		} finally {
			if(mapperSession!=null) {
				try { mapperSession.closeSession(); } catch (Exception e) { }
			}
			responseWriter.done();
		}
		
		modelAndView.addObject("content", writer.toString());
		return modelAndView;
	}
}
