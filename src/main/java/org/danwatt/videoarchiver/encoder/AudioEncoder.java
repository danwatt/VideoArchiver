package org.danwatt.videoarchiver.encoder;

import java.util.Collection;

import com.google.common.collect.Sets;

public class AudioEncoder implements Encoder {

	public Collection<String> getSupportedExceptions() {
		return Sets.newHashSet("mp3", "m4a", "wav");
	}

}
