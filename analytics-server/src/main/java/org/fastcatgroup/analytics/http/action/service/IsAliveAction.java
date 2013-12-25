package org.fastcatgroup.analytics.http.action.service;

import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.util.ResponseWriter;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

@ActionMapping("/service/isAlive")
public class IsAliveAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {
		writeHeader(response);
		response.setStatus(HttpResponseStatus.OK);
		ResponseWriter resultWriter = getDefaultResponseWriter(response.getWriter());
		resultWriter.object().key("status").value("ok").endObject();
		resultWriter.done();
	}

}
