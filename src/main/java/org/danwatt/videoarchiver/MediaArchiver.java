package org.danwatt.videoarchiver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class MediaArchiver {
	static final SuffixFileFilter MOVIE_FILE_FILTER = new SuffixFileFilter(Arrays.asList(".MOV", ".AVI", ".M4V"), IOCase.INSENSITIVE);
	static final ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).configure(
			SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	static {
		mapper.registerModule(new JodaModule());
	}

	public static void main(String[] args) throws IOException {
		String archivePath = args[0];
		String sourcePath = args[1];

		File configFile = new File(archivePath + File.separator + "videoArchive.json");
		ArchiverConfiguration config = new ArchiverConfiguration();
		if (configFile.exists()) {
			config = mapper.readValue(configFile, ArchiverConfiguration.class);
		}
		Archive archive = new Archive(new File(archivePath));
		archive.load();
		MediaSource source = gatherSourceFiles(sourcePath);
		System.out.println("The archive currently has " + archive.getChecksums().size() + " archived files");
		System.out.println("Source has " + source.getChecksums().size() + " files");
		Set<String> checksumsToProcess = new LinkedHashSet<String>();
		checksumsToProcess.addAll(source.getChecksums());
		checksumsToProcess.removeAll(archive.getChecksums());
		System.out.println("There are a total of " + checksumsToProcess.size() + " unique files to archive");
	}

	private static MediaSource gatherSourceFiles(String sourcePath) throws IOException {
		Collection<File> files = FileUtils.listFiles(new File(sourcePath), MOVIE_FILE_FILTER, FileFilterUtils.trueFileFilter());
		MediaSource source = new MediaSource();
		for (File f : files) {
			MediaSourceFile vf = new MetadataExtractor().extractMetadata(f);
			source.addFile(vf);
		}
		return source;
	}
}
