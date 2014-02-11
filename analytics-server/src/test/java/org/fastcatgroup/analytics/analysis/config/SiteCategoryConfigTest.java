package org.fastcatgroup.analytics.analysis.config;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.CategoryConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.util.JAXBConfigs;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteCategoryConfigTest {
	protected static Logger logger = LoggerFactory.getLogger(SiteCategoryConfigTest.class);
	@Test
	public void test() {
		
		File siteCategoryConfigFile = new File("/Users/swsong/TEST_HOME/danawa1022/analytics-1.14.2/statistics/search/site-category.xml");
		SiteCategoryListConfig siteCategoryConfig = null;
		try {
			siteCategoryConfig = JAXBConfigs.readConfig(siteCategoryConfigFile, SiteCategoryListConfig.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		logger.debug("##siteCategoryConfig >> {}", siteCategoryConfig);
	}
	
	@Test
	public void testWrite() {
		
		File siteCategoryConfigFile = new File("/Users/swsong/TEST_HOME/danawa1022/analytics-1.14.2/statistics/search/site-category2.xml");
		SiteCategoryListConfig siteCategoryConfig = new SiteCategoryListConfig();
		List<SiteCategoryConfig> list = new ArrayList<SiteCategoryConfig>();
		SiteCategoryConfig config = new SiteCategoryConfig("total", "통합검색");
		config.getCategoryList().add(new CategoryConfig("cat1", "카테고리1"));
		config.getCategoryList().add(new CategoryConfig("cat2", "카테고리2"));
		list.add(config);
		
		siteCategoryConfig.setList(list);
		try {
			JAXBConfigs.writeConfig(siteCategoryConfigFile, siteCategoryConfig, SiteCategoryListConfig.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		logger.debug("##siteCategoryConfig >> {}", siteCategoryConfig);
	}

}
