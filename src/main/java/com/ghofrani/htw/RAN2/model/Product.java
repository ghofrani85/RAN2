package com.ghofrani.htw.RAN2.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vivian Holzapfel
 *
 */
public class Product {

	private Integer id;
	private String title;
	private String description;
	private List<Feature> featureList;
	private List<Tracking> trackingList;
	
	/**
	 * Constructor
	 * 
	 * @param id
	 *            id of the product
	 * @param title
	 *            title of the product
	 * @param description
	 *            description of the product
	 * @param features
	 *            all features in the product
	 */
	public Product(Integer id, String title, String description, List<Feature> features) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.featureList = features;
		this.trackingList = new LinkedList<>();
	}

	/**
	 * Constructor empty
	 * 
	 */
	public Product() {
		this.id = null;
		this.title = "";
		this.description = "";
		this.featureList = new ArrayList<>();
		this.trackingList = new LinkedList<>();
	}

	/**
	 * Constructor just title and description
	 * 
	 * 
	 * @param title
	 *            title of the product
	 * @param description
	 *            description of the product
	 */
	public Product(String title, String description) {
		this.title = title;
		this.description = description;
		this.id = null;
		this.featureList = new ArrayList<>();
		this.trackingList = new LinkedList<>();
	}

	/**
	 * Constructor just title and description
	 * 
	 * @param id
	 *            id of the product
	 * @param title
	 *            title of the product
	 * @param description
	 *            description of the product
	 */
	public Product(int id, String title, String description) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.featureList = new ArrayList<>();
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
	 * @return the feature names
	 */
	public List<String> getFeatureNames() {
		List<String> featureNames = new ArrayList<>();
		for (Feature f : featureList) {
			featureNames.add(f.getTitle());
		}
		return featureNames;
	}

	/**
	 * @return the features
	 */
	public List<Feature> getFeatureList() {
		return featureList;
	}

	/**
	 * @param features
	 *            the list of features to set
	 */
	public void setFeatureList(List<Feature> features) {
		this.featureList = features;
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

	@Override
	public String toString() {
		return String.format("%d: %s, %s", this.id, this.title, this.description);
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
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((featureList == null) ? 0 : featureList.hashCode());
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
		if (!(obj instanceof Product)) {
			return false;
		}
		Product other = (Product) obj;
		if (this.id == null || other.getId() == null) {
			return false;
		}
		return this.id.equals(other.getId());
	}

	public void addFeature(Feature feature) {
		if (featureList == null) {
			featureList = new ArrayList<>();
		}
		featureList.add(feature);
	}

	public void removeFeature(Feature feature) {
		featureList.remove(feature);
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
}
