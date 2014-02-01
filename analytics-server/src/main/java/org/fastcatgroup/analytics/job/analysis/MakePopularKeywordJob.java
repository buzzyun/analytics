package org.fastcatgroup.analytics.job.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.fastcatgroup.analytics.analysis.PopularKeywordGenerator;
import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.analysis.vo.RankKeyword;
import org.fastcatgroup.analytics.control.ResultFuture;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.PopularKeywordMapper;
import org.fastcatgroup.analytics.db.vo.PopularKeywordVO;
import org.fastcatgroup.analytics.exception.AnalyticsException;
import org.fastcatgroup.analytics.job.Job;
import org.fastcatgroup.analytics.job.Job.JobResult;
import org.fastcatgroup.analytics.keyword.KeywordService;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.settings.StatisticsSettings;
import org.fastcatgroup.analytics.settings.StatisticsSettings.Category;

/**
 * 일/주/월/년의 인기키워드를 만들어 DB에 입력한다.
 * 
 * TODO 구현필요.
 * */

public class MakePopularKeywordJob extends Job {

	private static final long serialVersionUID = -187434814363709436L;

	@Override
	public JobResult doRun() throws AnalyticsException {

		String timeFormatString = "R" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date());// 지금시간.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -1);
		String prevTimeFormatString = "R" + new SimpleDateFormat("yyyyMMddHHmm").format(cal.getTime()); // 1시간 이전.

		StatisticsService searchStatisticsService = ServiceManager.getInstance().getService(StatisticsService.class);
		StatisticsSettings statisticsSettings = searchStatisticsService.statisticsSettings();
		List<Category> categoryList = statisticsSettings.getCategoryList();

		String fileEncoding = "utf-8";
		

		// 다 받으면 해당 위치의 파일들을 하나의 파일로 모두 모아서
		logger.debug("environment {}", environment);
		logger.debug("filePaths {}", environment.filePaths());
		logger.debug("getStatisticsRoot {}", environment.filePaths().getStatisticsRoot());

		for (Category category : categoryList) {
			// 카테고리별로 통계를 낸다.
			if (category.isUseRealTimePopularKeyword()) {
				try {
					String categoryId = category.getId();
					File targetDir = environment.filePaths().getStatisticsRoot().file(categoryId, "popular", "rt");
					File collectDir = environment.filePaths().getStatisticsRoot().file(categoryId, "collect", "rt");
					// 취합된 로그파일이 존재하는 디렉토리.
					File[] inFileList = collectDir.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							try {
								return FilenameUtils.getExtension(name).equals("log");
							} catch (Exception e) {
								return false;
							}
						}
					});

					
					/*
					 * TODO 루프를 돌면서 일/주/월/년 통계를 만든다.
					 * 
					 * 고려사항. 일/주/월/년 통계를 매일 갱신하면 1주,1달,1년이 끝나지 않아도 그때그때의 일기검색어를 볼수 있게된다. 대신 입력전에 기존 주/월의 통계를 다 지우고 입력한다.
					 * */
					
					
					PopularKeywordGenerator g = new PopularKeywordGenerator(targetDir, inFileList, statisticsSettings, fileEncoding);
					// 카테고리별 실시간 인기키워드결과.
					List<RankKeyword> result = g.generate();

					logger.debug("-- POPULAR [{}] --", category.getId());
					for (RankKeyword rankKeyword : result) {
						logger.debug("{}", rankKeyword);
					}
					logger.debug("--------------------");

					KeywordService keywordService = ServiceManager.getInstance().getService(KeywordService.class);
					MapperSession<PopularKeywordMapper> mapperSession = keywordService.getMapperSession(PopularKeywordMapper.class);
					try {
						// DB에 입력한다.
						PopularKeywordMapper mapper = mapperSession.getMapper();
						// 먼저 TIME = RECENT 에 1~10위 까지 업데이트함. 10개가 안될수 있으므로, Update를 사용.

						String TIME_REALTIME = "REALTIME"; 
						for(RankKeyword rankKeyword : result){
							int rank = rankKeyword.getRank();
							
							PopularKeywordVO vo = mapper.getRankEntry(categoryId, TIME_REALTIME, rank);
							
							PopularKeywordVO newVo = new PopularKeywordVO(categoryId, TIME_REALTIME, rankKeyword.getKeyword(), 0, rank, rankKeyword.getRankDiffType(), rankKeyword.getRankDiff());
							if(vo == null){
								mapper.putEntry(newVo);
							}else{
								mapper.updateEntry(newVo);
							}
							// 히스토리성 데이터를 남김.
							newVo.setTime(timeFormatString);
							mapper.putEntry(newVo);
						}
						
						//TODO 구현필요.
						//mapper.deleteElderThan(categoryId, prevTimeFormatString);// 1시간이전 데이터 지움.
					} finally {
						if (mapperSession != null) {
							mapperSession.closeSession();
						}
					}

				} catch (Throwable e) {
					logger.error("[" + category.getId() + "] fail to generate realtime popular keyword", e);

				}

			}
		}

		return new JobResult(true);
	}
}
