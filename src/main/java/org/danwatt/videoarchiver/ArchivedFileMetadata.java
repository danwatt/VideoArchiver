package org.danwatt.videoarchiver;

import lombok.Data;

import org.joda.time.DateTime;

@Data
public class ArchivedFileMetadata {
	private String originalName;
	private String archivedRelativePath;
	private DateTime archivedAt;
	private String sha1sum;
}
