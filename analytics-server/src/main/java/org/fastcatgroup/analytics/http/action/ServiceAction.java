package org.fastcatgroup.analytics.http.action;

import java.io.Writer;

import org.fastcatgroup.analytics.http.HttpChannel;
import org.fastcatgroup.analytics.http.HttpSession;
import org.fastcatgroup.analytics.util.JSONResponseWriter;
import org.fastcatgroup.analytics.util.ResponseWriter;
import org.fastcatgroup.analytics.util.XMLResponseWriter;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public abstract class ServiceAction extends HttpAction {
	public static final String DEFAULT_ROOT_ELEMENT = "response";
	public static final String DEFAULT_CHARSET = "utf-8";
	public static enum Type { json, xml, jsonp, html };
	protected Type resultType;
	
	public ServiceAction(){ 
	}
	
	public void init(Type resultType, ActionRequest request, ActionResponse response, HttpSession session, HttpChannel httpChannel){
		this.resultType = resultType;
		super.init(request, response, session, httpChannel);
		
	}
	
	public ServiceAction clone(){
		ServiceAction action;
		try {
			action = (ServiceAction) super.clone();
			action.request = null;
			action.httpChannel = null;
			action.response = null;
			return action;
		} catch (CloneNotSupportedException e) {
			logger.error("Clone error", e);
		}
		return null;
	}
	
	protected void writeHeader(ActionResponse response) {
		writeHeader(response, DEFAULT_CHARSET);
	}
	protected void writeHeader(ActionResponse response, String responseCharset) {
		response.setStatus(HttpResponseStatus.OK);
		logger.debug("resultType > {}",resultType);
		if (resultType == Type.json) {
			response.setContentType("application/json; charset=" + responseCharset);
		} else if (resultType == Type.jsonp) {
			response.setContentType("application/json; charset=" + responseCharset);
		} else if (resultType == Type.xml) {
			response.setContentType("text/xml; charset=" + responseCharset);
		} else if (resultType == Type.html) {
			response.setContentType("text/html; charset=" + responseCharset);
		} else {
			response.setContentType("application/json; charset=" + responseCharset);
		}
	}
	protected ResponseWriter getDefaultResponseWriter(Writer writer){
		return getResponseWriter(writer, DEFAULT_ROOT_ELEMENT, true, null);
	}
	
	protected ResponseWriter getResponseWriter(Writer writer, String rootElement, boolean isBeautify, String jsonCallback) {
		ResponseWriter resultWriter = null;
		if (resultType == Type.json) {
			resultWriter = new JSONResponseWriter(writer, isBeautify);
		} else if (resultType == Type.xml) {
			resultWriter = new XMLResponseWriter(writer, rootElement, isBeautify);
		}
		return resultWriter;
	}

}
