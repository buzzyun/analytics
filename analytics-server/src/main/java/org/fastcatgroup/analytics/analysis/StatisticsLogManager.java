package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 타입별로 구현되는 로그매니저.
 * Search type에 해당되는 모든 내용이 여기에서 구현된다.
 * logger는 로그를 기록하고,
 * log handler는 통계를 내서 저장하는 방식.
 * 
 * rolling로거는 실시간 검색어를 취합하는데 쓰인다.
 * 
 * */
public class StatisticsLogManager {
	
	
	private Map<String, DailyRawLogger> siteLogger;
	private RollingRawLogger rollingLogger;
	public StatisticsLogManager(File logFileBase){
		
		//TODO
		//base를 기점으로 로그파일을 찾아서 통계를 낸다.
		
		//handler는 실시간 인기검색어, 일간인기검색어, 주간, 월간, 년간 인기검색어 생성을 한다.
		
		//통계결과를 DB에 저장된다.
		
	}
	public StatisticsLogManager(){
		
		File baseDir = null;
		
		String prefix = null;
		
		int rollingLimit = 6;
		
		siteLogger = new HashMap<String, DailyRawLogger>();
		rollingLogger = new RollingRawLogger(baseDir, prefix, rollingLimit);
	}
	
	public void log(String site, String[] data){
		DailyRawLogger logger = siteLogger.get(site);
		
		logger.log(data);
		rollingLogger.log(data);
	}
	
	
	
	
}
