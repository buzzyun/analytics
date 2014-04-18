package org.fastcatgroup.analytics.http.action.service.keyword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.analysis.calculator.DumpPopularKeywordHitCalculator;
import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping("/service/keyword/popular/dump")
public class GetServicePopularKeywordDumpAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {

		writeHeader(response);
		ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());
		responseWriter.object();

		String siteId = request.getParameter("siteId");
		String categoryId = request.getParameter("categoryId");
		String fromDateStr = request.getParameter("from");
		String toDateStr = request.getParameter("to");
		int ln = request.getIntParameter("ln", 100);
		String errorMessage = null;
		File targetFile = null;
		File tmpFile = null;
		BufferedReader reader = null;
		try {
			
			Calendar fromDate = SearchStatisticsProperties.parseDatetimeString(fromDateStr, true);
			Calendar toDate = SearchStatisticsProperties.parseDatetimeString(toDateStr, false);
			
			tmpFile = File.createTempFile(this.getClass().getName(), ".tmp1");
			targetFile = File.createTempFile(this.getClass().getName(), ".tmp2");
			File baseDir = environment.filePaths().getStatisticsRoot().file("search", "date");
			DumpPopularKeywordHitCalculator calculator = 
				new DumpPopularKeywordHitCalculator(baseDir, fromDate, toDate, tmpFile, targetFile, siteId, Arrays.asList(new String[]{ categoryId }));
					
			calculator.init();
			calculator.calculate();
			
			reader = new BufferedReader(new FileReader(targetFile));
			
			responseWriter.key("siteId").value(siteId);
			responseWriter.key("categoryId").value(categoryId);
			
			responseWriter.key("list").array();

			int rank = 1;
			for (String rline = null; (ln==0 || rank <= ln) && (rline = reader.readLine()) != null; rank++) {
				String [] data = rline.split("\t");
				responseWriter.object();
				responseWriter.key("rank").value(rank);
				responseWriter.key("word").value(data[0].trim());
				responseWriter.key("count").value(data[1].trim());
				responseWriter.endObject();
			}
			responseWriter.endArray();

		} catch (Exception e) {
			errorMessage = e.getMessage();
		} finally {
			if (errorMessage != null) {
				responseWriter.key("errorMessage").value(errorMessage);
			}
			responseWriter.endObject();
			responseWriter.done();
			
			if(reader!=null) try {
				reader.close();
			} catch (IOException ignore) { }
			
			if(tmpFile!=null && tmpFile.exists()) {
				targetFile.delete();
			}
			
			if(targetFile!=null && targetFile.exists()) {
				targetFile.delete();
			}
		}
	}
}
