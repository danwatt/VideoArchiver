package org.danwatt.videoarchiver.encoder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CombinedEncoder implements Encoder {

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
			encoderMapping.put(e, encoder);
		}

	}

	public Collection<String> getSupportedExceptions() {
		return encoderMapping.keySet();
	}

}
