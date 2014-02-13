package org.fastcatgroup.analytics.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.service.ServiceManager;
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

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
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
