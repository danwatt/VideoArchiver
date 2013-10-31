package org.danwatt.videoarchiver;

import lombok.Data;

import org.joda.time.DateTime;

@Data
public class VideoFile {
	private String fullPath;
	private String sha1sum;
	private int audioRate;
	private long originalSize;
	private String make;
	private String model;
	private String imageDimensions;
	private DateTime captureDate;
}
