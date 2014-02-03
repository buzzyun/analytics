package org.fastcatgroup.analytics.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.fastcatgroup.analytics.analysis.log.LogData;
import org.junit.Test;

/*
 * 각 노드에서 받은 검색로그를 키워드순 카운트 로그로 머징하여 변환하는 과정테스트. 
 * node1.log, node2.log, .. => 0.log로 변환된다.
 * */
public class LogAggregatorTest {

	@Test
	public void test() {
		File targetDir = new File("src/test/resources/statistics/rt");
		File tmpDir = new File(targetDir, "tmp");
		
		int runKeySize = 100000;
		String inputEncoding = "utf-8";
		String outputEncoding = "utf-8";
		Set<String> banWords = null;
		
		File[] inFileList = tmpDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				try{
					return FilenameUtils.getExtension(name).equals("log");
				}catch(Exception e){
					return false;
				}
			}
		});
		String outputFilename = "0.log";
		
		List<AbstractLogAggregator<LogData>> handlerList = new ArrayList<AbstractLogAggregator<LogData>>();
//		handlerList.add(new PopularKeywordLogAggregateHandler(targetDir, outputFilename, runKeySize, outputEncoding, banWords, 0));
		
		LogAggregator<LogData> tmpLogAggregator = new LogAggregator<LogData>(inFileList, inputEncoding, handlerList);
		tmpLogAggregator.aggregate(); 
		
		
		
	}
	
	@Test
	public void test3() {
		String a = "	a	b	";
		int i = 0;
		for(String s : a.split("\t")){
			System.out.println(i + ": [" + s + "]");
		}
	}
}
