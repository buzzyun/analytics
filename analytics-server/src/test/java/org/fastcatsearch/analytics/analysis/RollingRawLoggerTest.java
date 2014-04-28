package org.fastcatsearch.analytics.analysis;

import java.io.File;
import java.util.Random;

import org.fastcatsearch.analytics.analysis.RollingRawLogger;
import org.junit.Test;

public class RollingRawLoggerTest {

	@Test
	public void test() {
		File baseDir = new File("/tmp");
		RollingRawLogger logger = new RollingRawLogger(baseDir, "total", "tmp.log");
		
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 100000; i++) {
			String[] data = new String[3];
			data[0] = "total";
			data[1] = "[" + i + "]";
			data[2] = System.currentTimeMillis() + "";
			logger.log(data);
			try {
				Thread.sleep(r.nextInt(200));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(i % (r.nextInt(100)+1) == 0){
				logger.rolling();
			}
		}
		
		logger.close();
	}

}
