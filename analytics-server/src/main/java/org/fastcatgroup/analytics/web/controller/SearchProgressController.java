package org.fastcatgroup.analytics.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/report/progress")
public class SearchProgressController extends AbstractController {

	@RequestMapping("/searchKeyword")
	public ModelAndView searchKeyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/searchKeyword");
		return mav;
	}
	
	//관심어추이.
	@RequestMapping("/myKeyword")
	public ModelAndView myKeyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/myKeyword");
		return mav;
	}
		
	//검색횟수추이.
	@RequestMapping("/hitCount")
	public ModelAndView hitCount() {

		
		
		//TODO
		
		//mapper가져와서
		
		
		//select해서 보낸다.
		
		
		
		
		
		
		
		
		
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/hitCount");
		return mav;
	}
	
	
	
	//응답시간추이
	@RequestMapping("/responseTime")
	public ModelAndView responseTime() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/progress/responseTime");
		return mav;
	}
	

		

}
