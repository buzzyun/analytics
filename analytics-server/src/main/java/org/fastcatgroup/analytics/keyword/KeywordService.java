package org.fastcatgroup.analytics.keyword;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.fastcatgroup.analytics.db.AbstractDBService;
import org.fastcatgroup.analytics.db.mapper.ADKeywordMapper;
import org.fastcatgroup.analytics.db.mapper.KeywordSuggestionMapper;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.SettingFileNames;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.keyword.KeywordDictionary.KeywordDictionaryType;
import org.fastcatgroup.analytics.keyword.module.PopularKeywordModule;
import org.fastcatgroup.analytics.keyword.module.RelateKeywordModule;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.settings.KeywordServiceSettings;
import org.fastcatgroup.analytics.settings.KeywordServiceSettings.KeywordServiceCategory;
import org.fastcatgroup.analytics.util.JAXBConfigs;

/**
 * 인기키워드 등의 키워드서비스를 제공한다.
 * */
public class KeywordService extends AbstractDBService {

	private KeywordServiceSettings keywordServiceSettings;

	private PopularKeywordModule popularKeywordModule;
	private RelateKeywordModule relateKeywordModule;

	private File moduleHome;

	private static Class<?>[] mapperList = new Class<?>[] { SearchKeywordRankMapper.class, RelateKeywordMapper.class, KeywordSuggestionMapper.class, ADKeywordMapper.class };

	public KeywordService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);

		moduleHome = environment.filePaths().getKeywordsRoot().file();
		popularKeywordModule = new PopularKeywordModule(moduleHome, environment, settings);
		relateKeywordModule = new RelateKeywordModule(moduleHome, environment, settings);
	}

	public File getFile(String categoryId, KeywordDictionaryType type) {
		return getFile(categoryId, type, 1);
	}

	public File getFile(String categoryId, KeywordDictionaryType type, int interval) {

		if (type == KeywordDictionaryType.POPULAR_KEYWORD_REALTIME || type == KeywordDictionaryType.POPULAR_KEYWORD_DAY || type == KeywordDictionaryType.POPULAR_KEYWORD_WEEK
				|| type == KeywordDictionaryType.POPULAR_KEYWORD_MONTH) {
			return popularKeywordModule.getDictionaryFile(categoryId, type, interval);
		} else if (type == KeywordDictionaryType.RELATE_KEYWORD) {
			return relateKeywordModule.getDictionaryFile(categoryId);
		} else {
			// TODO ad keyword, keyword suggestion
		}

		return null;
	}

	public KeywordServiceSettings keywordServiceSettings() {
		return keywordServiceSettings;
	}

	@Override
	protected boolean doStart() throws AnalyticsException {
		Properties driverProperties = null;
		Map<String, Object> globalParam = null;
		init(settings, mapperList, driverProperties, globalParam);

		File keywordServiceConfigFile = environment.filePaths().configPath().path(SettingFileNames.keywordServiceConfig).file();
		try {
			keywordServiceSettings = JAXBConfigs.readConfig(keywordServiceConfigFile, KeywordServiceSettings.class);
		} catch (JAXBException e) {
			logger.error("KeywordService setting file read error.", e);
			return false;
		}
		if (keywordServiceSettings == null) {
			logger.error("Cannot load KeywordService setting file >> {}", keywordServiceSettings);
			return false;
		}

		// 모듈 로딩.
		loadKeywordModules();

		return true;
	}

	public void loadPopularKeywordDictionary(String categoryId, KeywordDictionaryType type, int interval) throws IOException {
		popularKeywordModule.loadAndSetDictionary(categoryId, type, interval);
	}

	public void loadRelateKeywordDictionary(String categoryId) throws IOException {
		relateKeywordModule.loadAndSetDictionary(categoryId);
	}

	private void loadKeywordModules() {
		List<KeywordServiceCategory> categoryList = keywordServiceSettings.getCategoryList();
		popularKeywordModule.setCategoryList(categoryList);
		popularKeywordModule.load();
		relateKeywordModule.setCategoryList(categoryList);
		relateKeywordModule.load();
	}

	private void unloadKeywordModules() {
		popularKeywordModule.unload();
		relateKeywordModule.unload();
	}

	@Override
	protected boolean doStop() throws AnalyticsException {

		unloadKeywordModules();

		return super.doStop();
	}

	@Override
	protected boolean doClose() throws AnalyticsException {
		return super.doClose();
	}

	public KeywordDictionary getKeywordDictionary(String categoryId, KeywordDictionaryType key) {
		return getKeywordDictionary(categoryId, key, 1);
	}

	public KeywordDictionary getKeywordDictionary(String categoryId, KeywordDictionaryType key, int interval) {
		if (key == KeywordDictionaryType.POPULAR_KEYWORD_REALTIME || key == KeywordDictionaryType.POPULAR_KEYWORD_DAY || key == KeywordDictionaryType.POPULAR_KEYWORD_WEEK
				|| key == KeywordDictionaryType.POPULAR_KEYWORD_MONTH) {
			return popularKeywordModule.getKeywordDictionary(categoryId, key, interval);
		} else if (key == KeywordDictionaryType.RELATE_KEYWORD) {
			return relateKeywordModule.getKeywordDictionary(categoryId);
		} else {
			// TODO ad keyword, keyword suggestion
		}
		return null;
	}

	public KeywordDictionary getKeywordDictionary(KeywordDictionaryType key) {
		return getKeywordDictionary(null, key);
	}

	@Override
	protected void initMapper(Class<?>[] mapperList) throws AnalyticsException {
		// TODO Auto-generated method stub

	}

}
