package org.fastcatgroup.analytics.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.web.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class MainInterceptor extends HandlerInterceptorAdapter {

	protected static Logger logger = LoggerFactory.getLogger(MainInterceptor.class);

	private static List<String[]> siteList;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		HttpSession session = request.getSession();
		
		if(session.getAttribute(MainController.USER_ID) == null) {
			checkLoginRedirect(request, response);
			return false;
		}
		return true;
	}

	private void checkLoginRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String loginURL = request.getContextPath() + "/login.html";
		String method = request.getMethod();
		if(method.equalsIgnoreCase("GET")){
			String target = request.getRequestURL().toString();
			String queryString = request.getQueryString();
			if(queryString != null && queryString.length() > 0){
				target += ("?" + queryString);
			}
			loginURL += ( "?redirect=" + target);
			logger.debug("REDIRECT >> {}, target = {}", method, target);
			logger.debug("RedirectURL >> {}", loginURL);
		}
		
		response.sendRedirect(loginURL);
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
		String uri = request.getRequestURI();
		if(uri.contains("/report/")){
			modelAndView.addObject("_menuType", "report");
		}else if(uri.contains("/configuration/")){
			modelAndView.addObject("_menuType", "configuration");
		}
		
		Map<String, Object> pathVariables = (Map<String, Object>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String currentSiteId = (String) pathVariables.get("siteId");

		if (siteList == null) {
			synchronized (this) {
				siteList = new ArrayList<String[]>();
				StatisticsService s = ServiceManager.getInstance().getService(StatisticsService.class);
				SiteCategoryListConfig siteCategoryListConfig = s.getSiteCategoryListConfig();
				List<SiteCategoryConfig> list = siteCategoryListConfig.getList();
				for (SiteCategoryConfig siteCategoryConfig : list) {
					String siteId = siteCategoryConfig.getSiteId();
					String siteName = siteCategoryConfig.getSiteName();
					siteList.add(new String[] { siteId, siteName });
				}
			}
		}
		
//		logger.debug("siteList:{}", siteList);
		
		modelAndView.addObject("_siteList", siteList);

		if (currentSiteId != null) {
			for (String[] el : siteList) {
				if (el[0].equals(currentSiteId)) {
					modelAndView.addObject("_siteName", el[1]);
				}
			}
		}
	}
	//
	// @Override
	// public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	// }
}
