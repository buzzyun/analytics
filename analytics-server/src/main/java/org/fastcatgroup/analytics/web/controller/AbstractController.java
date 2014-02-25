package org.fastcatgroup.analytics.web.controller;

import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.http.action.ServiceAction.Type;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.JSONPResponseWriter;
import org.fastcatgroup.analytics.util.JSONResponseWriter;
import org.fastcatgroup.analytics.util.ResponseWriter;
import org.fastcatgroup.analytics.util.XMLResponseWriter;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

public class AbstractController {
	public static final String DEFAULT_ROOT_ELEMENT = "response";
	public static final String DEFAULT_CHARSET = "utf-8";
	
	protected Type resultType = Type.json;
	
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
	
	protected ResponseWriter getDefaultResponseWriter(Writer writer) {
		return getResponseWriter(writer, DEFAULT_ROOT_ELEMENT, true, null);
	}
	
	protected ResponseWriter getResponseWriter(Writer writer, String rootElement, boolean isBeautify, String jsonCallback) {
		ResponseWriter resultWriter = null;
		if (resultType == Type.json) {
			resultWriter = new JSONResponseWriter(writer, isBeautify);
		} else if (resultType == Type.jsonp) {
			resultWriter = new JSONPResponseWriter(writer, jsonCallback, isBeautify);
		} else if (resultType == Type.xml) {
			resultWriter = new XMLResponseWriter(writer, rootElement, isBeautify);
		}
		return resultWriter;
	}
	
	protected void writeHeader(HttpServletResponse response, String responseCharset) {
//		logger.debug("resultType > {}", resultType);
		if (resultType == Type.json) {
			response.setContentType("application/json; charset=" + responseCharset);
		} else if (resultType == Type.jsonp) {
			response.setContentType("application/json; charset=" + responseCharset);
		} else if (resultType == Type.xml) {
			response.setContentType("text/xml; charset=" + responseCharset);
		} else if (resultType == Type.html) {
			response.setContentType("text/html; charset=" + responseCharset);
		} else if (resultType == Type.text) {
			response.setContentType("text/plain; charset=" + responseCharset);
		} else {
			response.setContentType("application/json; charset=" + responseCharset);
		}
	}
}
