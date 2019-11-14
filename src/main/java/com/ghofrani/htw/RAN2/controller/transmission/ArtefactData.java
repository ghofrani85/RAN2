package com.ghofrani.htw.RAN2.controller.transmission;

/**
 * Class for data transfer
 * 
 * @author Stefan Schmidt
 *
 */
public class ArtefactData {
	private Integer featureId;
	private String content;

	/**
	 * @param featureId The ID in the database for the feature
	 * @param content The content to transmit
	 */
	public ArtefactData(Integer featureId, String content) {
		this.featureId = featureId;
		this.content = content;
	}

	/**
	 * @return the featureId
	 */
	public Integer getFeatureId() {
		return featureId;
	}

	/**
	 * @param featureId
	 *            the featureId to set
	 */
	public void setFeatureId(Integer featureId) {
		this.featureId = featureId;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

}
