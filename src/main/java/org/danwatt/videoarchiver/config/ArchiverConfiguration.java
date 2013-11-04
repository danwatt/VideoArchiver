package org.danwatt.videoarchiver.config;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ArchiverConfiguration {
	private String namingConvention;
	private Map<String,Setting> settings = new LinkedHashMap<String, Setting>();
}
