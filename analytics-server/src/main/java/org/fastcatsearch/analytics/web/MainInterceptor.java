package org.fastcatsearch.analytics.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.web.controller.MainController;
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
		
		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		
		String uri = request.getRequestURI();
		if(uri.contains("/report/")){
			modelAndView.addObject("_menuType", "report");
		}else if(uri.contains("/configuration/")){
			modelAndView.addObject("_menuType", "configuration");
		}
		
		Map<String, Object> pathVariables = (Map<String, Object>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String currentSiteId = (String) pathVariables.get("siteId");
		
		//if(siteList!=null) {
		//	if(statisticsService.getSiteListSetting().getSiteList().size() != siteList.size()) {
		//		siteList = null;
		//	}
		//}

		//if (siteList == null) {
			synchronized (this) {
				siteList = new ArrayList<String[]>();
				List<SiteSetting> list = statisticsService.getSiteListSetting().getSiteList();
				for (SiteSetting siteCategoryConfig : list) {
					String siteId = siteCategoryConfig.getId();
					String siteName = siteCategoryConfig.getName();
					siteList.add(new String[] { siteId, siteName });
				}
			}
		//}
		
		logger.trace("siteList:{}", siteList);
		
		modelAndView.addObject("_siteList", siteList);

		if (currentSiteId != null) {
			for (String[] el : siteList) {
				if (el[0].equals(currentSiteId)) {
					modelAndView.addObject("_siteName", el[1]);
				}
			}
		}
	}
}
