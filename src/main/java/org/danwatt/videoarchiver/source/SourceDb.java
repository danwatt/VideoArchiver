package org.danwatt.videoarchiver.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import lombok.Data;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@Data
// TODO: Just move the IO into this class. I'm not building Enterprise Java at
// home
public class SourceDb {
	private ListMultimap<String, SourceItem> items = ArrayListMultimap.create();
	private File sourcePath;

	public static final String MEDIA_ARCHIVER_DB = "mediaArchiver.db";
	private ObjectMapper mapper;
	private File dbFile;

	public SourceDb(File sourceDirectory) {
		this.sourcePath = sourceDirectory;
		dbFile = new File(sourceDirectory.getAbsolutePath() + File.separator + MEDIA_ARCHIVER_DB);
		mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new GuavaModule());
	}

	public void load() throws IOException {
		if (dbFile.exists()) {
			InputStream is = new FileInputStream(dbFile);
			try {
				items = mapper.readValue(is, ListMultimap.class);
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
	}

	public void save() throws IOException {
		OutputStream os = new FileOutputStream(dbFile);

		try {
			mapper.writeValue(os, this.items);
			os.flush();
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

	public void merge(SourceDb quickDb) {
		for (Entry<String, SourceItem> e : quickDb.getItems().entries()) {
			String quickHash = e.getKey();
			SourceItem quickScannedItem = e.getValue();
			ListMultimap<String, SourceItem> toAdd = ArrayListMultimap.create();
			if (items.containsKey(quickHash)) {
				// So, the incoming quickDb has paths, quick hashes, and sizes.
				// If a corresponding quick hash exists, and the entry matches
				// by filename and size, discard the incoming quick hash
				List<SourceItem> existingFilesWithSameQuickHash = items.get(quickHash);
				List<SourceItem> toRemove = new ArrayList<SourceItem>();
				for (SourceItem existingFile : existingFilesWithSameQuickHash) {
					if (existingFile.getRelativePath().equals(quickScannedItem.getRelativePath())) {
						if (existingFile.getLength() != quickScannedItem.getLength()) {
							toRemove.add(existingFile);
							toAdd.put(quickHash, quickScannedItem);
						}
					} else {
						toAdd.put(quickHash, quickScannedItem);
					}
				}
				existingFilesWithSameQuickHash.removeAll(toRemove);
			} else {
				toAdd.put(quickHash, quickScannedItem);
			}
			items.putAll(toAdd);
		}

	}
}
