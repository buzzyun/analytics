package org.fastcatsearch.analytics.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.UserAccountMapper;
import org.fastcatsearch.analytics.db.vo.UserAccountVO;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.web.http.ResponseHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController extends AbstractController {

	public static final String USER_ID = "_USERID";
	public static final String USER_NAME = "_USERNAME";

	@RequestMapping("/index")
	public ModelAndView index(HttpSession session) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:main/start.html");
		return mav;
	}

	@RequestMapping("/login")
	public ModelAndView login() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		return mav;
	}

	@RequestMapping(value = "/doLogin", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView doLogin(HttpSession session, @RequestParam("userId") String userId, @RequestParam("password") String password,
			@RequestParam(value = "redirect", required = false) String redirect) throws Exception {

		logger.debug("login {}:{}", userId, password);

		if (userId.length() == 0 || password.length() == 0) {
			ModelAndView mav = new ModelAndView();
			mav.setViewName("redirect:login.html");
			return mav;
		}

		AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<UserAccountMapper> mapperSession = analyticsDBService.getMapperSession(UserAccountMapper.class);
		try{
			UserAccountMapper mapper = mapperSession.getMapper();
			UserAccountVO account = mapper.getEntryByUserId(userId);
			if (account != null && account.isEqualsEncryptedPassword(password)) {
				// 로그인이 올바를 경우 메인 화면으로 이동한다.
				ModelAndView mav = new ModelAndView();
				if (redirect != null && redirect.length() > 0) {
					mav.setViewName("redirect:" + redirect);
				} else {
					// 로그인되었다면 바로 start.html로 간다.
					mav.setViewName("redirect:main/start.html");
				}
	
				// session에 로그인 정보를 담는다.
				session.setAttribute(USER_ID, userId);
				session.setAttribute(USER_NAME, account.name);
	
				return mav;
			}
		}finally {
			if(mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		return mav;

	}

	@RequestMapping("/main/logout")
	public ModelAndView logout(HttpSession session) throws Exception {

		// 세션삭제를 처리한다.
		ResponseHttpClient httpClient = (ResponseHttpClient) session.getAttribute("httpclient");
		if (httpClient != null) {
			httpClient.close();
		}
		session.invalidate();
		// 로긴 화면으로 이동한다.
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		return mav;
	}

	@RequestMapping("/main/start")
	public ModelAndView viewStart() throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("start");
		return mav;
	}

	@RequestMapping("/main/profile")
	public ModelAndView viewProfile(HttpSession session) throws Exception {
		String userId = (String) session.getAttribute(USER_ID);
		AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<UserAccountMapper> mapperSession = null;
		try {
			mapperSession = analyticsDBService.getMapperSession(UserAccountMapper.class);
			UserAccountMapper mapper = mapperSession.getMapper();
			UserAccountVO account = mapper.getEntryByUserId(userId);

			ModelAndView mav = new ModelAndView();
			mav.setViewName("profile");
			mav.addObject("account", account);
			return mav;
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
	}

	@RequestMapping("/main/profile-update")
	public ModelAndView updateProfile(HttpSession session, @RequestParam String id, @RequestParam String name, @RequestParam(required = false) String email, @RequestParam(required = false) String sms,
			@RequestParam(required = false) String password, @RequestParam(required = false) String newPassword, @RequestParam(required = false) String reqPassword)
			throws Exception {
		String userId = (String) session.getAttribute(USER_ID);
		AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<UserAccountMapper> mapperSession = null;
		try {
			mapperSession = analyticsDBService.getMapperSession(UserAccountMapper.class);
			UserAccountMapper mapper = mapperSession.getMapper();
			UserAccountVO account = mapper.getEntryByUserId(userId);
			if(account != null){
				
				account.name = name;
				account.email = email;
				account.sms = sms;
				if(password != null && newPassword != null && newPassword.equals(reqPassword) && account.isEqualsEncryptedPassword(password)){
					account.setEncryptedPassword(newPassword);
				}
				mapper.updateEntry(account);

				//한번더 디비에서 가져온다.
				account = mapper.getEntryByUserId(userId);
				
			}
			ModelAndView mav = new ModelAndView();
			mav.setViewName("redirect:profile.html");
			mav.addObject("account", account);
			return mav;
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
	}

	@RequestMapping("/main/request")
	public ModelAndView request(@RequestParam String uri, @RequestParam String dataType) throws Exception {
		String content = uri + " : " + dataType;
		ModelAndView mav = new ModelAndView();
		mav.addObject("content", content);
		mav.setViewName("text");
		return mav;

	}
}
