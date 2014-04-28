package org.fastcatsearch.analytics.analysis;

import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.PopularKeywordSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.RealTimePopularKeywordSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings.RelateKeywordSetting;


public class RealtimePopularKeywordGeneratorTest {

	public StatisticsSettings getStatisticsSettings(String banwords, int rtPopMinHit, int popMinHit, int relMinHit){
		RealTimePopularKeywordSetting realTimePopularKeywordConfig = new RealTimePopularKeywordSetting(10,10,10);
		PopularKeywordSetting popularKeywordConfig = new PopularKeywordSetting(10,10);
		RelateKeywordSetting relateKeywordConfig = new RelateKeywordSetting(10);
		realTimePopularKeywordConfig.setMinimumHitCount(rtPopMinHit);
		realTimePopularKeywordConfig.setRecentCount(6);
		realTimePopularKeywordConfig.setTopCount(10);
		popularKeywordConfig.setMinimumHitCount(popMinHit);
		relateKeywordConfig.setMinimumHitCount(relMinHit);
		
		StatisticsSettings statisticsSettings = new StatisticsSettings();
		statisticsSettings.getSiteProperties().setBanwords(banwords);
		statisticsSettings.setRealtimePopularKeywordSetting(realTimePopularKeywordConfig);
		statisticsSettings.setPopularKeywordSetting(popularKeywordConfig);
		statisticsSettings.setRelateKeywordSetting(relateKeywordConfig);
		
		return statisticsSettings;
	}

}
