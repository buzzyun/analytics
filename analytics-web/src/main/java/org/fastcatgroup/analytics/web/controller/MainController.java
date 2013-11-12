package org.fastcatgroup.analytics.web.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	private static Logger logger = LoggerFactory.getLogger(MainController.class);

	@RequestMapping("/index")
	public ModelAndView index() {

		// TODO 로긴여부 확인.
		// 로긴되어있으면 start로, 아니면 login페이지로.
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:login.html");
		// 접속에 사용할 client를 셋팅해준다.

		// mav.setViewName("start");
		return mav;
	}

	@RequestMapping("/login")
	public ModelAndView login() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		return mav;
	}

	@RequestMapping(value = "/doLogin", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView doLogin(HttpSession session, @RequestParam("host") String host, @RequestParam("username") String username,
			@RequestParam("password") String password) {

		logger.debug("login {} : {}:{}", host, username, password);

		if (host == null || host.length() == 0 || username.length() == 0 || password.length() == 0) {
			ModelAndView mav = new ModelAndView();
			mav.setViewName("redirect:login.html");
			return mav;
		}

		// TODO 로그인되었다면 바로 start.html로 간다.

//		ResponseHttpClient httpClient = new ResponseHttpClient(host);
//		try {
//			JSONObject loginResult = httpClient.httpPost("/management/login").addParameter("id", username).addParameter("password", password)
//					.requestJSON();
//			logger.debug("loginResult > {}", loginResult);
//			if (loginResult != null && loginResult.getInt("status") == 0) {
//				// 로그인이 올바를 경우 메인 화면으로 이동한다.
//				ModelAndView mav = new ModelAndView();
//				mav.setViewName("redirect:main/start.html");
//
//				session.setAttribute("httpclient", httpClient);
//				return mav;
//			}
//		} catch (Exception e) {
//			logger.error("", e);
//		}

		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		return mav;

	}

	@RequestMapping("/main/logout")
	public ModelAndView logout() {

		// TODO 세션삭제를 처리한다.

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

	@RequestMapping("/main/search")
	public ModelAndView search() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("search");
		return mav;
	}

	@RequestMapping("/main/search/config")
	public ModelAndView searchConfig() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("searchConfig");
		return mav;
	}

	@RequestMapping("/main/settings")
	public ModelAndView settings() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("settings");
		return mav;
	}

	/**
	 * 검색엔진에 proxy로 호출해준다. &uri=/a/b/c&param1=1&param=2와 같이 파라미터를 전달받으면 재조합해서 uri로 호출한다. 
	 * Get,Post모두 가능. 
	 * */
	@RequestMapping("/main/request")
	@ResponseBody
	public String request(HttpServletRequest request) {

		String uri = request.getParameter("uri");
		
		//만약 ? 가 붙어있다면 제거한다.
		int parameterStart = uri.indexOf('?');
		if(parameterStart > 0){
			uri = uri.substring(0, parameterStart);
		}
		
//		ResponseHttpClient httpClient = (ResponseHttpClient) request.getSession().getAttribute("httpclient");
//
//		
//		AbstractMethod abstractMethod = null;
//		if (request.getMethod().equalsIgnoreCase("GET")) {
//			abstractMethod = httpClient.httpGet(uri);
//		}else if (request.getMethod().equalsIgnoreCase("POST")) {
//			abstractMethod = httpClient.httpPost(uri);
//		}else{
//			//error
//			logger.error("Unknown http method >> {}", request.getMethod());
//		}
		
		Enumeration<String> enumeration = request.getParameterNames();
//		while (enumeration.hasMoreElements()) {
//			String key = enumeration.nextElement();
//			String value = request.getParameter(key);
//			//uri파라미터를 제외한 모든 파라미터를 재전달한다.
//			if(!key.equals("uri")){
//				abstractMethod.addParameter(key, value);
//			}
//		}
		JSONObject result = null;
//		try {
//			result = abstractMethod.requestJSON();
//		} catch (Exception e) {
//			logger.error("", e);
//			return "";
//		}

		logger.debug("request result >> {}", result);

		if(result == null){
			return "";
		}else{
			return result.toString();
		}
	}

}
