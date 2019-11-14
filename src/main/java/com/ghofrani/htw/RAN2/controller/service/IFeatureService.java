package com.ghofrani.htw.RAN2.controller.service;

import com.ghofrani.htw.RAN2.model.Feature;

/**
 * Provides an interface for handling interactions with the feature database.
 * 
 * @author Daniel Wahlmann
 *
 */
public interface IFeatureService {

	/**
	 * Deletes the feature with the given id.
	 * 
	 * @param id
	 *            the id of the Feature to be deleted
	 */
	void deleteFeature(Integer id);

	/**
	 * Loads the feature with the given id.
	 * 
	 * @param id id of the feature to be loaded
	 * @return The loaded feature
	 */
	Feature loadFeature(Integer id);

	/**
	 * Notifies all children of the feature by setting the updatedParent variable
	 * 
	 * @param feature Feature that has been updated
	 * @author Robert Völkner
	 */
	void notifyChildren(Feature feature);
}
