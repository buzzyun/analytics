package org.fastcatgroup.analytics.keyword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fastcatgroup.analytics.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 사전과 비슷한 객체를 만들어 키워드 서비스를 하기 위한 모듈
 * @author websqrd
 */
public class RelateKeywordDictionaryCompiler {
	
	private static final Logger logger = LoggerFactory.getLogger(RelateKeywordDictionaryCompiler.class);
	
	private static final String fileName = "relate_keyword.csv";
	
	public static Map<String, List<String>> compile(Environment env) {
		
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		
		File home = env.homeFile();
		File dir = new File(home,"statistics");
		File file = new File(dir, fileName);
		
		InputStream istream = null;
		BufferedReader reader = null;
		
		try {
			istream = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(istream));
			
			for (String rline = null; (rline = reader.readLine()) != null;) {
				
				if(rline.length()==0) {
					continue;
				}
				String[] tab = rline.split("\t");
				if(tab.length == 5) {
					
					String keyword = tab[2].trim().toUpperCase();
					String[] synonyms = tab[3].split(",");
					String[] values = tab[4].split(",");
					
					List<String> valueList = new ArrayList<String>();
					{
						Set<String> set = new HashSet<String>();
						for(String value : values) {
							value = value.trim();
							String valueUpper = value.toUpperCase();
							if(!set.contains(valueUpper)) {
								valueList.add(value);
								set.add(valueUpper);
							}
						}
						set = null;
					}
					
					ret.put(keyword, valueList);
					for(String synonym : synonyms) {
						synonym = synonym.trim().toUpperCase();
						if(!ret.containsKey(synonym)) {
							ret.put(synonym, valueList);
						}
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (reader != null) try {
				reader.close();
			} catch (IOException ignore) { }
			
			if (istream != null) try {
				istream.close();
			} catch (IOException ignore) { }
		}
		return ret;
	}
}
