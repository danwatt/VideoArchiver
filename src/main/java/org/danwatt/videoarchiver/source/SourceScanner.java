package org.danwatt.videoarchiver.source;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.danwatt.videoarchiver.FileHasher;

import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifTool.Tag;

public class SourceScanner {
	public static final long QUICK_HASH_SIZE = 1024 * 128;
	protected ExifTool exifTool = new ExifTool();

	public SourceScanner() {
	}

	public SourceDb quickScan(File sourceRoot, Collection<String> extensions) throws IOException {
		SourceDb db = new SourceDb(sourceRoot);
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

	public void fillInMissingData(File sourceRoot, SourceDb db) throws IOException {
		for (Entry<String, SourceItem> e : db.getItems().entries()) {
			SourceItem si = e.getValue();
			File f = resolveFile(sourceRoot, si);
			boolean hashUpdated = ensureHashPresent(si, f);
			boolean exifUpdated = ensureExifPresent(si, f);
			if (hashUpdated || exifUpdated) {
				System.out.println("Updated " + f.getAbsolutePath());
			}
		}
	}

	private boolean ensureExifPresent(SourceItem si, File f) throws IOException {
		if (null == si.getCachedExifTool() || si.getCachedExifTool().isEmpty()) {
			Map<Tag, String> meta = exifTool.getImageMeta(f, Tag.values());
			Map<String, String> converted = new TreeMap<String, String>();
			for (Entry<Tag, String> e : meta.entrySet()) {
				converted.put(e.getKey().name(), e.getValue());
			}
			si.setCachedExifTool(converted);
			return true;
		}
		return false;
	}

	private boolean ensureHashPresent(SourceItem si, File f) throws IOException {
		if (StringUtils.isBlank(si.getHash())) {
			si.setHash(FileHasher.hashFile(f));
			return true;
		}
		return false;
	}

	private File resolveFile(File sourceRoot, SourceItem si) {
		return new File(sourceRoot.getAbsolutePath() + File.separator + si.getRelativePath());
	}

}
