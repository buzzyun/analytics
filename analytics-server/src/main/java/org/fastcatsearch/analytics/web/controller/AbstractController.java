package org.fastcatsearch.analytics.web.controller;

import java.io.Writer;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.http.action.ServiceAction.Type;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.JSONPResponseWriter;
import org.fastcatsearch.analytics.util.JSONResponseWriter;
import org.fastcatsearch.analytics.util.ResponseWriter;
import org.fastcatsearch.analytics.util.XMLResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AbstractController {
	public static final String DEFAULT_ROOT_ELEMENT = "response";
	public static final String DEFAULT_CHARSET = "utf-8";
	public static final String ENVIRONMENT = "ENVIRONMENT";
	
	protected Type resultType = Type.json;
	protected static Environment environment;
	private ServletContext context;
	
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

	@Autowired
	public void setSpringTestService(ServletContext context) {
		this.context = context;
	}

	@PostConstruct
	protected void init() {
		if(environment == null) {
			environment = (Environment)context.getAttribute(ENVIRONMENT);
			logger.info("environment : {}", environment);
		}
	}
	
	protected StatisticsService getStatisticsService(){
		return ServiceManager.getInstance().getService(StatisticsService.class);
	}
	
	protected List<SiteSetting> getSiteCategoryListConfig(){
		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		SiteListSetting config = statisticsService.getSiteListSetting();
		return config.getSiteList();
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
		logger.trace("resultType > {}", resultType);
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
	
	public Integer getInt(String value, Integer defaultValue) {
		try {
			return Integer.parseInt(value.trim());
		} catch (NullPointerException ignore) {
		} catch (NumberFormatException ignore) {
		}
		return defaultValue;
	}
	
	public String getString(Object obj, String defaultValue) {
		try {
			if(obj!=null) {
				return String.valueOf(obj);
			}
		} catch (NullPointerException ignore) {
		}
		return defaultValue;
	}
	
	public Boolean getBoolean(String bool, Boolean defaultValue) {
		try {
			return Boolean.valueOf(bool);
		} catch (NullPointerException ignore) {
		}
		return defaultValue;
	}
}
