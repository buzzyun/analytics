package org.fastcatsearch.analytics.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class FormatterTest {

	@Test
	public void test() {
		for(long l = 0;l < 1024*1024*1024 ; l++) {
			String str =Formatter.getFormatSize(l);
			
			System.out.println(l + " > " + str);
		}
	}
	
	@Test
	public void testLarge() {
		for(long l = 8000000000L;l > 0 ; l--) {
			String str =Formatter.getFormatSize(l);
			
			System.out.println(l + " > " + str);
		}
	}

}
