package org.danwatt.videoarchiver.encoder;

import java.io.File;
import java.util.Collection;

import org.apache.commons.exec.CommandLine;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.danwatt.videoarchiver.source.SourceItem;

import com.google.common.collect.Sets;

public class AudioEncoder implements Encoder {

	public Collection<String> getSupportedExceptions() {
		return Sets.newHashSet("mp3", "m4a", "wav");
	}

	public CommandLine buildCommandLine(ArchiverConfiguration config, SourceItem sourceItem, File desitnationFile) {
		return null;
	}

	public String getIdentifier() {
		return "audio";
	}

}
