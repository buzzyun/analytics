package org.fastcatsearch.analytics.util.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.fastcatsearch.analytics.analysis.StatisticsUtils;

public class LogGenerator {
	public static void main(String[] args) {
		if (args.length != 10) {
			System.out.println("Usage: LogGenerator [analytics-path] [site id] [keyword file path] [min,max qty] [from,to date] [category list] [type list] [click min,max] [click id list] [click types]");
			System.out.println("   ex: > LogGenerator /home/fastcat/analytics total ../keyword.txt 1000,10000 20140102,20150102 cat1,cat2 category<cat1|cate2>,page<1|2|3|4|5>,sort<score|date|price>,age<0|10|20|30|40|50>,service<autocomplete|catelist>,login<Y|N>,gender<M|F|U> 0,10 11111,22222,33333,aaa_12345,bbb_22222,ccc_33333 blog,goshop,list");
			System.exit(1);
		}
		String homePath = args[0];
		String siteId = args[1];
		String keywordFilePath = args[2];
		String[] minMax = args[3].split(",");
		if(minMax.length < 2) {
			//default value
			minMax = new String[] { "1000", "10000" };
		}
		
		int minQty = Integer.parseInt(minMax[0]);
		int maxQty = Integer.parseInt(minMax[1]);
		
		String[] fromTo = args[4].split(",");
		
		System.out.println("from:"+fromTo[0]+" ~ to:"+fromTo[1]);
		Calendar fromDate = parseDate(fromTo[0]);
		Calendar toDate = parseDate(fromTo[1]);
		
		String[] categoryList = args[5].split(",");
		String typeList = args[6]; // category,page,sort,age,service,login,gender
		String[] clickMinMax = args[7].split(",");
		
		int minClick = Integer.parseInt(clickMinMax[0]);
		int maxClick = Integer.parseInt(clickMinMax[1]);
		
		String[] clickIdList = args[8].split(",");
		String[] clickTypeList = args[9].split(",");

		File file = new File(keywordFilePath);

		String[] types = null;
		if (typeList != null) {
			types = typeList.split(",");
		}
		List<String>[] typeValues = new List[types.length];
		for (int i = 0; i < typeValues.length; i++) {
			int s = types[i].indexOf("<");
			int e = types[i].indexOf(">");

			String values = types[i].substring(s + 1, e);
			types[i] = types[i].substring(0, s);
			
			String[] values2 = values.split("\\|");
			typeValues[i] = new ArrayList<String>(values2.length);
			for (String v : values2) {
				typeValues[i].add(v);
			}
		}
		
		File home = new File(homePath, "statistics");
		System.out.println("generating...");
		new LogGenerator().generate(home, siteId, file, minQty, maxQty, fromDate, toDate, categoryList, types, typeValues, minClick, maxClick, clickIdList, clickTypeList);
	}
	
	public LogGenerator() {
	}
	
