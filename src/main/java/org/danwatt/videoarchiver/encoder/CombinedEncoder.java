package org.danwatt.videoarchiver.encoder;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang.StringUtils;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.danwatt.videoarchiver.source.SourceItem;

public class CombinedEncoder extends Encoder {

	private ImageEncoder imageEncoder = new ImageEncoder();
	private VideoEncoder videoEncoder = new VideoEncoder();
	private AudioEncoder audioEncoder = new AudioEncoder();
	private Map<String, Encoder> encoderMapping = new LinkedHashMap<String, Encoder>();

	public CombinedEncoder() {
		addMapping(imageEncoder);
		addMapping(videoEncoder);
		addMapping(audioEncoder);
	}

	private void addMapping(Encoder encoder) {
		for (String e : encoder.getSupportedExceptions()) {
			encoderMapping.put(e.toUpperCase(), encoder);
		}
	}

	@Override
	public Collection<String> getSupportedExceptions() {
		return encoderMapping.keySet();
	}

	@Override
	public CommandLine buildCommandLine(ArchiverConfiguration config, File sourceRoot, SourceItem sourceItem, File desitnationFile) {
		return encoderMapping.get(determineExtension(sourceItem)).buildCommandLine(config, sourceRoot, sourceItem, desitnationFile);
	}

	public String determineExtension(SourceItem sourceItem) {
		return StringUtils.substringAfterLast(sourceItem.getRelativePath(), ".").toUpperCase();
	}

	@Override
	public String getIdentifier() {
		return "combined";
	}

	@Override
	public boolean encode(ArchiverConfiguration config, File sourceRoot, SourceItem sourceItem, File destinationFile) {
		String ext = determineExtension(sourceItem);
		return encoderMapping.get(ext).encode(config, sourceRoot, sourceItem, destinationFile);
	}

}
