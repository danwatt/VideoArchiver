package org.danwatt.videoarchiver.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ArchiverConfiguration {
	private String ffmpegPath = "";
	private String exifToolPath = "";
	private String namingConvention;
	private List<String> includeExtensions;
	private Map<String, Setting> settings = new LinkedHashMap<String, Setting>();

	public Setting getSetting(String string) {
		return settings.get(string);
	}
}
