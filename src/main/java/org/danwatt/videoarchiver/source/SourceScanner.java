package org.danwatt.videoarchiver.source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.danwatt.videoarchiver.FileHasher;

public class SourceScanner {
	public static final long QUICK_HASH_SIZE = 1024*128;

	public SourceScanner() {
		
	}

	public SourceDb scan(File sourceRoot, Collection<String> extensions) throws IOException {
		SourceDb db = new SourceDb();
		SuffixFileFilter extensionFileFilter = new SuffixFileFilter(new ArrayList<String>(extensions), IOCase.INSENSITIVE);
		Collection<File> files = FileUtils.listFiles(sourceRoot, extensionFileFilter, FileFilterUtils.trueFileFilter());
		for (File f : files) {
			String quickHash = FileHasher.quickHash(f, SourceScanner.QUICK_HASH_SIZE);
			SourceItem si = new SourceItem();
			si.setLength(f.length());
			si.setQuickHash(quickHash);
			si.setRelativePath(sourceRoot.toURI().relativize(f.toURI()).getPath());
			db.getItems().put(quickHash, si);
		}
		return db;
		
	}
	
}
