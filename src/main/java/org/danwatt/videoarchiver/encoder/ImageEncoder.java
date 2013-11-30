package org.danwatt.videoarchiver.encoder;

import java.io.File;
import java.util.Collection;

import org.apache.commons.exec.CommandLine;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.danwatt.videoarchiver.source.SourceItem;

import com.google.common.collect.Sets;

public class ImageEncoder extends Encoder {

	@Override
	public Collection<String> getSupportedExceptions() {
		return Sets.newHashSet("JPG", "TIF", "NEF", "CR2", "DNG");
	}

	@Override
	public CommandLine buildCommandLine(ArchiverConfiguration config, File sourceRoot, SourceItem sourceItem, File desitnationFile) {
		return null;
	}

	@Override
	public String getIdentifier() {
		return "image";
	}

}
