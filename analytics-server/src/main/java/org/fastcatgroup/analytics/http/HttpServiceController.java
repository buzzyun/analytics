package org.fastcatgroup.analytics.http;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.HttpAction;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.http.action.ServiceAction.Type;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServiceController {
	private static final Logger logger = LoggerFactory.getLogger(HttpServiceController.class);
	private ExecutorService executorService;
	private Map<String, ServiceAction> actionMap;
	private HttpSessionManager httpSessionManager;
	
	public HttpServiceController(ExecutorService executorService, HttpSessionManager httpSessionManager) {
		this.executorService = executorService;
		this.httpSessionManager = httpSessionManager;
	}

	public void dispatchRequest(HttpRequest request, HttpChannel httpChannel) {
		// request에서 uri를 보고 외부요청을 확인하여 channel에 최종 결과를 write한다.
		HttpAction action = createAction(request, httpChannel);
		if (action != null) {
			try {
				executorService.execute(action);
			} catch (Exception e) {
				logger.error("Action job allocation error!", e);
			}
		}else{
			httpChannel.sendError(HttpResponseStatus.NOT_FOUND, null);
		}
	}

	private HttpAction createAction(HttpRequest request, HttpChannel httpChannel) {
		String uri = request.getUri();
		
		logger.debug("URI : {}, method={}, version={}", uri, request.getMethod(), request.getProtocolVersion());
		// uri의 파라미터 제거
		int pos = uri.indexOf("?");
		if (pos > 0) {
			uri = uri.substring(0, pos);
		}
		String standardURI = null; //.json등의 접미사가 없는 uri
		ServiceAction.Type contenType = null;
		if(uri.endsWith(".json")){
			standardURI = uri.substring(0, uri.length() - 5);
			contenType = Type.json;
		}else if(uri.endsWith(".xml")){ 
			standardURI = uri.substring(0, uri.length() - 4);
			contenType = Type.xml;
		}else if(uri.endsWith(".jsonp")){ 
			standardURI = uri.substring(0, uri.length() - 6);
			contenType = Type.jsonp;
		}else if(uri.endsWith(".html")){ 
			standardURI = uri.substring(0, uri.length() - 5);
			contenType = Type.html;
		}else{
			standardURI = uri;
			contenType = Type.json;
		}
		
		ServiceAction actionObj = actionMap.get(standardURI);
		if(actionObj == null) {
			return null;
		}
		String method = request.getMethod().getName().toUpperCase();
		ActionMethod actionMethod = ActionMethod.valueOf(method);
		if(!actionObj.isMethod(actionMethod)){
			//허용 method가 아니면 null
			return null;
		}
		ActionResponse actionResponse = new ActionResponse();
		HttpSession httpSession = httpSessionManager.handleCookie(request, actionResponse);
		
		ServiceAction action = actionObj.clone();
		action.init(contenType, new ActionRequest(uri, request), actionResponse, httpSession, httpChannel);
		return action;
	}

	public void setActionMap(Map<String, ServiceAction> actionMap) {
		this.actionMap = actionMap;
	}
}
