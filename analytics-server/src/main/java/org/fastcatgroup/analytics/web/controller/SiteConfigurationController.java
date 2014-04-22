package org.fastcatgroup.analytics.web.controller;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.fastcatgroup.analytics.analysis.StatisticsUtils;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.ClickTypeSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.PopularKeywordSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.RealTimePopularKeywordSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.RelateKeywordSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.ServiceSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.job.Job;
import org.fastcatgroup.analytics.job.task.DailySearchLogAnalyticsTaskRunJob;
import org.fastcatgroup.analytics.job.task.TestBulkLogAnalyticsTaskRunJob;
import org.fastcatgroup.analytics.util.ResponseWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/configuration")
public class SiteConfigurationController extends AbstractController {

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

		if (date != null && !"".equals(date)) {

			String[] dateArr = date.split(" - ");

			calendar1 = StatisticsUtils.parseDatetimeString(dateArr[0], true);

			if (dateArr.length > 1) {
				calendar2 = StatisticsUtils.parseDatetimeString(dateArr[1], false);
			} else {
				calendar2 = calendar1;
			}
			timeId1 = StatisticsUtils.getTimeId(calendar1, Calendar.DAY_OF_MONTH);
			timeId2 = StatisticsUtils.getTimeId(calendar2, Calendar.DAY_OF_MONTH);
			if ("searchStatictics".equals(taskType)) {
				Job job = new DailySearchLogAnalyticsTaskRunJob(siteId, timeId1, timeId2);
				JobService.getInstance().offer(job);
			} else if ("relateKeyword".equals(taskType)) {
				// action = new RelateSearchLogAnalyticsTaskRunAction();
			} else if ("realtimeKeyword".equals(taskType)) {
				// action = new RealtimeSearchLogAnalyticsTaskRunAction();
			}

		} else {
			calendar1 = StatisticsUtils.getCalendar();
			calendar1.add(Calendar.DATE, -1);
		}

