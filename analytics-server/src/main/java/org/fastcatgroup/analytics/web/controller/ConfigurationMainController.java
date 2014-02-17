package org.fastcatgroup.analytics.web.controller;

import java.util.Calendar;
import java.util.Map;

import org.fastcatgroup.analytics.analysis.SearchStatisticsProperties;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.http.action.ServiceAction.Type;
import org.fastcatgroup.analytics.http.action.service.management.DailySearchLogAnalyticsTaskRunAction;
import org.fastcatgroup.analytics.http.action.service.management.RealtimeSearchLogAnalyticsTaskRunAction;
import org.fastcatgroup.analytics.http.action.service.management.RelateSearchLogAnalyticsTaskRunAction;
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
	public ModelAndView run(@PathVariable String siteId, @RequestParam(required = false) String taskType, @RequestParam(required = false) String timeId) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("configuration/management/run");
		
		ActionRequest request = new ActionRequest(null, null);
		ActionResponse response = new ActionResponse();
		response.init();
		Map<String, String> parameterMap = request.prepareParameterMap();
		parameterMap.put("siteId", siteId);
		parameterMap.put("timeId", timeId);
		
		ServiceAction action = null;
		
		if("searchStatictics".equals(taskType)) {
			action = new DailySearchLogAnalyticsTaskRunAction();
		} else if("relateKeyword".equals(taskType)) {
			action = new RelateSearchLogAnalyticsTaskRunAction();
		} else if("realtimeKeyword".equals(taskType)) {
			action = new RealtimeSearchLogAnalyticsTaskRunAction();
		}
		
		if(action != null){
			action.init(Type.json, request, response, null, null);
			action.doAction(request, response);
			logger.debug("task done..");
		}
		
		if(timeId == null){
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			timeId = SearchStatisticsProperties.getTimeId(calendar, Calendar.DATE);
		}
		mav.addObject("timeId", timeId);
		return mav;
	}
}
