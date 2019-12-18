package com.ghofrani.htw.RAN2.controller.transmission;

/**
 * Class for data transfer
 * 
 * @author Stefan Schmidt
 *
 */
public class FileData {
	private Integer folderId;
	private String content;

	/**
	 * @param folderId The ID in the database for the folder
	 * @param content The content to transmit
	 */
	public FileData(Integer folderId, String content) {
		this.folderId = folderId;
		this.content = content;
	}

	/**
	 * @return the folderId
	 */
	public Integer getFolderId() {
		return folderId;
	}

	/**
	 * @param folderId
	 *            the folderId to set
	 */
	public void setFolderId(Integer folderId) {
		this.folderId = folderId;
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
