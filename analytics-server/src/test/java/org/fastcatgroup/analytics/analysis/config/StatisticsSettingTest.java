package org.fastcatgroup.analytics.analysis.config;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.fastcatgroup.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.ClickTypeSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.PopularKeywordSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.RealtimePopularKeywordSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.RelateKeywordSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.ServiceSetting;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.SiteAttribute;
import org.fastcatgroup.analytics.analysis.config.StatisticsSettings.TypeSetting;
import org.fastcatgroup.analytics.util.JAXBConfigs;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsSettingTest {

	private static final Logger logger = LoggerFactory.getLogger(StatisticsSettingTest.class);
	
	@Test
	public void testSave() throws Exception {
		
		
		StatisticsSettings settings = new StatisticsSettings();
		
		List<CategorySetting> categoryListSetting = new ArrayList<CategorySetting>();
		
		categoryListSetting.add(new CategorySetting("_root","전체",true,true,true));
		categoryListSetting.add(new CategorySetting("cate1","카테고리1",true,true,true));
		settings.setCategoryList(categoryListSetting);
		settings.setBanwords("무료,배송,쿠폰,할인");
		settings.setFileEncoding("utf8");
		RealtimePopularKeywordSetting realtimePopularKeyword = new RealtimePopularKeywordSetting(10,10,10);
		settings.setRealtimePopularKeywordSetting(realtimePopularKeyword);
		PopularKeywordSetting popularKeyword = new PopularKeywordSetting(10,10);
		settings.setPopularKeywordSetting(popularKeyword);
		RelateKeywordSetting relateKeyword = new RelateKeywordSetting(10);
		settings.setRelateKeywordSetting(relateKeyword);
		SiteAttribute siteAttribute = new SiteAttribute();
		
		List<ClickTypeSetting> clickTypeList = new ArrayList<ClickTypeSetting>();
		clickTypeList.add(new ClickTypeSetting("blog","블로그"));
		clickTypeList.add(new ClickTypeSetting("goshop","사러가기"));
		clickTypeList.add(new ClickTypeSetting("list","리스트보기"));
		siteAttribute.setClickTypeList(clickTypeList);
		
		List<ServiceSetting> serviceList = new ArrayList<ServiceSetting>();
		serviceList.add(new ServiceSetting("autocomplete","자동완성"));
		serviceList.add(new ServiceSetting("total","통합검색"));
		serviceList.add(new ServiceSetting("main","메인화면"));
		serviceList.add(new ServiceSetting("catelist","카테고리목록"));
		siteAttribute.setServiceList(serviceList);
		
		List<TypeSetting> typeList = new ArrayList<TypeSetting>();
		typeList.add(new TypeSetting("category","카테고리별 분류"));
		typeList.add(new TypeSetting("page","페이지별 분류"));
		typeList.add(new TypeSetting("sort","정렬방식별 분류"));
		typeList.add(new TypeSetting("age","연령대별 분류"));
		typeList.add(new TypeSetting("service","서비스별 분류"));
		typeList.add(new TypeSetting("login","로그인여부별 분류"));
		typeList.add(new TypeSetting("gender","성별 분류"));
		siteAttribute.setTypeList(typeList);
		
		settings.setSiteAttribute(siteAttribute);
		
		File file = new File("/tmp/test.xml");
		
		JAXBConfigs.writeConfig(file, settings, StatisticsSettings.class);
		
		assertTrue(true);
	}
	
	@Test
	public void testSiteSave() throws Exception {
		
		SiteListSetting settings = new SiteListSetting();
		List<SiteSetting> siteList = new ArrayList<SiteSetting>();
		settings.setSiteList(siteList);
		siteList.add(new SiteSetting("www","쇼핑몰"));
		siteList.add(new SiteSetting("blog","블로그"));
		
		File file = new File("/tmp/test.xml");
		JAXBConfigs.writeConfig(file, settings, SiteListSetting.class);
		
		SiteListSetting config = JAXBConfigs.readConfig(file, SiteListSetting.class);
		
		logger.debug("config:{}", config.getSiteList());
		
		assertTrue(true);
	}
}