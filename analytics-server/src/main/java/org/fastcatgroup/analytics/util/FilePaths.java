package org.fastcatgroup.analytics.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FilePaths {
	private static final Logger logger = LoggerFactory.getLogger(FilePaths.class);
	
	private File root;
	private String id;
	private static String dataRoot = "data";
	private static String indexPrefix = "index";
	
	public FilePaths(File root){
		this.root = root;
		this.id = root.getName();
	}
	public FilePaths(File root, String id) {
		this.root = new File(root, id);
		this.id = id;
	}

	public File file() {
		return root;
	}

	public String getId(){
		return id;
	}
	
	public FilePaths shard(String shardId){
		return new FilePaths(file(dataRoot), shardId);
	}
	public File file(String... dirs) {
		File file = root;
		for (int i = 0; i < dirs.length; i++) {
			file = new File(file, dirs[i]);
		}
		return file;
	}
	
//	private String dataPath() {
//		return dataPath(0);
//	}

	private String indexPath(Object dataSequence) {
		return indexPrefix + dataSequence.toString();
	}
	
	public File dataFile() {
		return file(dataRoot);
	}
	
	public File indexDirFile(Object dataSequence) {
		return file(indexPath(dataSequence));
	}

	public File segmentFile(Object dataSequence, Object segmentId) {
		return file(indexPath(dataSequence), segmentId.toString());
	}
	
	public File revisionFile(Object dataSequence, Object segmentId, Object revisionNumber) {
		return file(indexPath(dataSequence), segmentId.toString(), revisionNumber.toString());
	}
	
}
