package org.fastcatgroup.analytics.analysis.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fastcatgroup.analytics.analysis.EntryParser;
import org.fastcatgroup.analytics.analysis.FileRunEntryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 로그를 정렬한다. 예를들어, 키워드순으로 되어있는 로그를 count순으로 재정렬할때 필요하다.
 * comparator 구현에 따라 다른 정렬도 가능하다.
 * 
 * 기본적으로 중복키가 없다는 가정하에 정렬만 수행한다. distinct기능은 없다. 
 * 
 * 메모리에서 정렬이 힘든 대용량 정렬을 위해 runKeysize만큼만 메모리에서 정렬하고 run파일로 만든후 최종적으로 run파일을 머징하여 정렬된 OutputStream을 내보낸다.
 * */
public class LogSorter<EntryType extends RunEntry> {
	protected static Logger logger = LoggerFactory.getLogger(LogSorter.class);

	private int runKeySize;
	private String encoding;
	private InputStream is;

	public LogSorter(InputStream is, String encoding, int runKeySize) {
		this.is = is;
		this.encoding = encoding;
		this.runKeySize = runKeySize;
	}

	public void sort(OutputStream os, EntryParser<EntryType> entryParser, Comparator<EntryType> comparator, File workDir) throws IOException {
		if (!workDir.exists()) {
			workDir.mkdir();
		}
		
		try{
			List<EntryType> list = new ArrayList<EntryType>(runKeySize);
			int flushCount = 0;
			FileRunEntryReader<EntryType> entryReader = new FileRunEntryReader<EntryType>(is, encoding, entryParser);
			try {
				while (entryReader.next()) {
					EntryType entry = entryReader.entry();
					// logger.debug(">>> {}", entry);
					if (entry != null) {
						list.add(entry);
	
						if (list.size() >= runKeySize) {
							flush(workDir, flushCount++, list, comparator);
	
						}
					}
				}
	
			} catch (IOException e) {
				entryReader.close();
			}
	
			if (list.size() > 0) {
				flush(workDir, flushCount++, list, comparator);
			}
	
			/*
			 * 2. run들을 하나로 합친다.
			 */
			File[] runFileList = new File[flushCount];
			for (int i = 0; i < flushCount; i++) {
				runFileList[i] = getRunFile(workDir, i);
			}
			List<RunEntryReader<EntryType>> entryReaderList = getReaderList(runFileList, entryParser);
	
			if (entryReaderList.size() > 0) {
				RunEntryMergeReader<EntryType> mergeReader = new RunEntryMergeReader<EntryType>(entryReaderList, comparator);
	
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, encoding));
				try {
					EntryType entry = null;
	
					while ((entry = mergeReader.read()) != null) {
						writer.write(entry.getRawLine());
						writer.write("\n");
					}
	
				} finally {
					for (RunEntryReader<EntryType> r : entryReaderList) {
						r.close();
					}
	
					writer.close();
					
				}
			}
		}finally{
			FileUtils.deleteQuietly(workDir);			
		}
	}

	private void flush(File workDir, int flushCount, List<EntryType> list, Comparator<EntryType> comparator) throws IOException {
		File runFile = getRunFile(workDir, flushCount);
		BufferedWriter writer = null;
		try {
			//if(runFile.exists()) {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(runFile), encoding));
				Collections.sort(list, comparator);
				for (EntryType entry : list) {
					writer.write(entry.getRawLine());
					writer.write("\n");
				}
			//}
		} finally {
			if (writer != null) {
				writer.close();
			}
			list.clear();
		}
	}

	private File getRunFile(File workDir, int i) {
		return new File(workDir, Integer.valueOf(i) + ".run");
	}

	private List<RunEntryReader<EntryType>> getReaderList(File[] fileList, EntryParser<EntryType> entryParser) throws IOException {
		List<RunEntryReader<EntryType>> list = new ArrayList<RunEntryReader<EntryType>>();
		for (int i = 0; i < fileList.length; i++) {
			File f = fileList[i];
			if (f.exists()) {
				FileRunEntryReader<EntryType> r = new FileRunEntryReader<EntryType>(f, encoding, entryParser);
				r.next();
				list.add(r);
			}
		}
		return list;
	}
}
