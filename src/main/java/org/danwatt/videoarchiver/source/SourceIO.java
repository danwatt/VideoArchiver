package org.danwatt.videoarchiver.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

public class SourceIO {
	public static final String MEDIA_ARCHIVER_DB = "mediaArchiver.db";
	private ObjectMapper mapper;
	private File dbFile;

	public SourceIO(File sourceDirectory) {
		dbFile = new File(sourceDirectory.getAbsolutePath() + File.separator + MEDIA_ARCHIVER_DB);
		mapper = new ObjectMapper();
		mapper.registerModule(new GuavaModule());
	}

	public SourceDb load() throws IOException {
		GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(dbFile));
		SourceDb db;
		try {
			db = mapper.readValue(gzis, SourceDb.class);
		} finally {
			IOUtils.closeQuietly(gzis);
		}
		return db;
	}

	public void save(SourceDb db) throws IOException {
		GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(dbFile));
		
		try {
			mapper.writeValue(gzos, db);
			gzos.flush();
		} finally {
			IOUtils.closeQuietly(gzos);
		}
	}
}
