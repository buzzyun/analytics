package org.fastcatgroup.analytics.web.controller;

import org.fastcatgroup.analytics.service.ServiceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/report")
public class ReportMainController extends AbstractController {

	@RequestMapping("/index")
	public ModelAndView index() {
//		JobService jobService = ServiceManager.getInstance().getService(JobService.class);
		logger.debug("ServiceManager111222 > {}", ServiceManager.getInstance());
		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/index");
		return mav;
	}
	
	@RequestMapping("/dashboard")
	public ModelAndView dashboard() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/dashboard");
		return mav;
	}
	

}
