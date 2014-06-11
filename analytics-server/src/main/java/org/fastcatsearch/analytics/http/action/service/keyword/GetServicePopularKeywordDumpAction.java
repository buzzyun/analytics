package org.fastcatsearch.analytics.http.action.service.keyword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;

import org.fastcatsearch.analytics.analysis.StatisticsProperties;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.calculator.DumpPopularKeywordHitCalculator;
import org.fastcatsearch.analytics.http.ActionMapping;
import org.fastcatsearch.analytics.http.action.ActionRequest;
import org.fastcatsearch.analytics.http.action.ActionResponse;
import org.fastcatsearch.analytics.http.action.ServiceAction;
import org.fastcatsearch.analytics.util.ResponseWriter;

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
		int sn = request.getIntParameter("sn", 1);
		int ln = request.getIntParameter("ln", 0);
		String errorMessage = null;
		File targetFile = null;
		File tmpFile = null;
		BufferedReader reader = null;
		try {
			
			Calendar fromDate = StatisticsUtils.parseDatetimeString(fromDateStr, true);
			Calendar toDate = StatisticsUtils.parseDatetimeString(toDateStr, false);
			
			tmpFile = File.createTempFile(this.getClass().getName(), ".tmp1");
			targetFile = File.createTempFile(this.getClass().getName(), ".tmp2");
			File baseDir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
			DumpPopularKeywordHitCalculator calculator = 
				new DumpPopularKeywordHitCalculator(baseDir, fromDate, toDate, tmpFile, targetFile, siteId, Arrays.asList(new String[]{ categoryId }));
					
			calculator.init(null);
			calculator.calculate();
			
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(targetFile), StatisticsProperties.encoding));
			
			responseWriter.key("siteId").value(siteId);
			responseWriter.key("categoryId").value(categoryId);
			
			responseWriter.key("list").array();

			int rank = 1;
			for (String rline = null; (ln == 0 || rank <= (sn + ln))
					&& (rline = reader.readLine()) != null; rank++) {
				String [] data = rline.split("\t");
				if(rank >= sn)  {
					responseWriter.object();
					responseWriter.key("rank").value(rank);
					responseWriter.key("word").value(data[0].trim());
					responseWriter.key("count").value(data[1].trim());
					responseWriter.endObject();
				}
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
