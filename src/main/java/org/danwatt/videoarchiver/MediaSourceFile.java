package org.danwatt.videoarchiver;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.joda.time.DateTime;

@Data
@EqualsAndHashCode
public class MediaSourceFile {
	private String originalName;
	private String originalChecksum;
	private int audioRate;
	private long originalSize;
	private DateTime captureDate;
	
	private String make;
	private String model;
	private String imageDimensions;
	
	public String getFormatIdentifier() {
		return make+"/"+model+"/"+imageDimensions;
	}
}