	private void generate(File home, String siteId, File keywordFile, int minQty, int maxQty, Calendar fromDate, Calendar toDate, String[] categoryList, String[] types, List<String>[] typeValues, int minClick, int maxClick, String[] clickIdList, String[] clickTypeList) {
		List<String> keywordList = new ArrayList<String>();
		BufferedReader keywordReader = null;
		try {
			keywordReader = new BufferedReader(new InputStreamReader(new FileInputStream(keywordFile)));
			String line = null;
			while ((line = keywordReader.readLine()) != null) {
				line = line.trim();
				keywordList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (keywordReader != null) {
				try {
					keywordReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Loaded " + keywordList.size() + " words.");
		Random r = new Random(System.nanoTime());
		
		int[] qtyGenerate = new int[24];
			
		String[] typeValue = new String[types.length];
		int count = 0;
		
		Calendar calendar = (Calendar) fromDate.clone();
		
		while (calendar.compareTo(toDate) <= 0) {
			File searchLogFile = getSearchLogFile(siteId, home, calendar);
			File typeLogFile = getTypeLogFile(siteId, home, calendar);
			File clickLogFile = getClickLogFile(siteId, home, calendar);
			
			System.out.println("searchLogFile path = " + searchLogFile.getPath());
			System.out.println("typeLogFile path = " + typeLogFile.getPath());
			System.out.println("clickLogFile path = " + clickLogFile.getPath());
			
			if(searchLogFile.exists()) {
				System.out.println("deleting.."+searchLogFile.getAbsolutePath());
				searchLogFile.delete();
			}
			if(typeLogFile.exists()) {
				System.out.println("deleting.."+typeLogFile.getAbsolutePath());
				typeLogFile.delete();
			}
			if(clickLogFile.exists()) {
				System.out.println("deleting.."+clickLogFile.getAbsolutePath());
				clickLogFile.delete();
			}
			BufferedWriter searchLogWriter = null;
			BufferedWriter typeLogWriter = null;
			BufferedWriter clickLogWriter = null;
			try{
				searchLogWriter = new BufferedWriter(new FileWriter(searchLogFile));
				typeLogWriter = new BufferedWriter(new FileWriter(typeLogFile));
				clickLogWriter = new BufferedWriter(new FileWriter(clickLogFile));
				
				//하루동안 입력될 데이터의 총갯수를 정한다.
				int qtyTotalGenerate = minQty;
				if(maxQty > minQty) {
					qtyTotalGenerate += r.nextInt(maxQty - minQty);
				}
				int qtyTotal = qtyTotalGenerate;
				int avgQty = qtyTotalGenerate / qtyGenerate.length;
				//우선은 시간별 평균값으로 분배한다.
				for(int inx=0;inx < qtyGenerate.length; inx++) {
					qtyGenerate[inx] = avgQty;
					qtyTotal -= avgQty;
				}
				qtyGenerate[0] += qtyTotal; //자투리를 더해준다.
				
				System.out.println("qtyTotal="+qtyTotalGenerate+".. / shffling..");
				
				//난수로 갯수를 섞는다.
//				r.setSeed(System.currentTimeMillis());
				int randomCount = r.nextInt(10000);
				for(int inx=0;inx< randomCount; inx++) {
					int randomInxF = r.nextInt(24);
					int randomInxT = r.nextInt(24);
					int delta = r.nextInt(qtyGenerate[randomInxF]);
					qtyGenerate[randomInxF] -= delta;
					qtyGenerate[randomInxT] += delta;
				}
				
				//for (int inx = 0; inx < qtyGenerate.length; inx++) {
				//	System.out.println("generate count["+inx+"] = "+qtyGenerate[inx]);
				//}
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				
				for(int inx=0;inx<qtyGenerate.length;inx++) {
					
					for(int inx2=0;inx2<qtyGenerate[inx];inx2++) {
		
						//searchlog
						String categoryId = "_root";
						int cateIndex = r.nextInt(categoryList.length + 1);
						if(cateIndex < categoryList.length){
							categoryId = categoryList[cateIndex];
						}
//						r.setSeed(System.currentTimeMillis());
				
						int keyword0 = r.nextInt(keywordList.size());
						int keyword1 = r.nextInt(keywordList.size());
						String keyword = keywordList.get(keyword0);
						String prevKeyword = keywordList.get(keyword1);
						for (int i = 0; i < typeValue.length; i++) {
//							r.setSeed(System.currentTimeMillis());
							typeValue[i] = typeValues[i].get(r.nextInt(typeValues[i].size()));
						}
						
						// 1/10 의 확률로 결과없음이 나온다.
						int resultCount = 0;
						if(r.nextInt(10) > 0){
							resultCount = r.nextInt(2000000);
						}
						int resptime = r.nextInt(4000) + 100;
						
						int minute = inx2 * 60 / qtyGenerate[inx];
						
						String timeId = (inx<10?"0"+inx:inx)+":"+(minute<10?"0"+minute:minute);
						
						String[] rawData = makeRawData(siteId, timeId, categoryId, keyword, prevKeyword, resultCount, resptime, types, typeValue);
						insertSearchLog(searchLogWriter, typeLogWriter, rawData);
						
						//clicklog
						int clickQty = r.nextInt(maxClick - minClick) + minClick;
						for (int clickInx = 0; clickInx < clickQty; clickInx++) {
							String productId="";
							String clickType="";
							
							productId = clickIdList[ r.nextInt(clickIdList.length) ];
							
							//if(productId.contains("_")) { //제휴사 상품타입일 경우 goshop 으로 고정
							//	clickType = clickTypeList[];
							//}
							
//							r.setSeed(System.currentTimeMillis());
							clickType = clickTypeList[ r.nextInt(clickTypeList.length) ];
							insertClickLog(clickLogWriter, timeId, rawData[3], productId, clickType);
						}
//						System.out.println(">> "+count +" " + new Date(calendar.getTimeInMillis()));
						count++;
						if(count % 10000 == 0){
							System.out.println("log " + count + "... / " + format.format(calendar.getTime()) );
						}
					}
				}
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(searchLogWriter != null) {
					try {
						searchLogWriter.close();
					} catch (IOException e) {
					}
				}
				if(typeLogWriter != null) {
					try {
						typeLogWriter.close();
					} catch (IOException e) {
					}
				}
				if(clickLogWriter != null) {
					try {
						clickLogWriter.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}
	
	private static Calendar parseDate(String dateStr) {
		Calendar calendar = StatisticsUtils.getCalendar();
		if(!(dateStr == null || "".equals(dateStr))) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssS");
			try {
				dateStr = dateStr.replaceAll("[-.:/]", "");
				for(int inx=dateStr.length();inx < 17; inx++) {
					dateStr+="0";
				}
				Date date = format.parse(dateStr);
				calendar.setTime(date);
				System.out.println("dateStr >>> " + dateStr +" : "+ date);
			} catch (ParseException ignore) {
				ignore.printStackTrace();
			}
		}
		return calendar;
	}

	private String[] makeRawData(String siteId, String categoryId, String time, String keyword, String prevKeyword, int resultCount, int resptime, String[] types, String[] typeValue) {
		String[] rawData = new String[7 + typeValue.length];
		int i = 0;
		rawData[i++] = siteId;
		rawData[i++] = categoryId;
		rawData[i++] = time;
		rawData[i++] = keyword;
		rawData[i++] = prevKeyword;
		rawData[i++] = String.valueOf(resultCount);
		rawData[i++] = String.valueOf(resptime);
		if (typeValue != null) {
			for (int j = 0; j < types.length; j++) {
				rawData[i++] = typeValue[j];
			}
		}
		
		return rawData;
	}
	
	
	
	private File getSearchLogFile(String siteId, File baseDir, Calendar calendar){
		return getLogFile(siteId, "raw.log", baseDir, calendar);
	}
	private File getTypeLogFile(String siteId, File baseDir, Calendar calendar){
		return getLogFile(siteId, "type_raw.log", baseDir, calendar);
	}
	private File getClickLogFile(String siteId, File baseDir, Calendar calendar){
		return getLogFile(siteId, "click_raw.log", baseDir, calendar);
	}
	private File getLogFile(String siteId, String fileName, File baseDir, Calendar calendar){
		File dateDir  = new File(new File(baseDir, siteId), "date");
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int date = calendar.get(Calendar.DAY_OF_MONTH);
		
		dateDir = new File(dateDir, "Y" + (year < 10 ? "0" + year : year));
		dateDir = new File(dateDir, "M" + (month < 10 ? "0" + month : month));
		dateDir = new File(dateDir, "D" + (date < 10 ? "0" + date : date));
		
		File dataDir = new File(dateDir, "data");
		
		if(!dataDir.exists()) {
			dataDir.mkdirs();
		}
		
		return new File(dataDir, fileName);
	}
	private void insertClickLog(BufferedWriter writer, String timeId, String keyword, String productId, String clickType) {
		try {	
			writer.append(timeId).append("\t") //시간
			.append(keyword).append("\t") //키워드
			.append(productId).append("\t") //상품id
			.append(clickType).append("\n"); //클릭타입
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void insertSearchLog(BufferedWriter rawLogWriter, BufferedWriter typeLogWriter, String[] rawData) {
		try {
			
			rawLogWriter.append(rawData[1]).append("\t").append(rawData[2]).append("\t")
				.append(rawData[3]).append("\t").append(rawData[4]).append("\t")
				.append(rawData[5]).append("\t").append(rawData[6]).append("\t")
				.append(rawData[7 + 4]); //서비스id 추가
			rawLogWriter.append("\n");
			rawLogWriter.flush();
			
			typeLogWriter.append(rawData[1]).append("\t").append(rawData[2]).append("\t")
				.append(rawData[3]);
			//각 타입별 로그
			for (int inx = 7; inx < rawData.length; inx++) {
				typeLogWriter.append("\t").append(rawData[inx]);
			}
			typeLogWriter.append("\n");
			typeLogWriter.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
