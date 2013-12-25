package org.fastcatgroup.analytics.web.controller;

import javax.servlet.http.HttpSession;

import org.fastcatgroup.analytics.web.http.ResponseHttpClient;
import org.fastcatgroup.analytics.web.http.ResponseHttpClient.GetMethod;
import org.fastcatgroup.analytics.web.http.ResponseHttpClient.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

public class AbstractController {
	
	protected static Logger logger = LoggerFactory.getLogger(AbstractController.class);
	
	protected static final String HTTPCLIENT_ID = "httpclient";
	protected static final String USERNAME_ID = "_username";
	/*
	 * exception페이지로 이동한다.
	 * */
	@ExceptionHandler(Throwable.class)
	public ModelAndView handleAllException(Exception ex) {
 
		ModelAndView model = new ModelAndView("error");
		model.addObject("exception", ex);
		return model;
 
	}
	
	protected PostMethod httpPost(HttpSession session, String uri) {
		ResponseHttpClient httpClient = (ResponseHttpClient) session.getAttribute(HTTPCLIENT_ID);
		return httpClient.httpPost(uri);
	}
	
	protected GetMethod httpGet(HttpSession session, String uri) {
		ResponseHttpClient httpClient = (ResponseHttpClient) session.getAttribute(HTTPCLIENT_ID);
		return httpClient.httpGet(uri);
	}
}
