package org.fastcatgroup.analytics.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.CategoryConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.vo.RelateKeywordVO;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.http.action.ServiceAction.Type;
import org.fastcatgroup.analytics.http.action.service.management.RelateSearchLogAnalyticsTaskRunAction;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
			
			action = new RelateSearchLogAnalyticsTaskRunAction();
			action.init(Type.json, request, response, null, null);
			action.doAction(request, response);
			
			logger.debug("task done..");
			
		} else if("relateKeyword".equals(taskType)) {
			
		} else if("realtimeKeyword".equals(taskType)) {
			
		} else {
			
		}
		
		
		return mav;
	}
}
