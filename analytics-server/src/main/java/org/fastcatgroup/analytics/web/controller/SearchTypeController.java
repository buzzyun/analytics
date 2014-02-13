package org.fastcatgroup.analytics.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report/type")
public class SearchTypeController extends AbstractController {

	//카테고리별 비율보기.
	@RequestMapping("/viewCategory")
	public ModelAndView viewCategory(@PathVariable String siteId) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/type/viewCategory");
		return mav;
	}
	
	//서비스별 비율보기.
	@RequestMapping("/viewService")
	public ModelAndView viewService(@PathVariable String siteId) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/type/viewService");
		return mav;
	}
	
	
}
