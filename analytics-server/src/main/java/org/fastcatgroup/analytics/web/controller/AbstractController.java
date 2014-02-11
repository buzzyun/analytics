package org.fastcatgroup.analytics.web.controller;

import java.util.List;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

public class AbstractController {
	
	protected static Logger logger = LoggerFactory.getLogger(AbstractController.class);
	
	/*
	 * exception페이지로 이동한다.
	 * */
	@ExceptionHandler(Throwable.class)
	public ModelAndView handleAllException(Exception ex) {
 
		ModelAndView model = new ModelAndView("error");
		model.addObject("exception", ex);
		return model;
 
	}
	
	protected StatisticsService getStatisticsService(){
		return ServiceManager.getInstance().getService(StatisticsService.class);
	}
	protected List<SiteCategoryConfig> getSiteCategoryListConfig(){
		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		SiteCategoryListConfig config = statisticsService.getSiteCategoryListConfig();
		return config.getList();
	}
	
	
	
}
