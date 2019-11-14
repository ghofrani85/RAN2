package com.ghofrani.htw.RAN2.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that represents an artefact. Which means it has an asset and cuts it in a data type specific way.
 * If you want to extend the handled types extend this abstract class.
 * 
 * @author Stefan Schmidt
 *
 */
public abstract class Artefact {
	private Integer id;
	private String title;
	private Asset asset;
	private List<Tracking> trackingList;
	private Feature feature;

	/**
	 * @param id The ID of the artefact in the database
	 * @param title The title of the artefact
	 * @param asset The asset needed for the artefact
	 */
	public Artefact(Integer id, String title, Asset asset) {
		this.id = id;
		this.title = title;
		this.asset = asset;
		this.trackingList = new LinkedList<Tracking>();
		this.feature = null;
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
	 *            * the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the asset
	 */
	public Asset getAsset() {
		return asset;
	}

	/**
	 * @param asset
	 *            the asset to set
	 */
	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	/**
	 * @return the trackingList
	 */
	public List<Tracking> getTrackingList() {
		return trackingList;
	}

	/**
	 * @param trackingList
	 *            the trackingList to set
	 */
	public void setTrackingList(List<Tracking> trackingList) {
		this.trackingList = trackingList;
	}

	/**
	 * @return the feature
	 */
	public Feature getFeature() {
		return feature;
	}

	/**
	 * @param feature
	 *            the feature to set
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	/**
	 * Adds a tracking object to the tracking list.
	 * 
	 * @param tracking
	 *            the object to be added
	 */
	public void addTrackingEntry(Tracking tracking) {
		if (trackingList == null) {
			trackingList = new LinkedList<Tracking>();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((trackingList == null) ? 0 : trackingList.hashCode());
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
		if (!(obj instanceof Artefact)) {
			return false;
		}
		Artefact other = (Artefact) obj;
		if (this.id == null || other.getId() == null) {
			return false;
		}
		return this.id.equals(other.getId());
	}

}