		mav.addObject("date", StatisticsUtils.toDatetimeString(calendar1));
		return mav;
	}

	@RequestMapping("/management/advanceRun")
	public ModelAndView runTestRange(@PathVariable String siteId, @RequestParam(required = false) String date) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("configuration/management/advanceRun");

		Calendar calendar1 = null;
		Calendar calendar2 = null;

		String timeId1 = "";
		String timeId2 = "";

		if (date != null && !"".equals(date)) {

			String[] dateArr = date.split("-");
			if (dateArr.length == 2) {
				for (int i = 0; i < dateArr.length ;i++) {
					dateArr[i] = dateArr[i].trim();
				}

				calendar1 = StatisticsUtils.parseDatetimeString(dateArr[0], true);
				calendar2 = StatisticsUtils.parseDatetimeString(dateArr[1], false);
				
				timeId1 = StatisticsUtils.getTimeId(calendar1, Calendar.DAY_OF_MONTH);
				timeId2 = StatisticsUtils.getTimeId(calendar2, Calendar.DAY_OF_MONTH);
				
				Job job = new TestBulkLogAnalyticsTaskRunJob(siteId, timeId1, timeId2);
				JobService.getInstance().offer(job);
			}
			mav.addObject("date", date);
		}else{
			Calendar now = StatisticsUtils.getCalendar();
			String timeString = StatisticsUtils.toDatetimeString(now, Calendar.DAY_OF_MONTH);
			mav.addObject("date", timeString + " - " + timeString);
		}
		return mav;
	}
	
	@RequestMapping("/settings/categorySetting")
	public ModelAndView categorySetting(@PathVariable String siteId) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("configuration/settings/categorySetting");
		StatisticsSettings statisticsSetting = getStatisticsService().getStatisticsSetting(siteId);
		List<CategorySetting> categoryList = statisticsSetting.getCategoryList();
		if(categoryList == null) { 
			categoryList = new ArrayList<CategorySetting>();
		}
		mav.addObject("categoryList", categoryList);
		return mav;
	}
	
	@RequestMapping("/settings/siteSetting")
	public ModelAndView keywordSetting(@PathVariable String siteId) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("configuration/settings/siteSetting");
		StatisticsSettings statisticsSetting = getStatisticsService().getStatisticsSetting(siteId);
		String banwords = statisticsSetting.getSiteProperties().getBanwords();
		PopularKeywordSetting popularKeywordSetting = statisticsSetting.getPopularKeywordSetting();
		RelateKeywordSetting relateKeywordSetting = statisticsSetting.getRelateKeywordSetting();
		RealTimePopularKeywordSetting realTimePopularKeywordSetting = statisticsSetting.getRealtimePopularKeywordSetting();
		
		int defaultMinimumHitCount = 2;
		int defaultTopCount = 10;
		int defaultRealtimeRecentCount = 6;
		if(banwords==null) { banwords = ""; }
		if(popularKeywordSetting==null) { popularKeywordSetting = new PopularKeywordSetting(defaultTopCount, defaultMinimumHitCount); }
		if(relateKeywordSetting==null) { relateKeywordSetting = new RelateKeywordSetting(defaultMinimumHitCount); }
		if(realTimePopularKeywordSetting==null) { realTimePopularKeywordSetting = new RealTimePopularKeywordSetting(defaultRealtimeRecentCount, defaultTopCount, defaultMinimumHitCount); }
		
		mav.addObject("banWords", banwords);
		mav.addObject("popularKeywordSetting",popularKeywordSetting);
		mav.addObject("relateKeywordSetting",relateKeywordSetting);
		mav.addObject("realTimePopularKeywordSetting",realTimePopularKeywordSetting);
		return mav;
	}
	
	@RequestMapping("/settings/attributeSetting")
	public ModelAndView statisticsSetting(@PathVariable String siteId) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("configuration/settings/attributeSetting");
		StatisticsSettings statisticsSetting = getStatisticsService().getStatisticsSetting(siteId);
		SiteAttribute siteAttribute = statisticsSetting.getSiteAttribute();
		
		List<ClickTypeSetting> clickTypeList = siteAttribute.getClickTypeList();
		List<ServiceSetting> serviceList = siteAttribute.getServiceList();
		List<TypeSetting> typeList = siteAttribute.getTypeList();
		
		mav.addObject("clickTypeList", clickTypeList);
		mav.addObject("serviceList", serviceList);
		mav.addObject("typeList", typeList);
		return mav;
	}
	
	@RequestMapping("/settings/updateCategory")
	public ModelAndView updateCategory(@PathVariable String siteId, HttpServletRequest request) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("text");
		
		String mode = request.getParameter("mode");
		
		StatisticsSettings statisticsSetting = getStatisticsService().getStatisticsSetting(siteId);
		List<CategorySetting> categoryList = null;
		
		if(statisticsSetting.getCategoryList()!=null) {
			categoryList = statisticsSetting.getCategoryList();
		} else {
			categoryList = new ArrayList<CategorySetting>();
		}
		
		Set<String> categoryIdSet = new HashSet<String>();
		
		Writer writer = new StringWriter();
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		if("update".equals(mode)) {
			categoryList.clear();
			
			int count = Integer.parseInt(request.getParameter("count"));
			for (int inx = 0; inx < count; inx++) {
				String categoryIdGet = getString(request.getParameter("categoryId"+inx),"");
				String categoryNameGet = getString(request.getParameter("categoryName"+inx),"");
				boolean useRealTimePopularKeyword = getBoolean(request.getParameter("useRealTimePopularKeyword"+inx), false);
				boolean usePopularKeyword = getBoolean(request.getParameter("usePopularKeyword"+inx), false);
				boolean useRelateKeyword = getBoolean(request.getParameter("useRelateKeyword"+inx), false);
				if(categoryIdGet!=null && !"".equals(categoryIdGet)) {
					categoryList.add(new CategorySetting(categoryIdGet, categoryNameGet, usePopularKeyword, useRelateKeyword, useRealTimePopularKeyword));
					categoryIdSet.add(categoryIdGet);
				}
			}
			if(!categoryIdSet.contains("_root")) {
				categoryList.add(0, new CategorySetting("_root","ALL",false,false,false));
			}
			statisticsSetting.setCategoryList(categoryList);
			getStatisticsService().writeConfig();
		} else if("remove".equals(mode)) {
			String categoryId = getString(request.getParameter("categoryId"),"");
			for (int inx = 0; inx < categoryList.size(); inx++) {
				if(categoryList.get(inx).getId().equals(categoryId)) {
					categoryList.remove(inx);
					break;
				}
			}
			getStatisticsService().writeConfig();
		}
		
		responseWriter.object().key("success").value("true").key("status").value(1).endObject();
		
		mav.addObject("content", writer.toString());
		return mav;
	}
	
	@RequestMapping("/settings/updateSetting")
	public ModelAndView updateSetting(@PathVariable String siteId,
			@RequestParam String mode,
			@RequestParam String banWords,
			@RequestParam String fileEncoding,
			@RequestParam Integer realTimeKeywordMinimumHit,
			@RequestParam Integer realTimeKeywordRecentLog,
			@RequestParam Integer realTimeKeywordTopSize,
			@RequestParam Integer popularKeywordMinimumHit,
			@RequestParam Integer popularKeywordTopSize,
			@RequestParam Integer relateKeywordMinimumHit
			) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("text");
		
		StatisticsSettings statisticsSetting = getStatisticsService().getStatisticsSetting(siteId);
		
		Writer writer = new StringWriter();
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		if("update".equals(mode)) {
			
			RealTimePopularKeywordSetting realtimePopularKeywordSetting = new RealTimePopularKeywordSetting(
					realTimeKeywordRecentLog, realTimeKeywordTopSize,
					realTimeKeywordMinimumHit);
			PopularKeywordSetting popularKeywordSetting = new PopularKeywordSetting(
					popularKeywordTopSize, popularKeywordMinimumHit);
			RelateKeywordSetting relateKeywordSetting = new RelateKeywordSetting(
					relateKeywordMinimumHit);
			
			statisticsSetting.setRealtimePopularKeywordSetting(realtimePopularKeywordSetting);
			statisticsSetting.setPopularKeywordSetting(popularKeywordSetting);
			statisticsSetting.setRelateKeywordSetting(relateKeywordSetting);			
			
			getStatisticsService().writeConfig();
		}
		
		responseWriter.object().key("success").value("true").key("status").value(1).endObject();
		
		mav.addObject("content", writer.toString());
		return mav;
	}
	
	@RequestMapping("/settings/updateAttribute")
	public ModelAndView updateAttribute(@PathVariable String siteId, HttpServletRequest request) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("text");
		
		String mode = request.getParameter("mode");
		
		StatisticsSettings statisticsSetting = getStatisticsService().getStatisticsSetting(siteId);
		SiteAttribute siteAttribute = statisticsSetting.getSiteAttribute();
		
		List<TypeSetting> typeList = new ArrayList<TypeSetting>();
		List<ServiceSetting> serviceList = new ArrayList<ServiceSetting>();
		List<ClickTypeSetting> clickTypeList = new ArrayList<ClickTypeSetting>();
		
		Writer writer = new StringWriter();
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		if("update".equals(mode)) {
			
			int count = Integer.parseInt(request.getParameter("count"));
			
			//type 속성 업데이트
			for (int inx = 0; inx < count; inx++) {
				String typeIdGet = request.getParameter("typeId"+inx);
				String typeNameGet = getString(request.getParameter("typeName"+inx),"");
				Boolean isPrime = getBoolean(request.getParameter("typePrime"+inx),false);
				if(typeIdGet!=null) {
					logger.trace("typeId:{}/typeName:{}/isPrime:{}", typeIdGet, typeNameGet, isPrime);
					typeList.add(new TypeSetting(typeIdGet,typeNameGet,isPrime));
				} else {
					break;
				}
			}
			
			//service 속성 업데이트
			for (int inx = 0; inx < count; inx++) {
				String serviceIdGet = request.getParameter("serviceId"+inx);
				String serviceNameGet = getString(request.getParameter("serviceName"+inx),"");
				Boolean isPrime = (inx == getInt(request.getParameter("servicePrimeIndex"), -1));
				if(serviceIdGet!=null) {
					logger.trace("serviceId:{}/serviceName:{}/isPrime:{}", serviceIdGet, serviceNameGet, isPrime);
					serviceList.add(new ServiceSetting(serviceIdGet,serviceNameGet,isPrime));
				} else {
					break;
				}
			}
			
			//click-type 속성 업데이트
			for (int inx = 0; inx < count; inx++) {
				String clickTypeIdGet = request.getParameter("clickTypeId"+inx);
				String clickTypeNameGet = getString(request.getParameter("clickTypeName"+inx),"");
				if(clickTypeIdGet!=null) {
					logger.trace("clickTypeId:{}/clickTypeName:{}", clickTypeIdGet, clickTypeNameGet);
					clickTypeList.add(new ClickTypeSetting(clickTypeIdGet,clickTypeNameGet));
				} else {
					break;
				}
			}
			
			siteAttribute.setTypeList(typeList);
			siteAttribute.setServiceList(serviceList);
			siteAttribute.setClickTypeList(clickTypeList);
			
			getStatisticsService().writeConfig();
		}
		
		responseWriter.object().key("success").value("true").key("status").value(1).endObject();
		
		mav.addObject("content", writer.toString());
		return mav;
	}
}
