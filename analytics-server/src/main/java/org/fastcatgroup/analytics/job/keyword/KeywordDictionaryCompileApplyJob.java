package org.fastcatgroup.analytics.job.keyword;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.db.mapper.SearchKeywordRankMapper;
import org.fastcatgroup.analytics.db.vo.RankKeywordVO;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.job.Job;
import org.fastcatgroup.analytics.keyword.KeywordDictionary.KeywordDictionaryType;
import org.fastcatgroup.analytics.keyword.KeywordService;
import org.fastcatgroup.analytics.keyword.PopularKeywordDictionary;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.settings.StatisticsSettings.Category;

public class KeywordDictionaryCompileApplyJob extends Job {

	private static final long serialVersionUID = 5101762691161535526L;

	@Override
	public JobResult doRun() throws AnalyticsException {
		Map<String, Object> args = getMapArgs();
		String dictionaryTypeStr = (String) args.get("dictionaryType");
		//interval: 몇번째 이전 통계를 컴파일할것인가? 1일전, 2일전.. 1주전, 2주전.. 등이 가능하다. 기본은 1.
		Integer intervalValue = (Integer) args.get("interval");
		int interval = intervalValue != null ? intervalValue : 1;
		
		KeywordDictionaryType dictionaryType = null;

		String timeStr = null;

		SimpleDateFormat dateFormat = null;

		Calendar calendar = Calendar.getInstance();

		KeywordService keywordService = ServiceManager.getInstance().getService(KeywordService.class);

		StatisticsService statisticsService = ServiceManager.getInstance().getService(StatisticsService.class);

		List<Category> categoryList = statisticsService.statisticsSettings().getCategoryList();

		try {
			dictionaryType = KeywordDictionaryType.valueOf(dictionaryTypeStr);

			if (dictionaryType == KeywordDictionaryType.POPULAR_KEYWORD_REALTIME) {

				timeStr = "REALTIME";

				compilePopularKeyword(keywordService, categoryList, dictionaryType, interval, timeStr);

			} else if (dictionaryType == KeywordDictionaryType.POPULAR_KEYWORD_DAY) {

				dateFormat = new SimpleDateFormat("yyyyMMdd");

				calendar.add(Calendar.DATE, -1);

				timeStr = "D" + dateFormat.format(calendar.getTime());

				compilePopularKeyword(keywordService, categoryList, dictionaryType, interval, timeStr);

			} else if (dictionaryType == KeywordDictionaryType.POPULAR_KEYWORD_WEEK) {

				dateFormat = new SimpleDateFormat("yyyyMMww");

				calendar.add(Calendar.WEEK_OF_YEAR, -1);

				timeStr = "W" + dateFormat.format(calendar.getTime());

				compilePopularKeyword(keywordService, categoryList, dictionaryType, interval, timeStr);

			} else if (dictionaryType == KeywordDictionaryType.POPULAR_KEYWORD_MONTH) {

				dateFormat = new SimpleDateFormat("yyyyMM");

				calendar.add(Calendar.MONTH, -1);

				timeStr = "M" + dateFormat.format(calendar.getTime());

				compilePopularKeyword(keywordService, categoryList, dictionaryType, interval, timeStr);

//			} else if (dictionaryType == KeywordDictionaryType.RELATE_KEYWORD) {
//				compileRelateKeyword(keywordService, categoryList, dictionaryType);
			}

		} catch (IllegalArgumentException e) {
			logger.error("", e);
			return new JobResult("INVALID KEYWORD DICTIONARY TYPE >> " + dictionaryTypeStr);
		} catch (Exception e) {
			logger.error("", e);
			return new JobResult("KEYWORD DICTIONARY COMPILE ERROR >> " + e.getMessage());
		}

		return new JobResult(true);
	}

	private void compilePopularKeyword(KeywordService keywordService, List<Category> categoryList, KeywordDictionaryType type, int interval, String time) throws Exception {

		SearchKeywordRankMapper mapper = keywordService.getMapperSession(SearchKeywordRankMapper.class).getMapper();

		for (Category category : categoryList) {

			List<RankKeywordVO> keywordList = mapper.getEntryList("", "", category.getId(), time);

			PopularKeywordDictionary dictionary = new PopularKeywordDictionary(keywordList);

			File writeFile = keywordService.getFile(category.getId(), type, interval);

			File parentDir = writeFile.getParentFile();

			if (!parentDir.exists()) {
				FileUtils.forceMkdir(parentDir);
			}

			OutputStream ostream = null;

			try {

				ostream = new FileOutputStream(writeFile);

				dictionary.writeTo(ostream);

			} finally {

				if (ostream != null)
					try {
						ostream.close();
					} catch (IOException e) {
					}
			}
		}
	}

//	private void compileRelateKeyword(KeywordService keywordService, List<Category> categoryList, KeywordDictionaryType type) throws Exception {
//
//		RelateKeywordMapper mapper = keywordService.getMapperSession(RelateKeywordMapper.class).getMapper();
//
//		for (Category category : categoryList) {
//
//			List<RelateKeywordVO> keywordList = mapper.getEntryList(category.getId());
//
//			RelateKeywordDictionary dictionary = new RelateKeywordDictionary();
//
//			for (RelateKeywordVO keyword : keywordList) {
//				dictionary.putRelateKeyword(keyword.getKeyword(), keyword.getValue());
//			}
//
//			File writeFile = keywordService.getFile(category.getId(), type);
//
//			File parentDir = writeFile.getParentFile();
//
//			if (!parentDir.exists()) {
//				FileUtils.forceMkdir(parentDir);
//			}
//
//			OutputStream ostream = null;
//
//			try {
//
//				ostream = new FileOutputStream(writeFile);
//
//				dictionary.writeTo(ostream);
//
//			} finally {
//
//				if (ostream != null)
//					try {
//						ostream.close();
//					} catch (IOException e) {
//					}
//			}
//		}
//	}
}
