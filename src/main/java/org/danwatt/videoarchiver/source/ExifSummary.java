package org.danwatt.videoarchiver.source;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.thebuzzmedia.exiftool.ExifTool.Tag;

public class ExifSummary {
	public void outputMakeModelList(SourceDb db) {
		Set<String> makesAndModels = new TreeSet<String>();
		for (SourceItem si : db.getItems().values()) {
			String make = si.getCachedExifTool().get(Tag.MAKE.name());
			String model = si.getCachedExifTool().get(Tag.MODEL.name());
			if (StringUtils.isNotBlank(make) && StringUtils.isNotBlank(model)) {
				makesAndModels.add(make + ":" + model);
			}
		}
		System.out.println("Detected camera makes and models:");
		System.out.println(StringUtils.join(makesAndModels, "\n"));
	}
}
