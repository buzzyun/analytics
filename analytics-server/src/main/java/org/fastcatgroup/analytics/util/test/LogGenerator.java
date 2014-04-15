package org.fastcatgroup.analytics.util.test;

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
		home = new File(home, "search");
		System.out.println("generating...");
		new LogGenerator().generate(home, siteId, file, minQty, maxQty, fromDate, toDate, categoryList, types, typeValues, minClick, maxClick, clickIdList, clickTypeList);
	}
	
	private void generate(File home, String siteId, File file, int minQty, int maxQty, Calendar fromDate, Calendar toDate, String[] categoryList, String[] types, List<String>[] typeValues, int minClick, int maxClick, String[] clickIdList, String[] clickTypeList) {
		List<String> keywordList = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				keywordList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Loaded " + keywordList.size() + " words.");
		Random r = new Random(System.nanoTime());
		
		int[] qtyGenerate = new int[24];
		
		try {
			
			String[] typeValue = new String[types.length];
			int count = 0;
			
			Calendar calendar = (Calendar) fromDate.clone();
			
			while (calendar.before(toDate)) {
				
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
				
						int keyword0 = r.nextInt(keywordList.size());
						int keyword1 = r.nextInt(keywordList.size());
						String keyword = keywordList.get(keyword0);
						String prevKeyword = keywordList.get(keyword1);
						for (int i = 0; i < typeValue.length; i++) {
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
						
						String searchData = makeData(siteId, timeId, categoryId, keyword, prevKeyword, resultCount, resptime, types, typeValue);
						insertSearchLog(home, calendar, searchData, (inx==0 && inx2==0));
						
						//clicklog
						int clickQty = r.nextInt(maxClick - minClick) + minClick;
						String[] rawData = searchData.split("\t");
						for (int clickInx = 0; clickInx < clickQty; clickInx++) {
							String productId="";
							String clickType="";
							
							productId = clickIdList[ r.nextInt(clickIdList.length) ];
							
							//if(productId.contains("_")) { //제휴사 상품타입일 경우 goshop 으로 고정
							//	clickType = clickTypeList[];
							//}
							
							clickType = clickTypeList[ r.nextInt(clickTypeList.length) ];
							insertClickLog(home, calendar, timeId, rawData[3], productId, clickType, rawData, (inx==0 && inx2==0 && clickInx==0));
						}
						count++;
						if(count % 10000 == 0){
							System.out.println("log " + count + "... / " + format.format(calendar.getTime()) );
						}
					}
				}
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
		} finally {
		}
	}
	
	private static Calendar parseDate(String dateStr) {
		Calendar calendar = Calendar.getInstance(Locale.GERMAN);
		if(!(dateStr == null || "".equals(dateStr))) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssS");
			try {
				dateStr = dateStr.replaceAll("[-.:/]", "");
				for(int inx=dateStr.length();inx < 17; inx++) {
					dateStr+="0";
				}
				Date date = format.parse(dateStr);
				calendar.setTime(date);
			} catch (ParseException ignore) {
				ignore.printStackTrace();
			}
		}
		return calendar;
	}

	private String makeData(String siteId, String categoryId, String time, String keyword, String prevKeyword, int resultCount, int resptime, String[] types, String[] typeValue) {
		
		StringBuffer sb = new StringBuffer();
		try{
			sb.append(siteId);
			sb.append("\t");
			sb.append(categoryId);
			sb.append("\t");
			sb.append(time);
			sb.append("\t");
			sb.append(keyword);
			sb.append("\t");
			sb.append(prevKeyword);
			sb.append("\t");
			sb.append(resultCount);
			sb.append("\t");
			sb.append(resptime);
			if (typeValue != null) {
				for (int i = 0; i < types.length; i++) {
					String value = typeValue[i];
					sb.append("\t");
					sb.append(value);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private void insertClickLog(File baseDir, Calendar calendar, String timeId, String keyword, String productId, String clickType, String[] rawData, boolean create) {
		BufferedWriter writer = null;
		try {
			
			File dateDir  = new File(baseDir, "date");
			
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int date = calendar.get(Calendar.DAY_OF_MONTH);
			
			dateDir = new File(dateDir, "Y"+(year<10?"0"+year:year));
			dateDir = new File(dateDir, "M"+(month<10?"0"+month:month));
			dateDir = new File(dateDir, "D"+(date<10?"0"+date:date));
			
			File dataDir = new File(dateDir, "data");
			dataDir = new File(dataDir, rawData[0]);
			
			if(!dataDir.exists()) {
				dataDir.mkdirs();
			}
			
			File file = null;
			
			//click_raw log
			file = new File(dataDir, "click_raw.log");
			//System.out.println(file.getAbsolutePath());
			if(create && file.exists()) {
				System.out.println("deleting.."+file.getAbsolutePath());
				file.delete();
				writer = new BufferedWriter(new FileWriter(file));
			} else {
				writer = new BufferedWriter(new FileWriter(file, true));
			}
			
			writer.append(timeId).append("\t") //시간
			.append(keyword).append("\t") //키워드
			.append(productId).append("\t") //상품id
			.append(clickType).append("\n"); //클릭타입
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void insertSearchLog(File baseDir, Calendar calendar, String data, boolean create) {
		//System.out.println(data);
		String[] rawData = data.split("\t");
		BufferedWriter writer = null;
		try {
			
			File dateDir  = new File(baseDir, "date");
			
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int date = calendar.get(Calendar.DAY_OF_MONTH);
			
			dateDir = new File(dateDir, "Y"+(year<10?"0"+year:year));
			dateDir = new File(dateDir, "M"+(month<10?"0"+month:month));
			dateDir = new File(dateDir, "D"+(date<10?"0"+date:date));
			
			File dataDir = new File(dateDir, "data");
			dataDir = new File(dataDir, rawData[0]);
			
			if(!dataDir.exists()) {
				dataDir.mkdirs();
			}
			
			File file = null;
			
			//raw log
			file = new File(dataDir, "raw.log");
			//System.out.println(file.getAbsolutePath());
			if(create && file.exists()) {
				System.out.println("deleting.."+file.getAbsolutePath());
				file.delete();
				writer = new BufferedWriter(new FileWriter(file));
			} else {
				writer = new BufferedWriter(new FileWriter(file, true));
			}
			writer.append(rawData[1]).append("\t").append(rawData[2]).append("\t")
				.append(rawData[3]).append("\t").append(rawData[4]).append("\t")
				.append(rawData[5]).append("\t").append(rawData[6]).append("\t")
				.append(rawData[7 + 4]); //서비스id 추가
			writer.append("\n");
			writer.flush();
			
			writer.close();
			
			//type log
			file = new File(dataDir, "type_raw.log");
			//System.out.println(file.getAbsolutePath());
			if(create && file.exists()) {
				System.out.println("deleting.."+file.getAbsolutePath());
				file.delete();
				writer = new BufferedWriter(new FileWriter(file));
			} else {
				writer = new BufferedWriter(new FileWriter(file, true));
			}
			
			writer.append(rawData[1]).append("\t").append(rawData[2]).append("\t")
				.append(rawData[3]);
			
			//각 타입별 로그
			for (int inx = 7; inx < rawData.length; inx++) {
				writer.append("\t").append(rawData[inx]);
			}
			writer.append("\n");
			writer.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
