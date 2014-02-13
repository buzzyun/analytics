package org.fastcatgroup.analytics.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report/ctr")
public class CTRController extends AbstractController {

	@RequestMapping("/view")
	public ModelAndView view(@PathVariable String siteId) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/view");
		return mav;
	}
	
	@RequestMapping("/keyword")
	public ModelAndView keyword(@PathVariable String siteId) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/keyword");
		return mav;
	}
	
	@RequestMapping("/searchKeyword")
	public ModelAndView searchKeyword(@PathVariable String siteId) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr/searchKeyword");
		return mav;
	}
}
