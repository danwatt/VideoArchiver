package org.danwatt.videoarchiver;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;

public class SourceScanner {

	public MediaSource gatherSourceFiles(ArchiverConfiguration config, String sourcePath) throws IOException {
		MediaSource source = new MediaSource();
		MetadataExtractor extractor = new MetadataExtractor(config);
		SuffixFileFilter extensionFileFilter = new SuffixFileFilter(config.getIncludeExtensions(), IOCase.INSENSITIVE);
		Collection<File> files = FileUtils.listFiles(new File(sourcePath), extensionFileFilter, FileFilterUtils.trueFileFilter());
		for (File f : files) {
			source.addFile(extractor.extractMetadata(f));
		}
		return source;
	}

}
