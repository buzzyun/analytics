package org.fastcatsearch.analytics;

import static org.junit.Assert.*;

import org.junit.Test;

public class ToyAnalyticsTest {

	@Test
	public void test() {
		
		putLog("keyword=aa&sex=male&age=25&respTime=150&login=yes");
		putLog("keyword=bb&sex=female&age=35&respTime=100&login=yes");
		putLog("keyword=aa&sex=male&age=15&respTime=50&login=yes");
		putLog("keyword=cc&sex=&age=&respTime=190&login=no");
		putLog("keyword=aa&sex=male&age=22&respTime=250&login=yes");
		putLog("keyword=dd&sex=male&age=22&respTime=350&login=yes");
		putLog("keyword=bb&sex=&age=&respTime=450&login=no");
		putLog("keyword=dd&sex=male&age=21&respTime=100&login=yes");
		putLog("keyword=cc&sex=female&age=31&respTime=50&login=yes");
		putLog("keyword=aa&sex=male&age=35&respTime=190&login=yes");
		
		analyze();
		
		print();
	}

	private void print() {
		
	}

	private void analyze() {
		
	}

	private void putLog(String string) {
		
	}

	@Test
	public void test11(){
		String line = "1	2	-	-	-	-	-	-	-";
		String[] el = line.split("\t");
		for(int i=0;i<el.length;i++){
			System.out.println(i + " : " + el[i]);
		}
	}
}
