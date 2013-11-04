package org.danwatt.videoarchiver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MediaSource {
	private Map<String,MediaSourceFile> files = new LinkedHashMap<String,MediaSourceFile>();
	
	public void addFile(MediaSourceFile file) {
		files.put(file.getChecksum(), file);
	}
	
	public MediaSourceFile getFileInformation(String sha1checksum) {
		return files.get(sha1checksum);
	}
	
	public Set<String> getChecksums() {
		return files.keySet();
	}
	
	public Map<String, MediaSourceFile> getFiles() {
		return files;
	}
	
}
