package org.danwatt.videoarchiver.encoder;

import java.io.File;
import java.util.Collection;

import org.apache.commons.exec.CommandLine;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.danwatt.videoarchiver.source.SourceItem;

public interface Encoder {

	String getIdentifier();
	Collection<String> getSupportedExceptions();

	CommandLine buildCommandLine(ArchiverConfiguration config, SourceItem sourceItem, File desitnationFile);
}
