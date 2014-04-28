package org.fastcatsearch.analytics.analysis.config;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatsearch.analytics.util.JAXBConfigs;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteCategoryConfigTest {
	protected static Logger logger = LoggerFactory.getLogger(SiteCategoryConfigTest.class);
	@Test
	public void test() {
		
		File siteCategoryConfigFile = new File("/Users/swsong/TEST_HOME/danawa1022/analytics-1.14.2/statistics/search/site-category.xml");
		SiteListSetting siteCategoryConfig = null;
		try {
			siteCategoryConfig = JAXBConfigs.readConfig(siteCategoryConfigFile, SiteListSetting.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		logger.debug("##siteCategoryConfig >> {}", siteCategoryConfig);
	}
	
	@Test
	public void testWrite() {
		
		File siteCategoryConfigFile = new File("/Users/swsong/TEST_HOME/danawa1022/analytics-1.14.2/statistics/search/site-category2.xml");
		SiteListSetting siteCategoryConfig = new SiteListSetting();
		List<CategorySetting> categorySetting = new ArrayList<CategorySetting>();
		categorySetting.add(new CategorySetting("total", "통합검색",false,false,false));
		categorySetting.add(new CategorySetting("cat1", "카테고리1",false,false,false));
		categorySetting.add(new CategorySetting("cat2", "카테고리2",false,false,false));
		StatisticsSettings settings = new StatisticsSettings();
		settings.setCategoryList(categorySetting);
		SiteSetting siteSetting = new SiteSetting("www","www");
		siteSetting.setStatisticsSettings(settings);
		List<SiteSetting> sites = new ArrayList<SiteSetting>();
		siteCategoryConfig.setSiteList(sites);
		try {
			JAXBConfigs.writeConfig(siteCategoryConfigFile, siteCategoryConfig, SiteListSetting.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		logger.debug("##siteCategoryConfig >> {}", siteCategoryConfig);
	}

}
