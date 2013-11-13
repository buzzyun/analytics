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
	
	@RequestMapping("/searchKeyword")
	public ModelAndView searchKeyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/searchKeyword");
		return mav;
	}
	
	@RequestMapping("/searchKeywordRank")
	public ModelAndView searchKeywordRank() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/searchKeywordRank");
		return mav;
	}
	
	//검색횟수추이.
	@RequestMapping("/hitCount")
	public ModelAndView hitCount() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/hitCount");
		return mav;
	}
	
	//관심어추이.
	@RequestMapping("/myKeyword")
	public ModelAndView myKeyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/myKeyword");
		return mav;
	}
	
	//관심어순위
	@RequestMapping("/myKeywordRank")
	public ModelAndView myKeywordRank() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/myKeywordRank");
		return mav;
	}
	
	//응답시간추이
	@RequestMapping("/responseTime")
	public ModelAndView responseTime() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/responseTime");
		return mav;
	}
	
	//급상승검색어순위
	@RequestMapping("/hotKeywordRank")
	public ModelAndView hotKeywordRank() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/hotKeywordRank");
		return mav;
	}
	
	//유형별보기.
	@RequestMapping("/typeView")
	public ModelAndView typeView() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/typeView");
		return mav;
	}
	
	//ctr보기.
	@RequestMapping("/ctr")
	public ModelAndView ctr() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctr");
		return mav;
	}
	
	//ctr 키워드 보기.
	@RequestMapping("/ctrKeyword")
	public ModelAndView ctrKeyword() {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("report/ctrKeyword");
		return mav;
	}
}
