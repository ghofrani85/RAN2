package com.ghofrani.htw.RAN2.controller.transmission;

/**
 * Class for transmitting download information via REST controller.
 * 
 * @author Stefan Schmidt
 *
 */
public class DownloadData {

	private final long id;
	private final boolean error;
	private final String errorMessage;
	private final String url;

	/**
	 * @param id The id of the download data transmission 
	 * @param error A boolean is there an error or not
	 * @param errorMessage The error message to send if there was an error
	 * @param url The url to the data for the downlaod
	 */
	public DownloadData(long id, boolean error, String errorMessage, String url) {
		this.id = id;
		this.error = error;
		this.errorMessage = errorMessage;
		this.url = url;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

}
