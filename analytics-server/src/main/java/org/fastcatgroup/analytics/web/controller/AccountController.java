package org.fastcatgroup.analytics.web.controller;

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
public class AccountController extends AbstractController {
	
	@RequestMapping("/settings/index")
	public ModelAndView index(HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/settings/user.html");
		return modelAndView;
	}

	@RequestMapping("/settings/user")
	public ModelAndView user(HttpSession session) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/settings/user");
		
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
	@ResponseBody
	public void getUser(HttpServletResponse response, @RequestParam("id") int id ) throws Exception {
		
		Writer writer = response.getWriter();
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		
		AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<UserAccountMapper> mapperSession = null;
		
		try {
				
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
		} finally {
			if(mapperSession!=null) {
				try { mapperSession.closeSession(); } catch (Exception e) { }
			}
		}
	}
	
	@RequestMapping("/settings/update-user")
	@ResponseBody
	public void updateUser(HttpServletResponse response
			, @RequestParam("mode") String mode
			, @RequestParam("id") Integer id 
			, @RequestParam("name") String name
			, @RequestParam("userId") String userId
			, @RequestParam("email") String email
			, @RequestParam("sms") String sms
			, @RequestParam("password") String password
			, @RequestParam("confirmPassword") String confirm
			
			) throws Exception {
		Writer writer = response.getWriter();
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
						if (password == null || "".equals(password) || !password.equals(confirm)) {
							doChangePassword = true;
						}
					} else {
						updateMode = "insert";
					}
				} else {
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
		}
		responseWriter.done();
	}
}
