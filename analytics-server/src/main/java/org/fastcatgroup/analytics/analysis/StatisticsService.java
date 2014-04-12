package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBException;

import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.CategoryConfig;
import org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO;
import org.fastcatgroup.analytics.db.vo.RelateKeywordVO;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.SettingFileNames;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.service.AbstractService;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.settings.StatisticsSettings;
import org.fastcatgroup.analytics.util.JAXBConfigs;

/**
 * 검색통계를 내기위해 CategoryStatistics를 이용하여 컬렉션별로 검색어 로그를 5분단위, 하루단위로 만들어 놓는다. 외부 job이 주기적으로 5분에 한번씩 실시간 인기검색어를 통계내고, 하루 자정에 한번 일간 인기검색어 통계를 내어 KeywordService의 db에 저장한다.
 * */
public class StatisticsService extends AbstractService {

	private File statisticsHome;
	private StatisticsSettings statisticsSettings;
	private Map<String, CategoryStatistics> categoryStatisticsMap;

	private Map<String, Map<String, List<RankKeyword>>> realtimePopularKeywordMap;
	private Map<String, Map<String, Map<String, List<RankKeyword>>>> popularKeywordMap;

	private Map<String, SiteSearchLogStatisticsModule> siteStatisticsModuleMap;

	private Map<String, Map<String, List<String>>> relateKeywordMap;

	private SiteCategoryListConfig siteCategoryListConfig;

