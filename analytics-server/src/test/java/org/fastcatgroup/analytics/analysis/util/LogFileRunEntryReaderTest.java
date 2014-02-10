package org.fastcatgroup.analytics.analysis.util;

import java.io.File;
import java.io.IOException;

import org.fastcatgroup.analytics.analysis.FileRunEntryReader;
import org.fastcatgroup.analytics.analysis.log.KeyCountRunEntryParser;
import org.junit.Test;

public class LogFileRunEntryReaderTest {

	@Test
	public void test() throws IOException {
		String destDir = "src/test/resources/statistics/rt/test";
		File file = new File(destDir, "1.log");
		KeyCountRunEntryParser entryParser = new KeyCountRunEntryParser();
		FileRunEntryReader reader = new FileRunEntryReader(file, "utf-8", entryParser);
		
		
		while(reader.next()){
			System.out.println("> "+reader.entry());
		}
		
		reader.close();
	}

}
