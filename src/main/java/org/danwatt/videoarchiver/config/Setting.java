package org.danwatt.videoarchiver.config;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@JsonInclude(Include.NON_NULL)
public class Setting {
	private String videoBitrate;
	private String videoQualityFactor;
	private String videoScale;
	private String audioSamplerate;
	private String audioBitrate;
	private int maximumAudioChannels;
	private String advancedEncoderOptions;
}
