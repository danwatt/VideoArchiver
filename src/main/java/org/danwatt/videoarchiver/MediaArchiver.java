package org.danwatt.videoarchiver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class MediaArchiver {
	static final SuffixFileFilter MOVIE_FILE_FILTER = new SuffixFileFilter(Arrays.asList(".MOV",".AVI",".M4V"),IOCase.INSENSITIVE);
	static final ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	static {
		mapper.registerModule(new JodaModule());
	}
	public static void main(String[] args) throws IOException {
		String archivePath = args[0];
		String sourcePath = args[1];
		
		File configFile = new File(archivePath+File.separator+"videoArchive.json");
		ArchiverConfiguration config = new ArchiverConfiguration();
		if (configFile.exists()) {
			config = mapper.readValue(configFile, ArchiverConfiguration.class);
		}
		Archive archive = new Archive(new File(archivePath));
		archive.load();
//		Archive archive = gatherSourceFiles(sourcePath);
//		detectMissingConfigurations(config, archive);
	}
	private static void detectMissingConfigurations(ArchiverConfiguration config, Archive archive) throws JsonProcessingException {
		ArchiverConfiguration missingConfigs = new ArchiverConfiguration();
//		for (MediaFile mf : archive.getArchivedFiles().values()) {
//			if (!config.getSettings().containsKey(mf.getFormatIdentifier())) {
//				Setting defaultSetting = new Setting();
//				defaultSetting.getVariations().put("default", new Variation());
//				missingConfigs.getSettings().put(mf.getFormatIdentifier(), defaultSetting);
//			}
//		}
		System.out.println(mapper.writeValueAsString(missingConfigs));
	}
//	private static Archive gatherSourceFiles(String sourcePath) throws IOException {
//		Collection<File> files = FileUtils.listFiles(new File(sourcePath), MOVIE_FILE_FILTER, FileFilterUtils.trueFileFilter());
//		Archive archive = new Archive();
//		for (File f : files) {
//			MediaFile vf = new MetadataExtractor().extractMetadata(f);
//			archive.getArchivedFiles().put(vf.getOriginalChecksum(), vf);
//		}
//		return archive;
//	}
}
