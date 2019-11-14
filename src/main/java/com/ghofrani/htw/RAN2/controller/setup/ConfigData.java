package com.ghofrani.htw.RAN2.controller.setup;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * 
 * Stores and provides the custom configuration data for the application.
 * 
 * @author Daniel Wahlmann
 *
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = "configdata")
public class ConfigData {

	/**
	 * The path to the ffmpeg executable.
	 */
	@NotEmpty
	private String ffmpegPath;

	/**
	 * The path to the ffprobe executable.
	 */
	@NotEmpty
	private String ffprobePath;

	/**
	 * The maximum data volume a user can upload each day.
	 */
	@NotNull
	private String dailyUploadLimit;

	/**
	 * A list of language tags.
	 */
	@NotEmpty
	private List<String> supportedLanguages;

	/**
	 * The path to the welcome text.
	 */
	@NotEmpty
	private String welcomeTextPath;

	/**
	 * The path to the terms of service text.
	 */
	@NotEmpty
	private String tosTextPath;

	/**
	 * The path to the folder used for storing assets.
	 */
	@NotNull
	private String assetPath;

	/**
	 * The path to the folder for temporary files.
	 */
	@NotNull
	private String tempPath;

	/**
	 * @return the ffmpegPath
	 */
	public String getFfmpegPath() {
		return ffmpegPath;
	}

	/**
	 * @param ffmpegPath
	 *            the ffmpegPath to set
	 */
	public void setFfmpegPath(String ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}

	/**
	 * @return the ffprobePath
	 */
	public String getFfprobePath() {
		return ffprobePath;
	}

	/**
	 * @param ffprobePath
	 *            the ffprobePath to set
	 */
	public void setFfprobePath(String ffprobePath) {
		this.ffprobePath = ffprobePath;
	}

	/**
	 * @return the supportedLanguages
	 */
	public List<String> getSupportedLanguages() {
		return supportedLanguages;
	}

	/**
	 * @return the dailyUploadLimit
	 */
	public String getDailyUploadLimit() {
		return dailyUploadLimit;
	}

	/**
	 * @param dailyUploadLimit
	 *            the dailyUploadLimit to set
	 */
	public void setDailyUploadLimit(String dailyUploadLimit) {
		this.dailyUploadLimit = dailyUploadLimit;
	}

	/**
	 * Returns the daily upload limit in byte.
	 * 
	 * @return the Limit in bytes
	 */
	public long getDailyUploadLimitInBytes() {
		String unit = dailyUploadLimit.substring(dailyUploadLimit.length() - 2);

		long limit = Integer.parseInt(dailyUploadLimit.substring(0, dailyUploadLimit.length() - 2));

		switch (unit) {
		case "kb":
		case "KB":
		case "kB":
		case "Kb":
			limit *= 1000;
			break;
		case "MB":
		case "mb":
		case "mB":
		case "Mb":
			limit *= 1000 * 1000;
			break;
		case "gb":
		case "gB":
		case "Gb":
		case "GB":
			limit *= 1000 * 1000 * 1000;
			break;
		default:
			break;
		}

		return limit;
	}

	/**
	 * @param supportedLanguages
	 *            the supportedLanguages to set
	 */
	public void setSupportedLanguages(List<String> supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	/**
	 * @return the welcomeTextPath
	 */
	public String getWelcomeTextPath() {
		return welcomeTextPath;
	}

	/**
	 * @param welcomeTextPath
	 *            the welcomeTextPath to set
	 */
	public void setWelcomeTextPath(String welcomeTextPath) {
		this.welcomeTextPath = welcomeTextPath;
	}

	/**
	 * @return the tosTextPath
	 */
	public String getTosTextPath() {
		return tosTextPath;
	}

	/**
	 * @param tosTextPath
	 *            the tosTextPath to set
	 */
	public void setTosTextPath(String tosTextPath) {
		this.tosTextPath = tosTextPath;
	}

	/**
	 * @return the assetPath
	 */
	public String getAssetPath() {
		return assetPath;
	}

	/**
	 * @param assetPath
	 *            the assetPath to set
	 */
	public void setAssetPath(String assetPath) {
		this.assetPath = assetPath;
	}

	/**
	 * @return the tempPath
	 */
	public String getTempPath() {
		return tempPath;
	}

	/**
	 * @param tempPath
	 *            the tempPath to set
	 */
	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

}
