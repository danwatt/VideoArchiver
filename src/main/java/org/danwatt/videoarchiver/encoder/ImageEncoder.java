package org.danwatt.videoarchiver.encoder;

import java.io.File;
import java.util.Collection;

import org.apache.commons.exec.CommandLine;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.danwatt.videoarchiver.source.SourceItem;

import com.google.common.collect.Sets;

public class ImageEncoder implements Encoder {

	public Collection<String> getSupportedExceptions() {
		return Sets.newHashSet("jpg", "tif", "nef", "cr2", "dng");
	}

	public CommandLine buildCommandLine(ArchiverConfiguration config, SourceItem sourceItem, File desitnationFile) {
		return null;
	}

	public String getIdentifier() {
		return "image";
	}

}
