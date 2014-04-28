package org.fastcatsearch.analytics.settings;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CategorySetting;
import org.fastcatsearch.analytics.util.JAXBConfigs;
import org.junit.Test;

public class StatisticsSettingsTest {

	@Test
	public void test() throws JAXBException {
		StatisticsSettings s = new StatisticsSettings();
		List<CategorySetting> categoryList = new ArrayList<CategorySetting>();
		categoryList.add(new CategorySetting("total", "통합검색", true, true, false));
		s.setCategoryList(categoryList);
		
		StringWriter writer = new StringWriter();
		JAXBConfigs.writeRawConfig(writer, s, StatisticsSettings.class);
		
		System.out.println(writer);
		
		String source = writer.toString();
		
		Reader reader = new StringReader(source);
		
		StatisticsSettings s2 = JAXBConfigs.readConfig(reader, StatisticsSettings.class);
		List<CategorySetting> categoryList2 = s2.getCategoryList();
		for(CategorySetting category : categoryList2){
			System.out.println(category);
		}
	}

}
