package org.danwatt.videoarchiver.config;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifTool.Tag;

@Data
@JsonInclude(Include.NON_EMPTY)
public class EncoderOption {
	private Map<Tag, String> match = new EnumMap<ExifTool.Tag, String>(Tag.class);
	private Map<String, String> add = new LinkedHashMap<String, String>();

	public static EncoderOption singleAdd(Tag field, String value, String argumentName, String argumentVaue) {
		EncoderOption n = new EncoderOption();
		n.match.put(field, value);
		n.add.put(argumentName, argumentVaue);
		return n;
	}

	public EncoderOption addMatch(Tag tag, String value) {
		match.put(tag, value);
		return this;
	}
}
