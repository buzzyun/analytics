package org.fastcatgroup.analytics.analysis;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Random;

import org.junit.Test;

public class RawDataLoggerTest {

	@Test
	public void testRandomFlush() {
		File file = new File("/tmp/a.log");
		BufferedLogger logger = new BufferedLogger(file);
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 100000; i++) {
			String[] data = new String[3];
			data[0] = "total";
			data[1] = "[" + i + "]";
			data[2] = System.currentTimeMillis() + "";
			logger.log(data);
			try {
				Thread.sleep(r.nextInt(50));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		logger.close();
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testForceFlush() {
		File file = new File("/tmp/a.log");
		BufferedLogger logger = new BufferedLogger(file);
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 100000; i++) {
			String[] data = new String[3];
			data[0] = "total";
			data[1] = "[" + i + "]";
			data[2] = System.currentTimeMillis() + "";
			logger.log(data);
			try {
				Thread.sleep(r.nextInt(50));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(i % 20 == 0){
				logger.flush();
			}
		}
		
		logger.close();
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
