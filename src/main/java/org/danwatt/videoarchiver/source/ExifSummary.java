package org.danwatt.videoarchiver.source;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.danwatt.videoarchiver.encoder.CombinedEncoder;
import org.danwatt.videoarchiver.encoder.Encoder;

import com.thebuzzmedia.exiftool.ExifTool.Tag;

public class ExifSummary {
	public void outputMakeModelList(SourceDb db) {
		Collection<Encoder> encoders = new CombinedEncoder().getEncoders();
		for (Encoder enc : encoders) {
			Map<String, MutableInt> makesAndModels = new TreeMap<String, MutableInt>();
			for (SourceItem si : db.getItems().values()) {
				if (enc.getSupportedExceptions().contains(StringUtils.substringAfterLast(si.getRelativePath(), ".").toUpperCase())) {
					String make = StringUtils.defaultString(si.getCachedExifTool().get(Tag.MAKE), "UNKNOWN");
					String model = StringUtils.defaultString(si.getCachedExifTool().get(Tag.MODEL), "UNKNOWN");
					String width = si.getCachedExifTool().get(Tag.IMAGE_WIDTH);
					String height = si.getCachedExifTool().get(Tag.IMAGE_HEIGHT);

					boolean dimensionsPresent = StringUtils.isNumeric(width) && StringUtils.isNumeric(height);
					String mp = "";
					if (dimensionsPresent) {
						int pixels = Integer.parseInt(width) * Integer.parseInt(height);
						mp = Integer.toString(pixels / (1000000)) + "mp";

					} else {
						System.out.println("Anomoly detected: " + si.getRelativePath() + " does not have image dimensions");
					}
					String mm = make + ":" + model + (dimensionsPresent ? " (" + width + " x " + height + " " + mp + ")" : "");
					if (!makesAndModels.containsKey(mm)) {
						makesAndModels.put(mm, new MutableInt());
					}
					makesAndModels.get(mm).increment();
				}
			}
			System.out.println("=== " + enc.getIdentifier() + " ===");
			for (Entry<String, MutableInt> e : makesAndModels.entrySet()) {
				System.out.println(e.getKey() + ": " + e.getValue());
			}
		}
	}
}
