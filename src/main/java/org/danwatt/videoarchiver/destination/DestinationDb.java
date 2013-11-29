package org.danwatt.videoarchiver.destination;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.Data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.danwatt.videoarchiver.encoder.CombinedEncoder;
import org.danwatt.videoarchiver.source.SourceDb;
import org.danwatt.videoarchiver.source.SourceItem;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

@Data
// TODO: Split into a "DB" class and an execution class
public class DestinationDb {
	Logger logger = Logger.getLogger(DestinationDb.class.getName());

	public static final String DESTINATION_DB_FILE = "mediaArchiver-dest.db.gz";
	public static final String DESTINATION_CONFIG_FILE = "mediaArchiver.config.json";

	private ArchiverConfiguration configuration;
	// full SHA1 -> relative path
	private Map<String, String> archivedFiles = new LinkedHashMap<String, String>();

	private ObjectMapper mapper;

	private File destinationRoot;
	private File dbFile;
	private File configFile;
	private CombinedEncoder encoder = new CombinedEncoder();

	public DestinationDb(File destinationRoot) {
		this.destinationRoot = destinationRoot;
		this.dbFile = new File(this.destinationRoot.getAbsolutePath() + File.separator + DESTINATION_DB_FILE);
		this.configFile = new File(this.destinationRoot.getAbsolutePath() + File.separator + DESTINATION_CONFIG_FILE);
		mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new GuavaModule());
	}

	// Largely untested, spiking to get it done
	public void load() throws IOException {
		if (this.configFile.exists()) {
			this.configuration = mapper.readValue(this.configFile, ArchiverConfiguration.class);
		} else {
			configuration = new ArchiverConfiguration();
			mapper.writeValue(configFile, configuration);
		}
		if (dbFile.exists()) {
			InputStream is = new GZIPInputStream(new BufferedInputStream(new FileInputStream(dbFile)));
			try {
				archivedFiles = mapper.readValue(is, mapper.getTypeFactory().constructMapLikeType(LinkedHashMap.class, String.class, String.class));
			} finally {
				IOUtils.closeQuietly(is);
			}
		}
	}

	public void save() throws IOException {
		File temp = File.createTempFile(DESTINATION_DB_FILE, "tmp", destinationRoot);
		OutputStream os = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(temp)));

		try {
			mapper.writeValue(os, this.archivedFiles);
			os.flush();
		} finally {
			IOUtils.closeQuietly(os);
		}
		dbFile.delete();
		FileUtils.moveFile(temp, dbFile);
	}

	public boolean isArchived(SourceItem sourceItem) {
		return archivedFiles.containsKey(sourceItem.getHash());
	}

	protected File determineDestinationDirectory(SourceItem item) {
		return new File(destinationRoot.getAbsolutePath() + File.separator + item.getRelativePath()).getParentFile();
	}

	public boolean archive(File sourceRoot, SourceItem sourceItem) {
		if (!isArchived(sourceItem)) {
			File destDir = determineDestinationDirectory(sourceItem);
			destDir.mkdirs();

			// TODO: Possibility of collision
			File destFile = new File(destDir.getAbsolutePath() + File.separator + StringUtils.substringBeforeLast(StringUtils.substringAfterLast(sourceItem.getRelativePath(), "/"), "."));
			boolean encoded = encoder.encode(configuration, sourceRoot, sourceItem, destFile);
			if (encoded) {
				archivedFiles.put(sourceItem.getHash(), org.danwatt.videoarchiver.util.FileUtils.relativePath(sourceRoot, destFile));
			}
			return encoded;
		}
		return false;
	}

	public void archive(SourceDb source) throws IOException {
		// TODO: Dupes
		for (SourceItem si : source.getItems().values()) {
			if (archive(source.getSourcePath(), si)) {
				save();
			}
		}
	}
}
