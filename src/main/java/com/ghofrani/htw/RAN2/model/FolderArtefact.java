package com.ghofrani.htw.RAN2.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This class contains the functionality for handling folder creation and editing within an artefact
 * 
 * @author Hongfei Chen
 * 
 */

public class FolderArtefact extends Artefact {
	private String start;
	private String end;
	private List<Artefact> subArtefactList;
	private FolderArtefact parentFolderArtefact;
	
	/**
	 * Constructor
	 * 
	 * @param id ID of artefact
	 * @param title Title of this artefact
	 * @param asset Asset associated with this artefact
	 * @param start Start String
	 * @param end End String
	 * @param subArtefactList The artefacts in this file
	 * @param parentFolderArtefact The FolderArtefact to which this FolderArtefact belongs
	 */
	public FolderArtefact(Integer id, String title, Asset asset, String start, String end, List<Artefact> subArtefactList, FolderArtefact parentFolderArtefact) {
		super(id, title, asset);
		this.start = start;
		this.end = end;
		this.subArtefactList = subArtefactList;
		this.parentFolderArtefact = parentFolderArtefact;
	}
	
	/**
	 * Default constructor
	 */
	public FolderArtefact() {
		super(null, null, null);
		this.start = null;
		this.end = null;
		this.subArtefactList = new ArrayList<>();
		this.parentFolderArtefact = null;
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
	 * @return the subArtefactList
	 */
	public List<Artefact> getSubArtefactList() {
		return subArtefactList;
	}
	
	/**
	 * @param subArtefactList
	 * 			the subArtefactList to set
	 */
	public void setSubArtefactList(List<Artefact> subArtefactList) {
		this.subArtefactList = subArtefactList;
	}
	
	/**
	 * @return parentFolderArtefact
	 */
	public FolderArtefact getParentFolderArtefact() {
		return parentFolderArtefact;
	}
	
	/**
	 * @param parentFolderArtefact
	 * 			the parentFileArtefact to set
	 */
	public void setParentFileArtefact(FolderArtefact parentFolderArtefact) {
		this.parentFolderArtefact = parentFolderArtefact;
	}
	
	/**
	 * @param subArtefact
	 * 			the subArtefact to be added to the subArtefactList
	 */
	public void addSubArtefact(Artefact subArtefact) {
		if (subArtefactList == null) {
			subArtefactList = new ArrayList<>();
		}
		subArtefactList.add(subArtefact);
	}
	
	/**
	 * @param subArtefact
	 * 			the subArtefact to be removed from the subArtefactList
	 */
	public void removeSubArtefactList(Artefact subArtefact) {
		subArtefactList.remove(subArtefact);
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
		result = prime * result + ((subArtefactList == null) ? 0 : subArtefactList.hashCode());
		result = prime * result + ((parentFolderArtefact == null) ? 0 : parentFolderArtefact.hashCode());
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
		if (!(obj instanceof FolderArtefact)) {
			return false;
		}
		return super.equals(obj);
	}
}
