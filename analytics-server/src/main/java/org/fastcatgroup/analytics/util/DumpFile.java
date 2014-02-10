package org.fastcatgroup.analytics.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DumpFile {
	public static void dump(File f) {
		if (f.exists()) {
			BufferedReader r = null;
			try {
				r = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

				String line = null;
				System.out.println("-------" + f.getAbsolutePath() + "--------");
				while ((line = r.readLine()) != null) {
					System.out.println(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (r != null) {
					try {
						r.close();
					} catch (IOException e) {
					}
				}
			}
		}

	}
}
