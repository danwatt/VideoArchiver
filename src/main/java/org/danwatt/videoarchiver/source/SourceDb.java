package org.danwatt.videoarchiver.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import lombok.Data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@Data
//TODO: Just move the IO into this class. I'm not building Enterprise Java at home
public class SourceDb {
	private ListMultimap<String, SourceItem> items = ArrayListMultimap.create();
	
	public void merge(SourceDb quickDb) {
		for (Entry<String, SourceItem> e : quickDb.getItems().entries()) {
			String quickHash = e.getKey();
			SourceItem quickScannedItem = e.getValue();
			ListMultimap<String, SourceItem> toAdd = ArrayListMultimap.create();
			if (items.containsKey(quickHash)) {
				// So, the incoming quickDb has paths, quick hashes, and sizes.
				// If a corresponding quick hash exists, and the entry matches
				// by filename and size, discard the incoming quick hash
				List<SourceItem> existingFilesWithSameQuickHash = items.get(quickHash);
				List<SourceItem> toRemove = new ArrayList<SourceItem>();
				for (SourceItem existingFile : existingFilesWithSameQuickHash) {
					if (existingFile.getRelativePath().equals(quickScannedItem.getRelativePath())) {
						if (existingFile.getLength() != quickScannedItem.getLength()) {
							toRemove.add(existingFile);
							toAdd.put(quickHash, quickScannedItem);
						}
					} else {
						toAdd.put(quickHash, quickScannedItem);
					}
				}
				existingFilesWithSameQuickHash.removeAll(toRemove);
			} else {
				toAdd.put(quickHash, quickScannedItem);
			}
			items.putAll(toAdd);
		}
		
	}
}
