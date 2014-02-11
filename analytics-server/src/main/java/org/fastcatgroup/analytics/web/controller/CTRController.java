package org.fastcatgroup.analytics.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/report/ctr")
public class CTRController extends AbstractController {

	@RequestMapping("/view")
	public ModelAndView view() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/view");
		return mav;
	}
	
	@RequestMapping("/keyword")
	public ModelAndView keyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/keyword");
		return mav;
	}
	
	@RequestMapping("/searchKeyword")
	public ModelAndView searchKeyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/searchKeyword");
		return mav;
	}
}
