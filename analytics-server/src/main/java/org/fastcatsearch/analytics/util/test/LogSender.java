package org.fastcatsearch.analytics.util.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class LogSender {
	
	
	
	public static void main(String[] args) {
		if (args.length != 7) {
			System.out.println("Usage: LogGenerator [uri] [site id] [keyword file path] [duration minutes] [max interval millis] [category list] [type list]");
			System.out.println("   ex: > LogGenerator http://localhost:8050/service/keyword/hit/post.json total ../keyword.txt 10 200 cat1,cat2 category<cat1|cate2>,page<1|2|3|4|5>,sort<score|date|price>,age<0|10|20|30|40|50>,service<autocomplete|catelist>,login<Y|N>,gender<M|F|U>");
			System.exit(1);
		}
		String uri = args[0];
		String siteId = args[1];
		String keywordFilePath = args[2];
		int duration = Integer.parseInt(args[3]); // 분.
		int maxInterval = Integer.parseInt(args[4]); // 로그 생성 간격 (ms).
		String[] categoryList = args[5].split(",");
		String typeList = args[6]; // category,page,sort,age,service,login,gender

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
		

		new LogSender().generate(uri, siteId, file, duration, maxInterval, categoryList, types, typeValues);
	}

	
	
	private void generate(String uri, String siteId, File file, long duration, int maxInterval, String[] categoryList, String[] types, List<String>[] typeValues) {
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
		long durationNanotime = duration * 60 * 1000 * 1000000;
		long st = System.nanoTime();
		
		HttpClientBuilder builder = HttpClientBuilder.create();
		CloseableHttpClient httpClient = null;
		try {
			httpClient = builder.build();

			String[] typeValue = new String[types.length];
			int count = 0;
			while (true) {
				long lap = System.nanoTime() - st;
				if(lap >  durationNanotime){
					System.out.println("Generation finished. " + count+ " logs inserted during " + duration + " minutes. lap = " + lap+ " : "+ durationNanotime);
					break;
				}

				long interval = r.nextInt(maxInterval) + 100;

				Thread.sleep(interval);
				
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
				
				
				String url = makeUri(uri, siteId, categoryId, keyword, prevKeyword, resultCount, resptime, types, typeValue);
				
				insertLog(httpClient, url);
				count++;
				
				if(count % 100 == 0){
					System.out.println("log " + count + "...");
				}
				

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if(httpClient != null){
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private String makeUri(String uri, String siteId, String categoryId, String keyword, String prevKeyword, int resultCount, int resptime, String[] types, String[] typeValue) {
		
		StringBuffer sb = new StringBuffer();
		try{
		sb.append(uri);
		sb.append("?type=search&siteId=");
		sb.append(siteId);
		sb.append("&categoryId=");
		sb.append(categoryId);
		
		sb.append("&keyword=");
		sb.append(URLEncoder.encode(keyword, "utf-8"));
		
		sb.append("&prev=");
		sb.append(URLEncoder.encode(prevKeyword, "utf-8"));
		
		sb.append("&resultCount=");
		sb.append(resultCount);
		
		sb.append("&resptime=");
		sb.append(resptime);
		
		if (typeValue != null) {
			for (int i = 0; i < types.length; i++) {
				String type = types[i];
				String value = typeValue[i];
				sb.append("&");
				sb.append(type);
				sb.append("=");
				sb.append(URLEncoder.encode(value, "utf-8"));
			}
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}

	private void insertLog(CloseableHttpClient httpClient, String url) {
		System.out.println(url);
		HttpUriRequest request = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			
			if(statusCode != 200){
				String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
				System.err.println("Error >> " + responseString);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				EntityUtils.consume(response.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
