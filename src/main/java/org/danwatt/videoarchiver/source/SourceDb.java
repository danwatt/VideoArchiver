package org.danwatt.videoarchiver.source;

import lombok.Data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

@Data
public class SourceDb {
	private ListMultimap<String, SourceItem> items = ArrayListMultimap.create();
}
