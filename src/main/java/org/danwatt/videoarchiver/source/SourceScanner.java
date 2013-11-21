package org.danwatt.videoarchiver.source;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

public class SourceScanner {
	public SourceScanner() {
		
	}

	public SourceDb scan(File sourceRoot, Set<String> extensions) {
		SourceDb db = new SourceDb();
		SuffixFileFilter extensionFileFilter = new SuffixFileFilter(new ArrayList<String>(extensions), IOCase.INSENSITIVE);
		Collection<File> files = FileUtils.listFiles(sourceRoot, extensionFileFilter, FileFilterUtils.trueFileFilter());
		for (File f : files) {
			db.getItems().put("", new SourceItem());
		}
		return db;
		
	}
	
}
