package org.fastcatgroup.analytics.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBException;

import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.db.vo.PopularKeywordVO.RankDiffType;
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
 * 검색통계를 내기위해 CategoryStatistics를 이용하여 컬렉션별로 검색어 로그를 5분단위, 하루단위로 만들어 놓는다. 외부 job이 주기적으로 5분에 한번씩 실시간 인기검색어를 통계내고, 하루 자정에 한번 일간 인기검색어 통계를 내어 KeywordService의 db에 저장한다.
 * */
public class StatisticsService extends AbstractService {

	private File statisticsHome;
	private StatisticsSettings statisticsSettings;
	private Map<String, CategoryStatistics> categoryStatisticsMap;

	Map<String, Map<String, List<RankKeyword>>> realtimePopularKeywordMap;

	Map<String, SiteSearchLogStatisticsModule> siteStatisticsModuleMap;

	public StatisticsService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);
		statisticsHome = environment.filePaths().getStatisticsRoot().file();
		statisticsHome.mkdir();

		File realtimeKeywordBaseDir = new File(statisticsHome, "rt");
		if (!realtimeKeywordBaseDir.exists()) {
			realtimeKeywordBaseDir.mkdirs();
		}
		realtimePopularKeywordMap = new ConcurrentHashMap<String, Map<String, List<RankKeyword>>>();

		siteStatisticsModuleMap = new ConcurrentHashMap<String, SiteSearchLogStatisticsModule>();
		//
		List<String> siteIdList = new ArrayList<String>();
		siteIdList.add("total");
		siteIdList.add("mobile");

		for (String siteId : siteIdList) {
			SiteSearchLogStatisticsModule module = new SiteSearchLogStatisticsModule(statisticsHome, siteId, environment, settings);
			module.load();
			siteStatisticsModuleMap.put(siteId, module);
		}
		for (String siteId : siteIdList) {

			File siteBaseDir = new File(realtimeKeywordBaseDir, siteId);
			if (!siteBaseDir.exists()) {
				siteBaseDir.mkdir();
			}

			// 하위 카테고리를 확인하여 로딩한다.
			File[] categoryDirList = listCategoryDir(siteBaseDir);
			for (File categoryDir : categoryDirList) {
				String categoryId = categoryDir.getName();
				File resultDir = new File(categoryDir, "result");
				if (resultDir.exists()) {
					File f = new File(resultDir, "rt-popular.txt");
					if (f.exists()) {
						// load keyword file to dictionary.
						List<RankKeyword> keywordList = loadKeywordListFile(f);
						updateRealtimePopularKeywordList(siteId, categoryId, keywordList);
					}
				}
			}

		}

	}

	private List<RankKeyword> loadKeywordListFile(File f) {
		BufferedReader reader = null;
		String line = null;
		List<RankKeyword> list = new ArrayList<RankKeyword>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			int rank = 1;
			while ((line = reader.readLine()) != null) {
				String[] el = line.split("\t");
				RankKeyword k = new RankKeyword(el[0], rank);
				k.setRankDiff(Integer.parseInt(el[1]));
				k.setRankDiffType(RankDiffType.valueOf(el[2]));
				list.add(k);
				rank++;
			}
		} catch (IOException e) {

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return list;
	}

	/**
	 * 실시간 인기검색어를 리턴한다.
	 * */
	public List<RankKeyword> getRealtimePopularKeywordList(String siteId, String categoryId) {
		Map<String, List<RankKeyword>> map = realtimePopularKeywordMap.get(siteId);
		if (map != null) {
			return map.get(categoryId);
		}
		return null;
	}

	public void updateRealtimePopularKeywordList(String siteId, String categoryId, List<RankKeyword> keywordList) {
		Map<String, List<RankKeyword>> map = realtimePopularKeywordMap.get(siteId);
		if (map == null) {
			map = new ConcurrentHashMap<String, List<RankKeyword>>();
			realtimePopularKeywordMap.put(siteId, map);
		}
		map.put(categoryId, keywordList);
	}

	private File[] listCategoryDir(File dir) {
		return dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
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

	public CategoryStatistics categoryStatistics(String categoryId) {
		return categoryStatisticsMap.get(categoryId);
	}

	public void log(String type, String siteId, String... entries) {
		//현재 type은 사용되지 않음.
		SiteSearchLogStatisticsModule module = siteStatisticsModuleMap.get(siteId);
		if(module != null){
			module.log(entries);
		}
		
	}

}
