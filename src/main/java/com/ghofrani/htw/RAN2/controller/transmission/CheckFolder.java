package com.ghofrani.htw.RAN2.controller.transmission;

/**
 * Class for transmitting folder status information via the REST controller.
 * 
 * @author Daniel Wahlmann
 *
 */
public class CheckFolder {

	private final long id;
	private final String content;

	/**
	 * Constructor.
	 * 
	 * @param id id of check
	 * @param content content of check
	 */
	public CheckFolder(long id, String content) {
		this.id = id;
		this.content = content;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}

}
