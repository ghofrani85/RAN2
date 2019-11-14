package com.ghofrani.htw.RAN2.controller.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.database.FeatureAccess;
import com.ghofrani.htw.RAN2.model.Feature;

/**
 * Implementation of IFeatureService for use with the FeaturetAccess database
 * interface.
 * 
 * @author Daniel Wahlmann
 *
 */
@Service
public class FeatureService implements IFeatureService {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private FeatureAccess featureAccess;

	/**
	 * Deletes the feature with the given id.
	 * 
	 * @param id
	 *            the id of the Feature to be deleted
	 */
	@Override
	public void deleteFeature(Integer id) {

		Feature feature = loadFeature(id);
		featureAccess.deleteFeature(feature);
	}

	/**
	 * Loads the feature with the given id.
	 * 
	 * @param id id of the feature to be loaded
	 * @return The loaded feature
	 */
	@Override
	public Feature loadFeature(Integer id) {
		return featureAccess.selectFeaturesByID(id);
	}

	/**
	 * Notifies all children of the feature by setting the updatedParent variable
	 * 
	 * @param feature Feature that has been updated
	 * @author Robert Völkner
	 */
	@Override
	public void notifyChildren(Feature feature) {

		List<Feature> featureList = featureAccess.selectFeatures();
		Feature tempFeature;
		log.info("Change to feature {} with title {}, notifying children", feature.getId(), feature.getTitle());

		for (int i = 0; i < featureList.size(); i++) {

			tempFeature = featureList.get(i);
			// check for correct parent
			if (tempFeature.getParent() != null && tempFeature.getParent().getId().equals(feature.getId())) {
				tempFeature.setUpdatedparent(true);
				log.info("Set updatedparent of feature {} with title {}.", tempFeature.getId(), tempFeature.getTitle());
				featureAccess.saveFeature(tempFeature);
			}
		}

	}

}
