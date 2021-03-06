package org.fastcatsearch.analytics.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBException;

import org.fastcatsearch.analytics.analysis.config.SiteListSetting;
import org.fastcatsearch.analytics.analysis.config.StatisticsSettings;
import org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting;
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
import org.fastcatsearch.analytics.analysis.vo.RankKeyword;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatsearch.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatsearch.analytics.db.vo.RankKeywordVO;
import org.fastcatsearch.analytics.db.vo.RelateKeywordVO;
import org.fastcatsearch.analytics.env.Environment;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.exception.AnalyticsException;
import org.fastcatsearch.analytics.service.AbstractService;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.fastcatsearch.analytics.util.JAXBConfigs;

/**
 * 검색통계를 내기위해 CategoryStatistics를 이용하여 컬렉션별로 검색어 로그를 5분단위, 하루단위로 만들어 놓는다. 외부 job이 주기적으로 5분에 한번씩 실시간 인기검색어를 통계내고, 하루 자정에 한번 일간 인기검색어 통계를 내어 KeywordService의 db에 저장한다.
 * */
public class StatisticsService extends AbstractService {

	private File statisticsHome;
	private SiteListSetting siteListSetting;
	
	private Map<String, Map<String, List<RankKeyword>>> realtimePopularKeywordMap;
	private Map<String, Map<String, Map<String, List<RankKeyword>>>> popularKeywordMap;
	private Map<String, Map<String, List<String>>> relateKeywordMap;
	private Map<String, SiteSearchLogStatisticsModule> siteStatisticsModuleMap;
	private Map<String, StatisticsSettings> statisticsSettingMap;

