package org.danwatt.videoarchiver.source;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.Data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.danwatt.videoarchiver.FileHasher;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.TreeMultimap;
import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifTool.Feature;
import com.thebuzzmedia.exiftool.ExifTool.Format;
import com.thebuzzmedia.exiftool.ExifTool.Tag;

@Data
public class SourceDb {
	Logger logger = Logger.getLogger(SourceDb.class.getName());

	public static final String MEDIA_ARCHIVER_DB = "mediaArchiver.db.gz";
	public static final long QUICK_HASH_SIZE = 1024 * 128;

	protected ExifTool exifTool;
	private ObjectMapper mapper;

	private ListMultimap<String, SourceItem> items = ArrayListMultimap.create(TreeMultimap.<String, SourceItem> create());
	private transient Map<String, SourceItem> pathMapping = new HashMap<String, SourceItem>();
	private File sourcePath;
	private File dbFile;

	public SourceDb(File sourceDirectory) {
		this.sourcePath = sourceDirectory;
		dbFile = new File(sourceDirectory.getAbsolutePath() + File.separator + MEDIA_ARCHIVER_DB);
		mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new GuavaModule());
		exifTool = new ExifTool(Feature.STAY_OPEN);
	}

	public void load() throws IOException {
		if (dbFile.exists()) {
			InputStream is = new GZIPInputStream(new BufferedInputStream(new FileInputStream(dbFile)));
			try {
				items.clear();
				ListMultimap<String, SourceItem> loaded = mapper.readValue(is, mapper.getTypeFactory().constructMapLikeType(ArrayListMultimap.class, String.class, SourceItem.class));
				items.putAll(loaded);
				pathMapping.clear();
				for (Entry<String, SourceItem> e : items.entries()) {
					SourceItem v = e.getValue();
					pathMapping.put(v.getRelativePath(), v);
				}
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
		logger.info("Loaded existing source database with " + items.size() + " items indexed");
	}

	public void save() throws IOException {
		File temp = File.createTempFile(MEDIA_ARCHIVER_DB, "tmp", sourcePath);
		OutputStream os = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(temp)));

		try {
			mapper.writeValue(os, this.items);
			os.flush();
		} finally {
			IOUtils.closeQuietly(os);
		}
		dbFile.delete();
		FileUtils.moveFile(temp, dbFile);
	}

	public void scan(Collection<String> extensions) throws IOException {
		SuffixFileFilter extensionFileFilter = new SuffixFileFilter(new ArrayList<String>(extensions), IOCase.INSENSITIVE);
		Collection<File> files = FileUtils.listFiles(this.sourcePath, extensionFileFilter, FileFilterUtils.trueFileFilter());
		int updated = 0;
		logger.info("Scanning " + files.size() + " files");
		File previousDir = null;
		for (File f : files) {
			String parentPath = f.getParentFile().getAbsolutePath();
			if (null == previousDir || !previousDir.getAbsolutePath().equals(parentPath)) {
				logger.info("Scanning " + parentPath);
				previousDir = f.getParentFile();
			}
			String quickHash = FileHasher.quickHash(f, QUICK_HASH_SIZE);
			SourceItem si = new SourceItem();
			si.setLength(f.length());
			si.setQuickHash(quickHash);
			si.setRelativePath(relativePath(f));

			SourceItem existing = pathMapping.get(si.getRelativePath());
			if (null != existing && !(existing.getQuickHash().equals(si.getQuickHash()) && existing.getLength() == si.getLength())) {
				pathMapping.remove(si.getRelativePath());
				items.remove(existing.getQuickHash(), existing);
			}

			if (items.containsKey(quickHash)) {
				List<SourceItem> possibleMatches = items.get(quickHash);
				for (SourceItem possibleMatch : possibleMatches) {
					if (possibleMatch.getRelativePath().equals(si.getRelativePath())) {
						if (possibleMatch.getLength() == si.getLength()) {
							si = null;
						}
					}
				}
			}

			if (null != si) {
				add(si);
				boolean hashUpdated = ensureHashPresent(si, f);
				boolean exifUpdated = ensureExifPresent(si, f);
				if (hashUpdated || exifUpdated) {
					logger.info("Updated " + f.getAbsolutePath());
				}
				updated++;
				if (updated % 100 == 0) {// TODO: Configurable, or # of bytes
					// processed
					save();
				}
			}
		}
		save();
	}

	public String relativePath(File f) {
		return this.sourcePath.toURI().relativize(f.toURI()).getPath();
	}

	private boolean ensureExifPresent(SourceItem si, File f) throws IOException {
		if (null == si.getCachedExifTool() || si.getCachedExifTool().isEmpty()) {
			Map<Tag, String> meta = exifTool.getImageMeta(f, Format.HUMAN_READABLE, Tag.values());
			si.setCachedExifTool(meta);
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

	public void add(SourceItem item) {
		items.put(item.getQuickHash(), item);
		pathMapping.put(item.getRelativePath(), item);
	}
}
