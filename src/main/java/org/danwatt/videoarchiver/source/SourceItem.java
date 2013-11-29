package org.danwatt.videoarchiver.source;

import java.util.EnumMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifTool.Tag;

@Data
@EqualsAndHashCode
public class SourceItem implements Comparable<SourceItem> {
	private String quickHash;
	private String hash;
	private long length;
	private String relativePath;
	private Map<Tag, String> cachedExifTool = new EnumMap<ExifTool.Tag, String>(Tag.class);

	public int compareTo(SourceItem o) {
		return relativePath.compareTo(o.relativePath);
	}
}
