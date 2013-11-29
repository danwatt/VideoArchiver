package org.danwatt.videoarchiver.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.danwatt.videoarchiver.source.SourceItem;

import com.thebuzzmedia.exiftool.ExifTool.Tag;

public class ArgumentBuilder {

	public Map<String, String> buildArguments(List<EncoderOption> options, SourceItem item) {
		Map<String, String> commandLineArguments = new LinkedHashMap<String, String>();
		for (EncoderOption option : options) {
			boolean match = true;
			for (Entry<Tag, String> toMatch : option.getMatch().entrySet()) {
				if (!item.getCachedExifTool().containsKey(toMatch.getKey()) || !StringUtils.equals(item.getCachedExifTool().get(toMatch.getKey()), toMatch.getValue())) {
					match = false;
				}
			}

			if (match) {
				commandLineArguments.putAll(option.getAdd());
			}
		}
		return commandLineArguments;
	}
}