	public StatisticsService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);
		statisticsHome = environment.filePaths().getStatisticsRoot().file("search");
		if (!statisticsHome.exists()) {
			statisticsHome.mkdir();
		}

		realtimePopularKeywordMap = new ConcurrentHashMap<String, Map<String, List<RankKeyword>>>();
		
		popularKeywordMap = new ConcurrentHashMap<String, Map<String, Map<String, List<RankKeyword>>>>();

		siteStatisticsModuleMap = new ConcurrentHashMap<String, SiteSearchLogStatisticsModule>();

		relateKeywordMap = new ConcurrentHashMap<String, Map<String, List<String>>>();

		// 로드 siteCategoryConfig
		File siteCategoryConfigFile = new File(statisticsHome, "site-category.xml");
		try {
			siteCategoryListConfig = JAXBConfigs.readConfig(siteCategoryConfigFile, SiteCategoryListConfig.class);
		} catch (JAXBException e) {
			logger.error("", e);
		}
		logger.debug("##siteCategoryConfig >> {}", siteCategoryListConfig);
		List<SiteCategoryConfig> siteCategoryList = siteCategoryListConfig.getList();

		for (SiteCategoryConfig siteCategoryConfig : siteCategoryList) {

			String siteId = siteCategoryConfig.getSiteId();
			List<CategoryConfig> categoryList = siteCategoryConfig.getCategoryList();

			List<String> categoryIdList = new ArrayList<String>();
			for (CategoryConfig c : categoryList) {
				categoryIdList.add(c.getId());
			}

			SiteSearchLogStatisticsModule module = new SiteSearchLogStatisticsModule(this, statisticsHome, siteId, categoryIdList, environment, settings);
			siteStatisticsModuleMap.put(siteId, module);
		}
	}
	
	public void writeConfig() {
		File siteCategoryConfigFile = new File(statisticsHome, "site-category.xml");
		try {
			JAXBConfigs.writeConfig(siteCategoryConfigFile, siteCategoryListConfig, SiteCategoryListConfig.class);
		} catch (JAXBException e) {
			logger.error("", e);
		}
	}

	// 초기 서비스시작시 DB에서 연관어 읽어서 올림.
	private void loadRelateKeyword() {
		relateKeywordMap.clear();

		List<SiteCategoryConfig> siteCategoryList = siteCategoryListConfig.getList();

		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<RelateKeywordMapper> mapperSession = dbService.getMapperSession(RelateKeywordMapper.class);

		try {
			RelateKeywordMapper mapper = mapperSession.getMapper();

			for (SiteCategoryConfig siteCategoryConfig : siteCategoryList) {

				String siteId = siteCategoryConfig.getSiteId();
				Map<String, List<String>> siteRelateKeywordMap = new HashMap<String, List<String>>();
				relateKeywordMap.put(siteId, siteRelateKeywordMap);
				logger.debug("### Load relate keyword site {} > {}", siteId, mapper);
				int count = mapper.getCount(siteId);
				logger.debug("### Load relate keyword site count {}", count);
				List<RelateKeywordVO> list = mapper.getEntryList(siteId,0,0);
				if (list != null) {
					for (RelateKeywordVO vo : list) {
						String keyword = vo.getKeyword();
						String value = vo.getValue();
						if (value != null && value.length() > 0) {
							String[] valueList = value.split(",");
							List<String> relateKeywordList = new ArrayList<String>(valueList.length);
							for (String v : valueList) {
								relateKeywordList.add(v);
							}
							
							siteRelateKeywordMap.put(keyword, relateKeywordList);
							logger.debug("[{}]relate {} > {}", siteId, keyword, relateKeywordList);
						}else{
							logger.debug("[{}]relate NULL VALUE {}", siteId, keyword);
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
	}

	public SiteCategoryListConfig getSiteCategoryListConfig() {
		return siteCategoryListConfig;
	}

	/**
	 * 실시간 인기검색어를 리턴한다.
	 * */
	public List<RankKeyword> getRealtimePopularKeywordList(String siteId, String categoryId) {
//		logger.debug("get realtime keyword. siteId:{} / categoryId:{}", siteId, categoryId);
		Map<String, List<RankKeyword>> map = realtimePopularKeywordMap.get(siteId);
		if (map != null) {
			if (categoryId == null || categoryId.length() == 0) {
				categoryId = "_root";
			}
//			logger.debug("get realtime keyword map:{}", map);
			return map.get(categoryId);
		}
		return null;
	}

	public void updateRealtimePopularKeywordList(String siteId, String categoryId, List<RankKeyword> keywordList) {

		logger.debug("## updateRealtimePopularKeyword {}:{} > {}", siteId, categoryId, keywordList);
		Map<String, List<RankKeyword>> map = realtimePopularKeywordMap.get(siteId);
		if (map == null) {
			map = new ConcurrentHashMap<String, List<RankKeyword>>();
			realtimePopularKeywordMap.put(siteId, map);
		}
		map.put(categoryId, keywordList);
		logger.debug("realtime keyword. map:{}", map);
	}

	public Map<String, List<String>> getRelateKeywordMap(String siteId) {
//		logger.debug("relateKeywordMap:{}", relateKeywordMap);
		Map<String, List<String>> map = relateKeywordMap.get(siteId);
		if (map != null) {
//			logger.debug("get relative keyword map:{} [{}]", map);
			return map;
		}
		return null;
	}

	public void updateRelativeKeywordMap(String siteId, Map<String, List<String>> keywordMap) {

		logger.debug("## updateRelativeKeyword {}:{} > {}", siteId, keywordMap);
		relateKeywordMap.put(siteId, keywordMap);
		logger.debug("relative keyword. map:{}", keywordMap);
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

		for (SiteSearchLogStatisticsModule module : siteStatisticsModuleMap.values()) {
			module.load();
		}

		loadRelateKeyword();

		return true;
	}

	@Override
	protected boolean doStop() throws AnalyticsException {
		for (SiteSearchLogStatisticsModule module : siteStatisticsModuleMap.values()) {
			module.unload();
		}
		return true;
	}

	@Override
	protected boolean doClose() throws AnalyticsException {
		siteStatisticsModuleMap.clear();
		return true;
	}

	public StatisticsSettings statisticsSettings() {
		return statisticsSettings;
	}

	public CategoryStatistics categoryStatistics(String categoryId) {
		return categoryStatisticsMap.get(categoryId);
	}

	public void addLog(String type, String siteId, String... entries) {
		// 현재 type은 사용되지 않음.
		SiteSearchLogStatisticsModule module = siteStatisticsModuleMap.get(siteId);
		if (module != null) {
			module.addLog(entries);
		}

	}
	
	public void addTypeLog(String type, String siteId, String... entries) {
		// 현재 type은 사용되지 않음.
		SiteSearchLogStatisticsModule module = siteStatisticsModuleMap.get(siteId);
		if (module != null) {
			module.addTypeLog(entries);
		}

	}

	public void addClickLog(String siteId, String... entries) {
		// 현재 type은 사용되지 않음.
		SiteSearchLogStatisticsModule module = siteStatisticsModuleMap.get(siteId);
		if (module != null) {
			module.addClickLog(entries);
		}

	}
	
	public void clearPopularKeywordList(){
		popularKeywordMap.clear();
	}
	
	public List<RankKeyword> getPopularKeywordList(String siteId, String categoryId, String timeId) {
		//logger.debug("get keyword. siteId:{} / categoryId:{} / timeId:{} / popularKeywordMap:{}", siteId, categoryId, timeId, popularKeywordMap);
		Map<String, Map<String, List<RankKeyword>>> siteMap = popularKeywordMap.get(siteId);
		List<RankKeyword> list = null;
		if (siteMap != null) {
			if (categoryId == null || categoryId.length() == 0) {
				categoryId = "_root";
			}
//			logger.debug("get realtime keyword siteMap:{}", siteMap);
			Map<String,List<RankKeyword>> timeMap = siteMap.get(categoryId);
			
			if(timeMap != null){
				list = timeMap.get(timeId);
			}
		}
		
		if(list == null){
			//디비 로딩 및 업데이트.
			AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			MapperSession<SearchKeywordRankMapper> mapperSession = dbService.getMapperSession(SearchKeywordRankMapper.class);

			try {
				SearchKeywordRankMapper mapper = mapperSession.getMapper();
				int length = 10;
				list = new ArrayList<RankKeyword>(length);
				List<RankKeywordVO> voList = mapper.getEntryList(siteId, categoryId, timeId, null, 0, 0, length);
			
				for(RankKeywordVO vo : voList){
					RankKeyword keyword = new RankKeyword(vo.getKeyword(), vo.getRank(), vo.getCount());
					keyword.setRankDiff(vo.getRankDiff());
					keyword.setRankDiffType(vo.getRankDiffType());
					list.add(keyword);
				}
				
				logger.debug("## Update RankKeyword list {}:{}:{} > {}", siteId, categoryId, timeId, list);
			
			} catch (Exception e) {
				logger.error("", e);
			} finally {
				if (mapperSession != null) {
					mapperSession.closeSession();
				}
			}
			
			
			if(siteMap == null){
				siteMap = new HashMap<String, Map<String, List<RankKeyword>>>();
				popularKeywordMap.put(siteId, siteMap);
			}
			
			Map<String,List<RankKeyword>> timeMap = siteMap.get(categoryId);
			
			if(timeMap == null){
				timeMap = new HashMap<String,List<RankKeyword>>();
				siteMap.put(categoryId, timeMap);
			}
			
			timeMap.put(timeId, list);
			
		}
		return list;
	}

}
