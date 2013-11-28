package org.danwatt.videoarchiver.encoder;

import java.util.Collection;

import com.google.common.collect.Sets;

public class ImageEncoder implements Encoder {

	public Collection<String> getSupportedExceptions() {
		return Sets.newHashSet("jpg", "tif", "nef", "cr2", "dng");
	}

}
