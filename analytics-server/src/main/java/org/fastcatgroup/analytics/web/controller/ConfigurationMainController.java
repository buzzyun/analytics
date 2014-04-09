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
		
		Calendar calendar1 = null;
		Calendar calendar2 = null;
		
		String timeId1 = "";
		String timeId2 = "";
		
		if(date!=null && !"".equals(date)) {
			
			String[] dateArr = date.split(" - ");
			
			
			calendar1 = SearchStatisticsProperties.parseDatetimeString(dateArr[0], true);
			
			if(dateArr.length > 0) {
				calendar2 = SearchStatisticsProperties.parseDatetimeString(dateArr[1], false);
			} else {
				calendar2 = calendar1;
			}
			timeId1 = SearchStatisticsProperties.getTimeId(calendar1, Calendar.DAY_OF_MONTH);
			timeId2 = SearchStatisticsProperties.getTimeId(calendar2, Calendar.DAY_OF_MONTH);
			if("searchStatictics".equals(taskType)) {
				Job job = new DailySearchLogAnalyticsTaskRunJob(siteId, timeId1, timeId2);
				JobService.getInstance().offer(job);	
			} else if("relateKeyword".equals(taskType)) {
//				action = new RelateSearchLogAnalyticsTaskRunAction();
			} else if("realtimeKeyword".equals(taskType)) {
//				action = new RealtimeSearchLogAnalyticsTaskRunAction();
			}
		
		} else {
			calendar1 = SearchStatisticsProperties.getCalendar();
			calendar1.add(Calendar.DATE, -1);
		}
		
		mav.addObject("date", SearchStatisticsProperties.toDatetimeString(calendar1));
		return mav;
	}
}
