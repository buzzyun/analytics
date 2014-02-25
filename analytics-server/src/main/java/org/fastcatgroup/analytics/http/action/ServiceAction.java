package org.fastcatgroup.analytics.http.action;

import java.io.Writer;

import org.fastcatgroup.analytics.util.JSONPResponseWriter;
import org.fastcatgroup.analytics.util.JSONResponseWriter;
import org.fastcatgroup.analytics.util.ResponseWriter;
import org.fastcatgroup.analytics.util.XMLResponseWriter;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public abstract class ServiceAction extends HttpAction {
	public static final String DEFAULT_ROOT_ELEMENT = "response";
	public static final String DEFAULT_CHARSET = "utf-8";

	public static enum Type {
		json, xml, jsonp, html, text
	};

	public ServiceAction() {
	}

	abstract public void doAction(ActionRequest request, ActionResponse response) throws Exception;

	@Override
	public void runAction(ActionRequest request, ActionResponse response) throws Exception {
		try {
			doAction(request, response);
		} catch (Throwable t) {
			logger.error("ServiceAction Error!", t);
			writeHeader(response);
			Writer writer = response.getWriter();
			try {
				ResponseWriter resultWriter = getDefaultResponseWriter(writer);
				resultWriter.object().key("success").value(false).key("errorMessage").value(t.toString()).endObject();
				resultWriter.done();
			} finally {
				writer.close();
			}
		}
	}

	protected void writeHeader(ActionResponse response) {
		writeHeader(response, DEFAULT_CHARSET);
	}

	protected void writeHeader(ActionResponse response, String responseCharset) {
		response.setStatus(HttpResponseStatus.OK);
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
}
