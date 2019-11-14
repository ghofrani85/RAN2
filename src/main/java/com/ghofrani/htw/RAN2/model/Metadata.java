package com.ghofrani.htw.RAN2.model;

/**
 * Simple Key,Value Pair
 * 
 * @author Jannik
 *
 */
public class Metadata {

	private String label;
	private String content;

	/**
	 * Constructor for a metadata object containing a key, value pair
	 * 
	 * @param label
	 *            Key String
	 * @param content
	 *            Value String
	 */
	public Metadata(String label, String content) {
		this.setLabel(label);
		this.setContent(content);
	}

	/**
	 * Getter method for content
	 * 
	 * @return the content String
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Setter method for content
	 * 
	 * @param content Content for this metadata
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Getter Method for label string
	 * 
	 * @return The label string
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Setter Method for the Label
	 * 
	 * @param label
	 *            The new label string
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}
