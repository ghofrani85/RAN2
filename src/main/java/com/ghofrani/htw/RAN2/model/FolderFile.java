package com.ghofrani.htw.RAN2.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This class contains the functionality for handling folder creation and editing within an file
 * 
 * @author Hongfei Chen
 * 
 */

public class FolderFile extends File {
	private String start;
	private String end;
	private List<File> subFileList;
	private FolderFile parentFolderFile;
	
	/**
	 * Constructor
	 * 
	 * @param id ID of file
	 * @param title Title of this file
	 * @param asset Asset associated with this file
	 * @param start Start String
	 * @param end End String
	 * @param subFileList The files in this file
	 * @param parentFolderFile The FolderFile to which this FolderFile belongs
	 */
	public FolderFile(Integer id, String title, Asset asset, String start, String end, List<File> subFileList, FolderFile parentFolderFile) {
		super(id, title, asset);
		this.start = start;
		this.end = end;
		this.subFileList = subFileList;
		this.parentFolderFile = parentFolderFile;
	}
	
	/**
	 * Default constructor
	 */
	public FolderFile() {
		super(null, null, null);
		this.start = null;
		this.end = null;
		this.subFileList = new ArrayList<>();
		this.parentFolderFile = null;
	}
	
	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}
	
	/**
	 * @param start
	 * 			the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}
	
	/**
	 * @return the end
	 */
	public String getEnd() {
		return end;
	}
	
	/**
	 * @param end
	 * 			the end to set
	 */
	public void setEnd(String end) {
		this.end = end;
	}
	
	/**
	 * @return the subFileList
	 */
	public List<File> getSubFileList() {
		return subFileList;
	}
	
	/**
	 * @param subFileList
	 * 			the subFileList to set
	 */
	public void setSubFileList(List<File> subFileList) {
		this.subFileList = subFileList;
	}
	
	/**
	 * @return parentFolderFile
	 */
	public FolderFile getParentFolderFile() {
		return parentFolderFile;
	}
	
	/**
	 * @param parentFolderFile
	 * 			the parentFileFile to set
	 */
	public void setParentFileFile(FolderFile parentFolderFile) {
		this.parentFolderFile = parentFolderFile;
	}
	
	/**
	 * @param subFile
	 * 			the subFile to be added to the subFileList
	 */
	public void addSubFile(File subFile) {
		if (subFileList == null) {
			subFileList = new ArrayList<>();
		}
		subFileList.add(subFile);
	}
	
	/**
	 * @param subFile
	 * 			the subFile to be removed from the subFileList
	 */
	public void removeSubFileList(File subFile) {
		subFileList.remove(subFile);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((subFileList == null) ? 0 : subFileList.hashCode());
		result = prime * result + ((parentFolderFile == null) ? 0 : parentFolderFile.hashCode());
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
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof FolderFile)) {
			return false;
		}
		return super.equals(obj);
	}
}
