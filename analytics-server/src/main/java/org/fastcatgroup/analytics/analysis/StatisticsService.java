package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.SettingFileNames;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.service.AbstractService;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.settings.StatisticsSettings;
import org.fastcatgroup.analytics.settings.StatisticsSettings.Category;
import org.fastcatgroup.analytics.util.JAXBConfigs;

/**
 * 검색통계를 내기위해 CategoryStatistics를 이용하여 컬렉션별로 검색어 로그를 5분단위, 하루단위로 만들어 놓는다. 
 * 외부 job이 주기적으로 5분에 한번씩 실시간 인기검색어를 통계내고, 하루 자정에 한번 일간 인기검색어 통계를 내어 KeywordService의 db에 저장한다. 
 * KeywordService는 갱신된 인기검색어를 service-node로 전송하여 서비스할 인기검색어를 사전형태로 메모리에 띄워 서비스한다.
 * */
public class StatisticsService extends AbstractService {

	private File statisticsHome;
	private StatisticsSettings statisticsSettings;
	private Map<String, CategoryStatistics> categoryStatisticsMap;
	
	private Map<String, StatisticsLogManager> logManagerMap;
	
	public StatisticsService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);
		statisticsHome = environment.filePaths().getStatisticsRoot().file();
		statisticsHome.mkdir();
		
		logManagerMap = new HashMap<String, StatisticsLogManager>();
		
		String typeId = "";
		File logFileBase = null;
		Properties properties = null;//설정. jaxb로 구현필요. 
		StatisticsLogManager m = new StatisticsLogManager(logFileBase);//properties
		//TODO type 별 매니저..
		logManagerMap.put(typeId, m);
	}

	public Collection<CategoryStatistics> getCategoryStatisticsList() {
		return categoryStatisticsMap.values();
	}

	@Override
	protected boolean doStart() throws AnalyticsException {
		categoryStatisticsMap = new HashMap<String, CategoryStatistics>();
		File statisticsSettingFile = environment.filePaths().configPath().path(SettingFileNames.statisticsConfig).file();
		try {
			statisticsSettings = JAXBConfigs.readConfig(statisticsSettingFile, StatisticsSettings.class);
		} catch (JAXBException e) {
			logger.error("statisticsSetting read error.", e);
			return false;
		}
		if (statisticsSettings == null) {
			logger.error("Cannot load statistics setting file >> {}", statisticsSettingFile);
			return false;
		}

		List<Category> categoryList = statisticsSettings.getCategoryList();
		for (Category category : categoryList) {
			String categoryId = category.getId();
			CategoryStatistics categoryStatistics = new CategoryStatistics(category, statisticsHome);
			categoryStatisticsMap.put(categoryId, categoryStatistics);
			logger.debug("> {}", category);
		}


		return true;
	}

	@Override
	protected boolean doStop() throws AnalyticsException {
		for (CategoryStatistics categoryStatistics : categoryStatisticsMap.values()) {
			categoryStatistics.close();
		}
		return true;
	}

	@Override
	protected boolean doClose() throws AnalyticsException {

		return true;
	}

	public StatisticsSettings statisticsSettings() {
		return statisticsSettings;
	}

	// type은 향후 검색데이터뿐 아니라, 사용자가 여러가지 type을 생성하여 정의할수 있다.  
	public StatisticsLogManager logManager(String type) {
		return logManagerMap.get(type);
	}

	public CategoryStatistics categoryStatistics(String categoryId) {
		return categoryStatisticsMap.get(categoryId);
	}


}
