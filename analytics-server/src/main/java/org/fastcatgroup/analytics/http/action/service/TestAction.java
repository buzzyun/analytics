package org.fastcatgroup.analytics.http.action.service;

import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

@ActionMapping("/test")
public class TestAction extends ServiceAction {

	public String name;
	
	public TestAction() {
		name = "test";
	}

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {
		
		writeHeader(response);
		Object obj = session.getAttribute("test");
		logger.debug(getClass().getSimpleName()+" session="+obj);
		if(obj == null){
			session.setAttribute("test", "세션테스트");
		}
		String contentStr = "<html><body><h1>제목</h1><h3>sub title</h3></body></html>";
		try {
			response.getWriter().write(contentStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		response.setStatus(HttpResponseStatus.OK);
		
		
	}

}
