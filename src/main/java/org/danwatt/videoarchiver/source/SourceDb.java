package org.danwatt.videoarchiver.source;

import lombok.Data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

@Data
public class SourceDb {
	private Multimap<String, SourceItem> items = ArrayListMultimap.create();
}