	public StatisticsService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);
		statisticsHome = environment.filePaths().getStatisticsRoot().file();
		if (!statisticsHome.exists()) {
			statisticsHome.mkdir();
		}

		realtimePopularKeywordMap = new ConcurrentHashMap<String, Map<String, List<RankKeyword>>>();
		popularKeywordMap = new ConcurrentHashMap<String, Map<String, Map<String, List<RankKeyword>>>>();
		relateKeywordMap = new ConcurrentHashMap<String, Map<String, List<String>>>();
		siteStatisticsModuleMap = new ConcurrentHashMap<String, SiteSearchLogStatisticsModule>();
		statisticsSettingMap = new ConcurrentHashMap<String, StatisticsSettings>();
		// 로드 siteCategoryConfig
		
		File confDir = new File(environment.filePaths().file(),"conf");
		File confSiteDir = new File(confDir,"sites");
		File siteConfigFile = new File(confDir,"sites.xml");
		List<SiteSetting> siteList = null;
		try {
			siteListSetting = JAXBConfigs.readConfig(siteConfigFile, SiteListSetting.class);
			if(siteListSetting == null) {
				siteListSetting = new SiteListSetting();
			}
			
			siteList = siteListSetting.getSiteList();
			if(siteList == null) {
				siteList = new ArrayList<SiteSetting>();
				siteListSetting.setSiteList(siteList);
			}
			for(SiteSetting siteSetting : siteList) {
				String siteId = siteSetting.getId();
				File statisticsFile = new File(confSiteDir, siteSetting.getId()+".xml");
				
				StatisticsSettings statisticsSettings = JAXBConfigs.readConfig(statisticsFile, StatisticsSettings.class);
				siteSetting.setStatisticsSettings(statisticsSettings);
				
				List<String> categoryIdList = new ArrayList<String>();
				List<CategorySetting> categoryList = siteSetting.getStatisticsSettings().getCategoryList();
				for(CategorySetting c : categoryList) {
					categoryIdList.add(c.getId());
				}
				File siteFileHome = new File(statisticsHome, siteId);
				if(!siteFileHome.exists()){
					siteFileHome.mkdir();
				}
				SiteSearchLogStatisticsModule module = new SiteSearchLogStatisticsModule(this, siteFileHome, siteId, categoryIdList, environment, settings);
				siteStatisticsModuleMap.put(siteId, module);
				statisticsSettingMap.put(siteId, statisticsSettings);
			}
		} catch (JAXBException e) {
			logger.error("", e);
		}
		
		StatisticsProperties.runKeySize = settings.getInt("runKeySize", 10 * 10000);
		StatisticsProperties.encoding = settings.getString("encoding", "utf-8");
		StatisticsProperties.ROOT_ID = settings.getString("rootId", "_root");
	}

	public void addSite(String siteId, SiteSetting setting) {
		if(!statisticsSettingMap.containsKey(siteId)) {
			this.siteListSetting.getSiteList().add(setting);
			AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			dbService.addNewSiteMappers(siteId);

			File siteFileHome = new File(statisticsHome, siteId);
			if(!siteFileHome.exists()){
				siteFileHome.mkdir();
			}
			List<String> categoryIdList = new ArrayList<String>();
			SiteSearchLogStatisticsModule module = new SiteSearchLogStatisticsModule(this, siteFileHome, siteId, categoryIdList, environment, settings);
			siteStatisticsModuleMap.put(siteId, module);
			statisticsSettingMap.put(siteId, setting.getStatisticsSettings());
		}
	}

	public void deleteConfig(String siteId) {
		File confDir = new File(environment.filePaths().file(),"conf");
		File confSiteDir = new File(confDir,"sites");
		File file = new File(confSiteDir, siteId+".xml");
		if(file.exists()) {
			file.delete();
		}
	}
	
	public void writeConfig() {
		File confDir = new File(environment.filePaths().file(),"conf");
		File confSiteDir = new File(confDir,"sites");
		File siteConfigFile = new File(confDir,"sites.xml");
		
		if(!confSiteDir.exists()) {
			confSiteDir.mkdirs();
		}
		
		List<SiteSetting> siteList = siteListSetting.getSiteList();
		
		logger.trace("siteList:{}", siteList);
		
		File[] statisticsFiles = new File[siteList.size()];
		for (int inx = 0; inx < siteList.size(); inx++) {
			statisticsFiles[inx] = new File(confSiteDir, siteList.get(inx).getId()+".xml");
		}
		
		try {
			JAXBConfigs.writeConfig(siteConfigFile, siteListSetting, SiteListSetting.class);
		} catch (JAXBException e) {
			logger.error("error writing:{}", siteConfigFile, e);
		}
		for(int inx=0;inx<siteList.size();inx++) {
			try {
				JAXBConfigs.writeConfig(statisticsFiles[inx], siteList.get(inx).getStatisticsSettings(), StatisticsSettings.class);
			} catch (JAXBException e) {
				logger.error("error writing:{} / {}", statisticsFiles[inx], siteList.get(inx), e);
			}
		}
	}

	// 초기 서비스시작시 DB에서 연관어 읽어서 올림.
	private void loadRelateKeyword() {
		relateKeywordMap.clear();
		
		List<SiteSetting> siteList = siteListSetting.getSiteList();

		AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<RelateKeywordMapper> mapperSession = dbService.getMapperSession(RelateKeywordMapper.class);

		try {
			RelateKeywordMapper mapper = mapperSession.getMapper();
			
			for(SiteSetting siteSetting : siteList) {
				String siteId = siteSetting.getId();
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
							logger.trace("[{}]relate {} > {}", siteId, keyword, relateKeywordList);
						}else{
							logger.trace("[{}]relate NULL VALUE {}", siteId, keyword);
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
	
	public SiteListSetting getSiteListSetting() {
		return siteListSetting;
	}
	
	public StatisticsSettings getStatisticsSetting(String siteId) {
		return statisticsSettingMap.get(siteId);
	}

	/**
	 * 실시간 인기검색어를 리턴한다.
	 * */
	public List<RankKeyword> getRealtimePopularKeywordList(String siteId, String categoryId) {
		logger.trace("get realtime keyword. siteId:{} / categoryId:{}", siteId, categoryId);
		Map<String, List<RankKeyword>> map = realtimePopularKeywordMap.get(siteId);
		if (map != null) {
			if (categoryId == null || categoryId.length() == 0) {
				categoryId = "_root";
			}
			logger.trace("get realtime keyword map:{}", map);
			return map.get(categoryId);
		}
		return null;
	}

	public void updateRealtimePopularKeywordList(String siteId, String categoryId, List<RankKeyword> keywordList) {

		logger.trace("## updateRealtimePopularKeyword {}:{} > {}", siteId, categoryId, keywordList);
		Map<String, List<RankKeyword>> map = realtimePopularKeywordMap.get(siteId);
		if (map == null) {
			map = new ConcurrentHashMap<String, List<RankKeyword>>();
			realtimePopularKeywordMap.put(siteId, map);
		}
		map.put(categoryId, keywordList);
		logger.trace("realtime keyword. map:{}", map);
	}

	public Map<String, List<String>> getRelateKeywordMap(String siteId) {
		logger.trace("relateKeywordMap:{}", relateKeywordMap);
		Map<String, List<String>> map = relateKeywordMap.get(siteId);
		if (map != null) {
			logger.trace("get relative keyword map:{} [{}]", map);
			return map;
		}
		return null;
	}

	public void updateRelativeKeywordMap(String siteId, Map<String, List<String>> keywordMap) {

		logger.trace("## updateRelativeKeyword {}:{} > {}", siteId, keywordMap);
		relateKeywordMap.put(siteId, keywordMap);
		logger.trace("relative keyword. map:{}", keywordMap);
	}

	@Override
	protected boolean doStart() throws AnalyticsException {
		//categoryStatisticsMap = new HashMap<String,Map<String, CategoryStatistics>>();

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

	public void addLog(String siteId, String... entries) {
		// 현재 type은 사용되지 않음.
		SiteSearchLogStatisticsModule module = siteStatisticsModuleMap.get(siteId);
		if (module != null) {
			module.addLog(entries);
		}
	}
	
	public void addTypeLog(String siteId, String... entries) {
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
				
				logger.trace("## Update RankKeyword list {}:{}:{} > {}", siteId, categoryId, timeId, list);
			
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
	
	public void removeSite(String siteId) {
		List<SiteSetting> siteList = getSiteListSetting().getSiteList();
		for(int inx=0;inx < siteList.size(); inx++) {
			if(siteList.get(inx).getId().equals(siteId)) {
				siteList.remove(inx);
				statisticsSettingMap.remove(siteId);
				AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
				dbService.dropSiteMapper(siteId);
				break;
			}
		}
	}
	
	public SiteSetting newDefaultSite(String id, String name) {
		//초기 사이트 구축시 입력되는 기본 데이터
		SiteSetting ret = new SiteSetting(id, name);
		StatisticsSettings statisticsSettings  = newDefaultStatistics();
		ret.setStatisticsSettings(statisticsSettings);
		return ret;
	}
	
	public StatisticsSettings newDefaultStatistics() {
		StatisticsSettings statisticsSettings = new StatisticsSettings();
		//카테고리
		List<CategorySetting> categoryList = new ArrayList<CategorySetting>();
		categoryList.add(new CategorySetting(StatisticsProperties.ROOT_ID, "ALL", false, false, false));
		statisticsSettings.setCategoryList(categoryList);
		
		//인기키워드
		PopularKeywordSetting popularKeywordSetting = new PopularKeywordSetting(10, 1, 10000, 100);
		statisticsSettings.setPopularKeywordSetting(popularKeywordSetting);
		
		//연관키워드
		RelateKeywordSetting relateKeywordSetting = new RelateKeywordSetting(2);
		statisticsSettings.setRelateKeywordSetting(relateKeywordSetting);
		
		//실시간키워드
		RealTimePopularKeywordSetting realtimePopularKeywordSetting = new RealTimePopularKeywordSetting(6, 10, 300, 1);
		statisticsSettings.setRealtimePopularKeywordSetting(realtimePopularKeywordSetting);
		
		//CTR
		CTRSetting ctrSetting = new CTRSetting(0, "", 5, 0.9f);
		statisticsSettings.setCtrSetting(ctrSetting);
		
		//Site Properties
		SiteProperties siteProperties = new SiteProperties(); 
		statisticsSettings.setSiteProperties(siteProperties);
		
		//Site Attribute
		SiteAttribute siteAttribute = new SiteAttribute();
		List<ClickTypeSetting> clickTypeList = new ArrayList<ClickTypeSetting>();
		siteAttribute.setClickTypeList(clickTypeList);
		List<ServiceSetting> serviceList = new ArrayList<ServiceSetting>();
		siteAttribute.setServiceList(serviceList);
		List<TypeSetting> typeList = new ArrayList<TypeSetting>();
		siteAttribute.setTypeList(typeList);
		statisticsSettings.setSiteAttribute(siteAttribute);
		
		return statisticsSettings;
	}
}
