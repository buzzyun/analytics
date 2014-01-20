package org.fastcatgroup.analytics.http.action.test;

import java.io.Writer;

import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.job.analysis.MakeRealtimePopularKeywordJob;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping("/test/run-make-rt-popular-keyword")
public class RunMakeRealTimePopularKeywordAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {
		MakeRealtimePopularKeywordJob job = new MakeRealtimePopularKeywordJob();
		job.setEnvironment(environment);
		job.doRun();
		
		Writer writer = response.getWriter();
		
		writeHeader(response);
		
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		responseWriter.object()
		.key("success").value(true)
		.endObject();
		responseWriter.done();
	}

}
