package org.fastcatgroup.analytics.http.action;

import org.fastcatgroup.analytics.http.ActionMethod;
import org.fastcatgroup.analytics.http.HttpChannel;
import org.fastcatgroup.analytics.http.HttpSession;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpAction implements Runnable, Cloneable {
	protected static final Logger logger = LoggerFactory.getLogger(HttpAction.class);
	private ActionMethod[] method; //허용 http 메소드.
	
	protected ActionRequest request;
	protected HttpChannel httpChannel;
	protected ActionResponse response;
	protected HttpSession session;
	
	
	public HttpAction(){
	}
	
	public void init(ActionRequest request, ActionResponse response, HttpSession session, HttpChannel httpChannel){
		this.request = request;
		this.response = response;
		this.session = session;
		this.httpChannel = httpChannel;
		response.init();
		
	}
	
	abstract public void doAction(ActionRequest request, ActionResponse response) throws Exception;
		
	@Override
	public void run() {
		
		try {
			doAction(request, response);
			httpChannel.sendResponse(response);
		} catch (Throwable e) {
			logger.error("Action수행중 에러발생.", e);
			httpChannel.sendError(HttpResponseStatus.INTERNAL_SERVER_ERROR, e);
		}
		
	}

	public boolean isMethod(ActionMethod actionMethod){
		for(ActionMethod m : this.method){
			if(m == actionMethod){
				return true;
			}
		}
		return false;
	}
	
	public void setMethod(ActionMethod[] method) {
		this.method = method;
	}
	
}
