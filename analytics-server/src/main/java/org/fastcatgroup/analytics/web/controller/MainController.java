package org.fastcatgroup.analytics.web.controller;

import javax.servlet.http.HttpSession;

import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.UserAccountMapper;
import org.fastcatgroup.analytics.db.vo.UserAccountVO;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.web.http.ResponseHttpClient;
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
	public ModelAndView doLogin(HttpSession session
			, @RequestParam("userId") String userId, @RequestParam("password") String password
			, @RequestParam(value="redirect", required=false) String redirect) throws Exception {

		logger.debug("login {}:{}", userId, password);

		if (userId.length() == 0 || password.length() == 0) {
			ModelAndView mav = new ModelAndView();
			mav.setViewName("redirect:login.html");
			return mav;
		}

		AnalyticsDBService analyticsDBService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<UserAccountMapper> mapperSession = analyticsDBService.getMapperSession(UserAccountMapper.class);
		UserAccountMapper mapper = mapperSession.getMapper();
		UserAccountVO account = mapper.getEntryByUserId(userId);
		if (account.isEqualsEncryptedPassword(password)) {
			// 로그인이 올바를 경우 메인 화면으로 이동한다.
			ModelAndView mav = new ModelAndView();
			if(redirect != null && redirect.length() > 0){
				mav.setViewName("redirect:"+redirect);
			}else{
				// 로그인되었다면 바로 start.html로 간다.
				mav.setViewName("redirect:main/start.html");	
			}
			
			//session에 로그인 정보를 담는다.
			session.setAttribute(USER_ID, userId);
			session.setAttribute(USER_NAME, account.name);
			
			return mav;
		}

		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		return mav;

	}

	@RequestMapping("/main/logout")
	public ModelAndView logout(HttpSession session) throws Exception {

		//세션삭제를 처리한다.
		ResponseHttpClient httpClient = (ResponseHttpClient) session.getAttribute("httpclient");
		if(httpClient != null){
			httpClient.close();
		}
		session.invalidate();
		// 로긴 화면으로 이동한다.
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		return mav;
	}

	@RequestMapping("/main/start")
	public ModelAndView viewStart() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("start");
		return mav;
	}

	@RequestMapping("/main/dashboard")
	public ModelAndView dashboard() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("dashboard");
		return mav;
	}

	@RequestMapping("/main/settings")
	public ModelAndView settings() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("settings");
		return mav;
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
