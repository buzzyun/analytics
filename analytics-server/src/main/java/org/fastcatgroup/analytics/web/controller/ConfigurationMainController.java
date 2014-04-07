package org.fastcatgroup.analytics.web.controller;

import java.util.Calendar;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.job.Job;
import org.fastcatgroup.analytics.job.task.DailySearchLogAnalyticsTaskRunJob;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/configuration")
public class ConfigurationMainController extends AbstractController {

	@RequestMapping("/index")
	public ModelAndView index(@PathVariable String siteId) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("configuration/index");
		
		
		return mav;
	}
	
	@RequestMapping("/management/run")
	public ModelAndView run(@PathVariable String siteId, @RequestParam(required = false) String taskType, @RequestParam(required = false) String date) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("configuration/management/run");
		
		Calendar calendar = null;
		
		String timeId = "";
		if(date!=null && !"".equals(date)) {
			calendar = SearchStatisticsProperties.parseDatetimeString(date);
			timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.DAY_OF_MONTH);
			if("searchStatictics".equals(taskType)) {
				Job job = new DailySearchLogAnalyticsTaskRunJob(siteId, timeId);
				JobService.getInstance().offer(job);	
			} else if("relateKeyword".equals(taskType)) {
//				action = new RelateSearchLogAnalyticsTaskRunAction();
			} else if("realtimeKeyword".equals(taskType)) {
//				action = new RealtimeSearchLogAnalyticsTaskRunAction();
			}
		
		} else {
			calendar = SearchStatisticsProperties.getCalendar();
			calendar.add(Calendar.DATE, -1);
		}
		
		mav.addObject("date", SearchStatisticsProperties.toDatetimeString(calendar));
		return mav;
	}
}
