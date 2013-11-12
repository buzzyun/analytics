package org.fastcatgroup.analytics.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/report/")
public class ReportController {
	private static Logger logger = LoggerFactory.getLogger(ReportController.class);

	@RequestMapping("/index")
	public ModelAndView index() {
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
