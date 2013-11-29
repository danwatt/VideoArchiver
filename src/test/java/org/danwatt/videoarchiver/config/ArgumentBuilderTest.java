package org.danwatt.videoarchiver.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.danwatt.videoarchiver.source.SourceItem;
import org.junit.Before;
import org.junit.Test;

import com.thebuzzmedia.exiftool.ExifTool.Tag;

/**
 * Sample from my D300: "COLOR_SPACE" : "Adobe RGB", "CONTRAST" : "Normal",
 * "DATE_TIME_ORIGINAL" : "2011:10:04 10:17:55", "DIGITAL_ZOOM_RATIO" : "1",
 * "EXPOSURE_COMPENSATION" : "0", "EXPOSURE_PROGRAM" : "Aperture-priority AE",
 * "EXPOSURE_TIME" : "1/60", "FLASH" : "On, Return detected", "FOCAL_LENGTH" :
 * "70.0 mm", "FOCAL_LENGTH_35MM" : "105 mm", "IMAGE_HEIGHT" : "2868",
 * "IMAGE_WIDTH" : "4352", "ISO" : "400", "MAKE" : "NIKON CORPORATION",
 * "METERING_MODE" : "Spot", "MODEL" : "NIKON D300", "ORIENTATION" :
 * "Rotate 270 CW", "SATURATION" : "Normal", "SENSING_METHOD" :
 * "One-chip color area", "SHARPNESS" : "Hard", "SOFTWARE" : "Ver.1.00",
 * "WHITE_BALANCE" : "Auto", "X_RESOLUTION" : "300", "Y_RESOLUTION" : "300"
 * 
 */
public class ArgumentBuilderTest {

	private SourceItem source;
	private ArgumentBuilder builder = new ArgumentBuilder();
	private ArrayList<EncoderOption> options;

	@Before
	public void setup() {
		source = new SourceItem();
		source.setCachedExifTool(new LinkedHashMap<Tag, String>());
		options = new ArrayList<EncoderOption>();
	}

	@Test
	public void noOptions() {
		source.getCachedExifTool().put(Tag.MAKE, "NIKON CORPORATION");
		Map<String, String> args = builder.buildArguments(options, source);
		assertTrue(args.isEmpty());
	}

	@Test
	public void singleMatch() {
		source.getCachedExifTool().put(Tag.MAKE, "NIKON CORPORATION");
		options.add(EncoderOption.singleAdd(Tag.MAKE, "NIKON CORPORATION", "s", "hd480"));
		Map<String, String> args = builder.buildArguments(options, source);
		assertEquals(1, args.size());
		assertEquals("hd480", args.get("s"));
	}

	@Test
	public void twoMatchesOnSameParametersSecondOneWins() {
		source.getCachedExifTool().put(Tag.MAKE, "NIKON CORPORATION");
		options.add(EncoderOption.singleAdd(Tag.MAKE, "NIKON CORPORATION", "s", "hd480"));
		options.add(EncoderOption.singleAdd(Tag.MAKE, "NIKON CORPORATION", "s", "hd720"));
		Map<String, String> args = builder.buildArguments(options, source);
		assertEquals(1, args.size());
		assertEquals("hd720", args.get("s"));
	}

	@Test
	public void matchOnTwoFields() {
		source.getCachedExifTool().put(Tag.MAKE, "NIKON CORPORATION");
		source.getCachedExifTool().put(Tag.MODEL, "NIKON D300");

		options.add(EncoderOption.singleAdd(Tag.MAKE, "NIKON CORPORATION", "s", "hd480").addMatch(Tag.MODEL, "NIKON D300"));

		Map<String, String> args = builder.buildArguments(options, source);

		assertEquals(1, args.size());
		assertEquals("hd480", args.get("s"));
	}
}
