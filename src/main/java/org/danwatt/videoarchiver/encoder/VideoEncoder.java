package org.danwatt.videoarchiver.encoder;

import java.util.Collection;

import com.google.common.collect.Sets;

public class VideoEncoder implements Encoder {
	public Collection<String> getSupportedExceptions() {
		return Sets.newHashSet("m4v", "qt", "mov", "mpg", "avi");
	}
}
