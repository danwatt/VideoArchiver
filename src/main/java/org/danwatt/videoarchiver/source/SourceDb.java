package org.danwatt.videoarchiver.source;

import lombok.Data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@Data
public class SourceDb {
	private ListMultimap<String, SourceItem> items = ArrayListMultimap.create();
}
