package org.fastcatsearch.analytics.http.action.service.keyword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.vo.RankKeyword;
import org.fastcatsearch.analytics.db.vo.RankKeywordVO.RankDiffType;
import org.fastcatsearch.analytics.http.ActionMapping;
import org.fastcatsearch.analytics.http.action.ActionRequest;
import org.fastcatsearch.analytics.http.action.ActionResponse;
import org.fastcatsearch.analytics.http.action.ServiceAction;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.ResponseWriter;

import static org.fastcatsearch.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

@ActionMapping("/service/keyword/popular")
public class GetServicePopularKeywordAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {
		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);

		writeHeader(response);
		ResponseWriter responseWriter = getDefaultResponseWriter(response.getWriter());
		responseWriter.object();

		String siteId = request.getParameter("siteId");
		String categoryId = request.getParameter("categoryId");
		String timeType = request.getParameter("timeType");
		String timeId = request.getParameter("timeId");
		int sn = request.getIntParameter("sn", 1);
		int ln = request.getIntParameter("ln", 0);
		int interval = request.getIntParameter("interval", 1);
		String errorMessage = null;

		Calendar calendar = StatisticsUtils.getCalendar();
		
		try {
			//String timeId = null;
			List<RankKeyword> list = null;
			if (interval != 0) {
				if ("D".equalsIgnoreCase(timeType)) {
					calendar.add(Calendar.DATE, -interval);
					timeId = StatisticsUtils.getTimeId(calendar, Calendar.DATE);
					list = statisticsService.getPopularKeywordList(siteId, categoryId, timeId);
				} else if ("W".equalsIgnoreCase(timeType)) {
					calendar.add(Calendar.WEEK_OF_YEAR, -interval);
					timeId = StatisticsUtils.getTimeId(calendar, Calendar.WEEK_OF_YEAR);
					list = statisticsService.getPopularKeywordList(siteId, categoryId, timeId);
				} else if ("M".equalsIgnoreCase(timeType)) {
					calendar.add(Calendar.MONTH, -interval);
					timeId = StatisticsUtils.getTimeId(calendar, Calendar.MONTH);
					list = statisticsService.getPopularKeywordList(siteId, categoryId, timeId);
				}
			} else {
				//TODO: interval 을 0으로 주고 date 를 직접 기재한 경우
				//파일기반에서 덤프 하도록 한다.
				//ln 이 0 인 경우 모조리 덤프 한다.
				
				File baseDir = environment.filePaths().getStatisticsRoot().file(siteId, "date");
				calendar = StatisticsUtils.parseTimeId(timeId);
				
				Calendar dailyCalendar = (Calendar) calendar.clone();
				File timeDir = StatisticsUtils.getDayDataDir(baseDir, dailyCalendar);
				File file = new File(new File(new File(timeDir, siteId), categoryId), POPULAR_FILENAME);
				BufferedReader reader = null;
				
				try {
					reader = new BufferedReader(new FileReader(file));
					list = new ArrayList<RankKeyword>();
					int rank = 1;
					for (String rline = null; (ln == 0 || rank <= (sn + ln))
							&& (rline = reader.readLine()) != null; rank++) {
						String [] data = rline.split("\t");
						if(rank >= sn)  {
							RankKeyword vo = new RankKeyword(data[0].trim(), rank, Integer.parseInt(data[3].trim()));
							vo.setRankDiffType(RankDiffType.valueOf(data[1].trim()));
							vo.setRankDiff(Integer.parseInt(data[2].trim()));
							vo.setCountDiff(Integer.parseInt(data[4].trim()));
							list.add(vo);
						}
					}
				} finally {
					if(reader!=null) try {
						reader.close();
					} catch (IOException ignore) { }
				}
			}
			
			responseWriter.key("siteId").value(siteId);
			responseWriter.key("categoryId").value(categoryId);
			responseWriter.key("timeType").value(timeType);
			responseWriter.key("time").value(timeType);
			
			
			responseWriter.key("list").array();
			if (list != null) {
				
				for (RankKeyword vo : list) {
					responseWriter.object();
					responseWriter.key("rank").value(vo.getRank());
					responseWriter.key("word").value(vo.getKeyword());
					responseWriter.key("diffType").value(vo.getRankDiffType().name());
					responseWriter.key("diff").value(vo.getRankDiff());
					responseWriter.key("count").value(vo.getCount());
					responseWriter.key("countDiff").value(vo.getCountDiff());
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
		}
	}
}
