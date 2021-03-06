package org.fastcatsearch.analytics.web.controller;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CTRSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.ClickTypeSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.PopularKeywordSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.RealTimePopularKeywordSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.RelateKeywordSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.ServiceSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.SiteProperties;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatsearch.analytics.control.JobService;
import org.fastcatsearch.analytics.job.Job;
import org.fastcatsearch.analytics.job.task.DailySearchLogAnalyticsTaskRunJob;
import org.fastcatsearch.analytics.job.task.TestBulkLogAnalyticsTaskRunJob;
import org.fastcatsearch.analytics.util.ResponseWriter;
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
	public ModelAndView run(@PathVariable String siteId, @RequestParam(required = false) String date) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("configuration/management/run");

		Calendar calendar1 = null;

		if (date != null && !"".equals(date)) {
			String[] dateArr = date.split(" - ");
			calendar1 = StatisticsUtils.parseDatetimeString(dateArr[0], true);
		} else {
			calendar1 = StatisticsUtils.getNowCalendar();
			calendar1.add(Calendar.DATE, -1);
		}

		mav.addObject("date", StatisticsUtils.toDatetimeString(calendar1));
		return mav;
	}
	
	@RequestMapping("/management/doRun")
	public ModelAndView doRun(@PathVariable String siteId, @RequestParam(required = false, value="taskType") String[] taskType, @RequestParam(required = false) String date) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("text");
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
			for(String type : taskType) {
				logger.debug(">>> {}", type);
			}
			Job job = new DailySearchLogAnalyticsTaskRunJob(siteId, timeId1, timeId2, taskType);
			JobService.getInstance().offer(job);
		}

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
			Calendar now = StatisticsUtils.getNowCalendar();
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
		SiteProperties siteProperties = statisticsSetting.getSiteProperties();
		PopularKeywordSetting popularKeywordSetting = statisticsSetting.getPopularKeywordSetting();
		RelateKeywordSetting relateKeywordSetting = statisticsSetting.getRelateKeywordSetting();
		RealTimePopularKeywordSetting realTimePopularKeywordSetting = statisticsSetting.getRealtimePopularKeywordSetting();
		CTRSetting ctrSetting = statisticsSetting.getCtrSetting();
		
		int defaultMinimumHitCount = 2;
		int defaultTopCount = 10;
		int defaultRealtimeRecentCount = 6;
		int defaultRootStoreCount = 10000;
		int defaultCategoryStoreCount = 100;
		
		if(siteProperties == null) {
			siteProperties = new SiteProperties("", 15, 5, 0);
		}
		if (popularKeywordSetting == null) {
			popularKeywordSetting = new PopularKeywordSetting(defaultTopCount, defaultMinimumHitCount, defaultRootStoreCount, defaultCategoryStoreCount);
		}
		if (relateKeywordSetting == null) {
			relateKeywordSetting = new RelateKeywordSetting(defaultMinimumHitCount);
		}
		if (realTimePopularKeywordSetting == null) {
			realTimePopularKeywordSetting = new RealTimePopularKeywordSetting(defaultRealtimeRecentCount, defaultTopCount, 300, defaultMinimumHitCount);
		}

		mav.addObject("siteProperties", siteProperties);
		mav.addObject("realTimePopularKeywordSetting",realTimePopularKeywordSetting);
		mav.addObject("popularKeywordSetting",popularKeywordSetting);
		mav.addObject("relateKeywordSetting",relateKeywordSetting);
		mav.addObject("ctrSetting",ctrSetting);
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
			@RequestParam String banwords,
			@RequestParam Integer maxKeywordLength,
			@RequestParam Integer scheduleDelayInSeconds,
			@RequestParam Integer dailyScheduleTime,
			@RequestParam Integer realTimeKeywordMinimumHit,
			@RequestParam Integer realTimeKeywordRecentLog,
			@RequestParam Integer realTimeKeywordTopSize,
			@RequestParam Integer realTimeKeywordPeriod,
			@RequestParam Integer popularKeywordMinimumHit,
			@RequestParam Integer popularKeywordTopSize,
			@RequestParam Integer rootStoreCount,
			@RequestParam Integer categoryStoreCount,
			@RequestParam Integer relateKeywordMinimumHit,
			@RequestParam Integer dumpFileDaySize,
			@RequestParam String targetFilePath,
			@RequestParam Integer minimumClickCount,
			@RequestParam Float fileDailyDecayFactor
			) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("text");
		
		StatisticsSettings statisticsSetting = getStatisticsService().getStatisticsSetting(siteId);
		
		Writer writer = new StringWriter();
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		if("update".equals(mode)) {
			
			RealTimePopularKeywordSetting realtimePopularKeywordSetting = new RealTimePopularKeywordSetting(
					realTimeKeywordRecentLog, realTimeKeywordTopSize, realTimeKeywordPeriod, 
					realTimeKeywordMinimumHit);
			PopularKeywordSetting popularKeywordSetting = new PopularKeywordSetting(
					popularKeywordTopSize, popularKeywordMinimumHit, rootStoreCount, categoryStoreCount);
			RelateKeywordSetting relateKeywordSetting = new RelateKeywordSetting(
					relateKeywordMinimumHit);
			SiteProperties siteProperties = new SiteProperties(banwords, maxKeywordLength, scheduleDelayInSeconds, dailyScheduleTime);
			statisticsSetting.setSiteProperties(siteProperties);
			statisticsSetting.setRealtimePopularKeywordSetting(realtimePopularKeywordSetting);
			statisticsSetting.setPopularKeywordSetting(popularKeywordSetting);
			statisticsSetting.setRelateKeywordSetting(relateKeywordSetting);		
			
			logger.trace("targetFilePath:{}", targetFilePath);
			
			CTRSetting ctrSetting = new CTRSetting(dumpFileDaySize, targetFilePath, minimumClickCount, fileDailyDecayFactor);
			statisticsSetting.setCtrSetting(ctrSetting);
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
		
		Writer writer = null;
		try {
			writer = new StringWriter();
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
			responseWriter.done();
			
			mav.addObject("content", writer.toString());
			
		} finally {
			
			if(writer != null) try {
				writer.close();
			} catch (Exception ignore) { }
			
		}
		return mav;
	}
}
