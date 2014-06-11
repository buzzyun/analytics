/*
 * Copyright 2013 Websquared, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fastcatsearch.analytics.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 객체 / 문자열의 상호변환 등을 위한 유틸
 *
 */
public class Formatter {
	
	/** 파싱을 위한 날자포맷.**/
	public static final SimpleDateFormat DATEFORMAT_DEFAULT_PARSE = new SimpleDateFormat("yyyyMMddHHmmssS");
	public static final SimpleDateFormat YYYYMMDD_DATEFORMAT = new SimpleDateFormat("yyyyMMdd");
	/** 포맷팅을 위한 날자포맷. **/
	public static final SimpleDateFormat DATEFORMAT_DEFAULT_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	public static final SimpleDateFormat DATEFORMAT_DEFAULT_FORMAT_MIN = new SimpleDateFormat("yyyy.MM.dd HH:mm");
	/** 날자 중의 특수기호들을 삭제하기 위한 패턴 **/
	public static final Pattern PTN_STRIP_DATE = Pattern.compile("[-\t :.,/]");
//	public static final Pattern PTN_PARSE_DATE = Pattern.compile("[0-9]{4}+[-\t :.,/][0-9]{1-2}+[-\t :.,/][0-9]{1-2}+[-\t :.,/][0-9]{1-2}");

	/**
	 * 문자를 파싱하여 Date 객체로 반환한다.
	 * 반드시 년도(4자리) 월(2자리) 일(2자리) 시간(2자리) 분(2자리) 초(자리) 밀리초(3자리) 순으로 들어와야 하며
	 * 앞에서부터 순서대로라면 일부만 들어와도 파싱을 시도한다. (예를들면 20121010 등)
	 * @param data
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String data, Date defaultValue) {
		try {
			return parseDate(data);
		} catch (ParseException e) {
		}
		return defaultValue;
	}
	public static Date parseDate(String data) throws ParseException {
		data=PTN_STRIP_DATE.matcher(data).replaceAll("");
		//일부만 들어와도 되도록 자리맞춤을 위한 0 채움
		for(int strlen=data.length(); strlen < 17; strlen++) { data+="0"; }
		return DATEFORMAT_DEFAULT_PARSE.parse(data);
	}
	
	/**
	 * 날자를 문자로 변환하여 반환한다.
	 * 형식은 yyyy-MM-dd HH:mm:ss.S
	 * @param date
	 * @return
	 */
	public static String formatDate() {
		return formatDate(new Date());
	}
	public static String formatDate(Date date) {
		return DATEFORMAT_DEFAULT_FORMAT.format(date);
	}
	public static String formatYYYYMMDD(Date date) {
		return YYYYMMDD_DATEFORMAT.format(date);
	}
	public static String formatDateEndsMinute(Date date) {
		return DATEFORMAT_DEFAULT_FORMAT_MIN.format(date);
	}
	
	private static String CONTROL_CHAR_REGEXP = "["+(char)0+" "+(char)1+" "+(char)2+" "+(char)3+" "+(char)4+" "+(char)5+" "
	+(char)6+" "+(char)7+" "+(char)8+" "+(char)11+" "+(char)12+" "+(char)14+" "+(char)15+" "+(char)16+" "+(char)17+" "
	+(char)18+" "+(char)19+" "+(char)20+" "+(char)21+" "+(char)22+" "+(char)23+" "+(char)24+" "+(char)25+" "+(char)26+" "
	+(char)27+" "+(char)28+" "+(char)29+" "+(char)30+" "+(char)31+"]";
	
	public static String getFormatTime(long t){
		if(t > 1000){
			float a = (float) (t / 1000);
			if(a > 60){
				float b = a / 60;
				
				if(b > 60){
					float c = b / 60;
					return String.format("%.1f h", c);
				}else{
					return String.format("%.1f m", b);
				}
				
			}else{
				return String.format("%.1f s", a);
			}
		}else{
			return t +" ms";
		}
	}
	
	public static String getFormatSize(long s){
		
		if(s > 1000){
			float a = (float) (s / 1000f);
			
			if(a > 1000){
				float b = a / 1000f;
				
				if(b > 1000){
					float c = b / 1000f;
					return String.format("%.1fG", c);
				}else{
					return String.format("%.1fM", b);
				}
				
			}else{
				return String.format("%.1fK", a);
			}
		}else{
			return s +"B";
		}
	}
	
	public static String removeControlChars(String value){
		return value.replaceAll(CONTROL_CHAR_REGEXP, " ");
	}
	
}
