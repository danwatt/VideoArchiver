package org.danwatt.videoarchiver.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.danwatt.videoarchiver.encoder.AudioEncoder;
import org.danwatt.videoarchiver.encoder.ImageEncoder;
import org.danwatt.videoarchiver.encoder.VideoEncoder;

@Data
public class ArchiverConfiguration {
	private String namingConvention;
	private Map<String, List<EncoderOption>> encoderOptions = new LinkedHashMap<String, List<EncoderOption>>();

	public ArchiverConfiguration() {
		encoderOptions.put(new AudioEncoder().getIdentifier(), new ArrayList<EncoderOption>());
		encoderOptions.put(new ImageEncoder().getIdentifier(), new ArrayList<EncoderOption>());
		encoderOptions.put(new VideoEncoder().getIdentifier(), new ArrayList<EncoderOption>());
	}
}
