package com.ghofrani.htw.RAN2.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provides all information for a folder.
 * 
 * A folder is an object that has one or more files.
 * 
 * @author Stefan Schmidt
 *
 */
public class Folder {

	private Integer id;
	private String title;
	private String description;
	private List<File> fileList;
	private Folder parent;
	private boolean updatedparent;
	private List<Tracking> trackingList;
	private List<Folder> folderFileList;
	private Integer numOfFeatArt;

	/**
	 * Default Constructor.
	 */
	public Folder() {
		this.id = null;
		this.title = "";
		this.description = "";
		this.fileList = new ArrayList<>();
		this.trackingList = new LinkedList<>();
		this.updatedparent = false;
		this.folderFileList = new ArrayList<>();
		this.numOfFeatArt = null;
	}

	/**
	 * Constructor for a new Folder.
	 * 
	 * @param title
	 *            The title for the folder
	 * @param description
	 *            The description of the folder
	 */
	public Folder(String title, String description) {
		this.id = null;
		this.title = title;
		this.description = description;
		this.fileList = new ArrayList<>();
		this.parent = null;
		this.trackingList = new LinkedList<>();
		this.updatedparent = false;
		this.folderFileList = new ArrayList<>();
		this.numOfFeatArt = null;
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            id of the folder
	 * @param title
	 *            title of the folder
	 * @param description
	 *            description of the folder
	 * @param fileList
	 *            List of files for folder
	 */
	public Folder(Integer id, String title, String description, List<File> fileList) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.fileList = fileList;
		this.parent = null;
		this.trackingList = new LinkedList<>();
		this.updatedparent = false;
		this.folderFileList = new ArrayList<>();
		this.numOfFeatArt = null;
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            id of the folder
	 * @param title
	 *            title of the folder
	 * @param description
	 *            description of the folder
	 * @param fileList
	 *            List of files for folder
	 * @param parent
	 *            the Folder this was copied from
	 * @param folderFileList
	 * 			  list folder files for folder
	 */
	public Folder(Integer id, String title, String description, List<File> fileList, Folder parent, List<Folder> folderFileList) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.fileList = fileList;
		this.parent = parent;
		this.trackingList = new LinkedList<>();
		this.updatedparent = false;
		this.folderFileList = folderFileList;
		this.numOfFeatArt = folderFileList.size();
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the assetList
	 */
	public List<File> getFileList() {
		return fileList;
	}

	/**
	 * @param fileList
	 *            the assetList to set
	 */
	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}

	/**
	 * @return the parent
	 */
	public Folder getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(Folder parent) {
		this.parent = parent;
	}

	/**
	 * @return the trackinglist
	 */
	public List<Tracking> getTrackingList() {
		return trackingList;
	}

	/**
	 * @param trackinglist
	 *            the trackinglist to set
	 */
	public void setTrackingList(List<Tracking> trackinglist) {
		this.trackingList = trackinglist;
	}

	/**
	 * @return the updatedparent
	 */
	public boolean isUpdatedparent() {
		return updatedparent;
	}

	/**
	 * @param updatedparent
	 *            the updatedparent to set
	 */
	public void setUpdatedparent(boolean updatedparent) {
		this.updatedparent = updatedparent;
	}
	
	/**
	 * @return the folderFileList
	 * 
	 * @author Hongfei Chen
	 */
	public List<Folder> getFolderFileList() {
		return folderFileList;
	}
	
	/**
	 * @param folderFileList
	 * 			the folderFileList to set
	 * 
	 * @author Hongfei Chen
	 */
	public void setFolderFileList(List<Folder> folderFileList) {
		this.folderFileList = folderFileList;
	}
	
	/**
	 * @return The number of children folderFiles
	 * @author Hongfei Chen
	 */
	public Integer getNumOfFeatArt() {
		return numOfFeatArt;
	}
	
	public void setNumOfFeatArt(Integer numOfFeatArt) {
		this.numOfFeatArt = numOfFeatArt;
	}

	/**
	 * @param file
	 */
	public void addFile(File file) {
		if (fileList == null) {
			fileList = new ArrayList<>();
		}
		fileList.add(file);
	}

	public void removeFile(File file) {
		fileList.remove(file);
	}

	/**
	 * Adds a tracking object to the tracking list.
	 * 
	 * @param tracking
	 *            the object to be added
	 */
	public void addTrackingEntry(Tracking tracking) {
		if (trackingList == null) {
			trackingList = new LinkedList<>();
		}
		trackingList.add(tracking);
	}

	/**
	 * Removes a tracking object from the tracking list.
	 * 
	 * @param tracking
	 *            the object to be removed
	 */
	public void removeTrackingEntry(Tracking tracking) {
		trackingList.remove(tracking);
	}
	
	/**
	 * Adds a folder object to the folderFileList
	 * 
	 * @param folderFile
	 * 			the folder to be added
	 * 
	 * @author Hongfei Chen
	 */
	public void addFolderFile(Folder folderFile) {
		if (folderFileList == null) {
			folderFileList = new LinkedList<>();
		}
		folderFileList.add(folderFile);
	}
	
	/**
	 * Removes a folder from the folderFileList
	 * 
	 * @param folderFile
	 * 			the folder to be removed
	 * 
	 * @author Hongfei Chen
	 */
	public void removeFolderFile(Folder folderFile) {
		folderFileList.remove(folderFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileList == null) ? 0 : fileList.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((trackingList == null) ? 0 : trackingList.hashCode());
		result = prime * result + (updatedparent ? 1231 : 1237);
		result = prime * result + ((folderFileList == null) ? 0 : folderFileList.hashCode());
		return result;
	}

	/**
	 * Equals method, only compares the ids because the content is saved in the
	 * database.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Folder)) {
			return false;
		}
		Folder other = (Folder) obj;
		if (this.id == null || other.getId() == null) {
			return false;
		}
		return this.id.equals(other.id);
	}
}
