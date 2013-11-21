package org.danwatt.videoarchiver.source;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class SourceItem {
	private String quickHash;
	private String hash;
	private long length;
	private String relativePath;
	private String cachedExifTool;
}
