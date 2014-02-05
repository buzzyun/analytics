package org.fastcatgroup.analytics.analysis;

import org.fastcatgroup.analytics.settings.StatisticsSettings;
import org.fastcatgroup.analytics.settings.StatisticsSettings.PopularKeywordConfig;
import org.fastcatgroup.analytics.settings.StatisticsSettings.RealtimePopularKeywordConfig;
import org.fastcatgroup.analytics.settings.StatisticsSettings.RelateKeywordConfig;

public class RealtimePopularKeywordGeneratorTest {

	public StatisticsSettings getStatisticsSettings(String banwords, int rtPopMinHit, int popMinHit, int relMinHit){
		RealtimePopularKeywordConfig realTimePopularKeywordConfig = new RealtimePopularKeywordConfig();
		PopularKeywordConfig popularKeywordConfig = new PopularKeywordConfig();
		RelateKeywordConfig relateKeywordConfig = new RelateKeywordConfig();
		realTimePopularKeywordConfig.setMinimumHitCount(rtPopMinHit);
		realTimePopularKeywordConfig.setRecentLogUsingCount(6);
		realTimePopularKeywordConfig.setTopCount(10);
		popularKeywordConfig.setMinimumHitCount(popMinHit);
		relateKeywordConfig.setMinimumHitCount(relMinHit);
		
		StatisticsSettings statisticsSettings = new StatisticsSettings();
		statisticsSettings.setBanwords(banwords);
		statisticsSettings.setRealTimePopularKeywordConfig(realTimePopularKeywordConfig);
		statisticsSettings.setPopularKeywordConfig(popularKeywordConfig);
		statisticsSettings.setRelateKeywordConfig(relateKeywordConfig);
		
		return statisticsSettings;
	}

}
