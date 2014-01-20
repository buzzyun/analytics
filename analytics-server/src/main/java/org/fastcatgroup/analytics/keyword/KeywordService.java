package org.fastcatgroup.analytics.keyword;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.fastcatgroup.analytics.control.JobService;
import org.fastcatgroup.analytics.db.AbstractDBService;
import org.fastcatgroup.analytics.db.mapper.ADKeywordMapper;
import org.fastcatgroup.analytics.db.mapper.KeywordSuggestionMapper;
import org.fastcatgroup.analytics.db.mapper.ManagedMapper;
import org.fastcatgroup.analytics.db.mapper.PopularKeywordMapper;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.env.Environment;
import org.fastcatgroup.analytics.env.SettingFileNames;
import org.fastcatgroup.analytics.env.Settings;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.job.analysis.MakePopularKeywordJob;
import org.fastcatgroup.analytics.job.analysis.MakeRealtimePopularKeywordJob;
import org.fastcatgroup.analytics.job.analysis.MakeRelateKeywordJob;
import org.fastcatgroup.analytics.keyword.KeywordDictionary.KeywordDictionaryType;
import org.fastcatgroup.analytics.keyword.module.PopularKeywordModule;
import org.fastcatgroup.analytics.keyword.module.RelateKeywordModule;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.settings.KeywordServiceSettings;
import org.fastcatgroup.analytics.settings.KeywordServiceSettings.KeywordServiceCategory;
import org.fastcatgroup.analytics.util.DateUtils;
import org.fastcatgroup.analytics.util.JAXBConfigs;

/**
 * 인기키워드 등의 키워드서비스를 제공한다.
 * */
public class KeywordService extends AbstractDBService {

	private KeywordServiceSettings keywordServiceSettings;

	private boolean isMaster;

	private PopularKeywordModule popularKeywordModule;
	private RelateKeywordModule relateKeywordModule;

	private File moduleHome;

	private static Class<?>[] mapperList = new Class<?>[] { PopularKeywordMapper.class, RelateKeywordMapper.class, KeywordSuggestionMapper.class, ADKeywordMapper.class };

	public KeywordService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super("db/keyword", KeywordService.mapperList, environment, settings, serviceManager);

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

		// 키워드 서비스노드이면..
		logger.info("This node provides KeywordService. isMaster > {}", isMaster);

		// 모듈 로딩.
		loadKeywordModules();

		// 마스터 노드만 통계를 낸다.
		if (isMaster) {
			// 수집 스케쥴을 건다.
			// Realtime 정시에서 시작하여 5분단위.
			Calendar calendar = DateUtils.getLatestTimeLargerThanNow(5);
			calendar.add(Calendar.MINUTE, 2); // +2분 여유.
			Date nextTimeForRealtimeLog = calendar.getTime();
			JobService.getInstance().schedule(new MakeRealtimePopularKeywordJob(), nextTimeForRealtimeLog, DateUtils.getSecondsByMinutes(5)); // 5분주기.
			// Daily 매 정시기준으로 1일 단위.
			calendar = DateUtils.getNextDayHour(0); // 다음날 0시.
			calendar.add(Calendar.MINUTE, 10); // +10분 여유.
			Date nextTimeForDailyLog = calendar.getTime();
			JobService.getInstance().schedule(new MakePopularKeywordJob(), nextTimeForDailyLog, DateUtils.getSecondsByDays(1)); // 1일
			
			JobService.getInstance().schedule(new MakeRelateKeywordJob(), nextTimeForDailyLog, DateUtils.getSecondsByDays(1)); // 1일
			
			
		}

		if (isMaster) {
			// 마스터서버이면, 자동완성, 연관키워드, 인기검색어 등의 db를 연다.
			return super.doStart();
		} else {

			return true;
		}

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

		if (isMaster) {
			return super.doStop();
		} else {

			return true;
		}
	}

	@Override
	protected boolean doClose() throws AnalyticsException {
		if (isMaster) {
			return super.doClose();
		} else {

			return true;
		}
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
	protected void initMapper(ManagedMapper managedMapper) throws Exception {
		// do nothing
	}

}
