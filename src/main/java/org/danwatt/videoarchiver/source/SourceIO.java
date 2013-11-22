package org.danwatt.videoarchiver.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

public class SourceIO {
	public static final String MEDIA_ARCHIVER_DB = "mediaArchiver.db";
	private ObjectMapper mapper;
	private File dbFile;

	public SourceIO(File sourceDirectory) {
		dbFile = new File(sourceDirectory.getAbsolutePath() + File.separator + MEDIA_ARCHIVER_DB);
		mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new GuavaModule());
	}

	public SourceDb load() throws IOException {
		InputStream is =new FileInputStream(dbFile);
		SourceDb db;
		try {
			db = mapper.readValue(is, SourceDb.class);
		} finally {
			IOUtils.closeQuietly(is);
		}
		return db;
	}

	public void save(SourceDb db) throws IOException {
		OutputStream os = new FileOutputStream(dbFile);

		try {
			mapper.writeValue(os, db);
			os.flush();
		} finally {
			IOUtils.closeQuietly(os);
		}
	}
}
