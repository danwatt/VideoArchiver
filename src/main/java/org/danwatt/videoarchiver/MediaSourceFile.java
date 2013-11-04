package org.danwatt.videoarchiver;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.joda.time.DateTime;

@Data
@EqualsAndHashCode
public class MediaSourceFile {
	private String path;
	private String checksum;
	private int audioRate;
	private DateTime captureDate;
	
	private String make;
	private String model;
	private String imageDimensions;
	
	public String getFormatIdentifier() {
		return make+"/"+model+"/"+imageDimensions;
	}
}
