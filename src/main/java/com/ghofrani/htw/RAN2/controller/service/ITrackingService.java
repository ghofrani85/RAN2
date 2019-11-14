package com.ghofrani.htw.RAN2.controller.service;

import javax.servlet.http.HttpServletRequest;

import com.ghofrani.htw.RAN2.model.Artefact;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Feature;
import com.ghofrani.htw.RAN2.model.Product;
import com.ghofrani.htw.RAN2.model.Project;

/**
 * Provides an interface for handling interactions.
 * 
 * @author Robert Völkner
 *
 */
public interface ITrackingService {

	/**
	 * Adds tracking information to the feature after adding an artefact
	 * 
	 * @param artefact Artefact to be tracked
	 * @param feature Feature that contains artefact 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated feature
	 */
	Feature trackAddedArtefact(Artefact artefact, Feature feature, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the feature and asset after editing an artefact
	 * 
	 * @param artefact Artefact to be tracked
	 * @param feature Feature that contains artefact 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated feature
	 */
	Feature trackArtefactInformation(Artefact artefact, Feature feature, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the feature and asset after deleting an artefact
	 * 
	 * @param artefact Artefact to be tracked
	 * @param feature Feature that contains artefact 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated feature
	 */
	Feature trackDeletedArtefact(Artefact artefact, Feature feature, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information for the creation to the feature
	 * 
	 * @param feature Feature to be tracked
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated feature
	 */
	Feature trackCreatedFeature(Feature feature, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the new feature after copying it from the parent
	 * feature
	 * 
	 * @param parent ParentFeature of feature
	 * @param newFeature new childFeature
	 * @param httpRequest RequestObject RequestObject
	 * @return return the copied feature
	 */
	Feature trackCopiedFeature(Feature parent, Feature newFeature, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the feature after editing it
	 * 
	 * @param feature Feature to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated feature
	 */
	Feature trackFeatureInformation(Feature feature, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the project after adding a feature
	 * 
	 * @param feature Feature to be tracked
	 * @param project Project containing feature
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated project
	 */
	Project trackAddedFeature(Feature feature, Project project, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the project after adding a product
	 * 
	 * @param product Product to be tracked
	 * @param project Project containing product
	 * @param httpRequest RequestObject Request Object
	 * @return return the updated project
	 */
	Project trackAddedProduct(Product product, Project project, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information for the creation to the project
	 * 
	 * @param project Project to be tracked
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated project
	 */
	Project trackCreatedProject(Project project, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the new project after copying it from the parent
	 * project
	 * 
	 * @param parentId Id of parentProject
	 * @param newProject newProject Object
	 * @param httpRequest RequestObject Request Object
	 */
	void trackCopiedProject(Integer parentId, Project newProject, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the project after deleting a feature
	 * 
	 * @param projectId ID of project containing feature
	 * @param featureId ID of feature thats tracking will be deleted
	 * @param httpRequest RequestObject
	 */
	void trackDeletedFeature(Integer projectId, Integer featureId, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the project after deleting a product
	 * 
	 * @param projectId ID of project containg product
	 * @param productId ID of product thats track should be deleted
	 * @param httpRequest RequestObject
	 */
	void trackDeletedProduct(Integer projectId, Integer productId, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the project after editing it
	 * 
	 * @param project project to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated project
	 */
	Project trackProjectInformation(Project project, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information for the creation to the asset
	 * 
	 * @param asset Asset to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated asset
	 */
	Asset trackCreatedAsset(Asset asset, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the asset after editing it
	 * 
	 * @param asset Asset to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated asset
	 */
	Asset trackAssetInformation(Asset asset, HttpServletRequest httpRequest);
	
	/**
	 * Adds tracking information for the rating to the project
	 * 
	 * @param project Project to be tracked
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated project
	 */
	Project trackRatedProject(Project project, HttpServletRequest httpRequest);

}
