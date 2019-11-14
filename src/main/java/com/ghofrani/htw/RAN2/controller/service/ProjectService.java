package com.ghofrani.htw.RAN2.controller.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.error.DatabaseException;
import com.ghofrani.htw.RAN2.controller.error.SameUserException;
import com.ghofrani.htw.RAN2.database.ArtefactAccess;
import com.ghofrani.htw.RAN2.database.FeatureAccess;
import com.ghofrani.htw.RAN2.database.ProductAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.model.Artefact;
import com.ghofrani.htw.RAN2.model.Feature;
import com.ghofrani.htw.RAN2.model.Product;
import com.ghofrani.htw.RAN2.model.Project;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Implementation of IProjectService for use with the ProjectAccess database
 * interface.
 * 
 * @author Daniel Wahlmann
 *
 */
@Service
public class ProjectService implements IProjectService {
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ProjectAccess projectAccess;

	@Autowired
	private ProductAccess productAccess;

	@Autowired
	private FeatureAccess featureAccess;

	@Autowired
	private ArtefactAccess artefactAccess;

	/**
	 * Loads the Project with the given id.
	 * 
	 * @param id the id of the project to load
	 * @return The Project loaded
	 */
	@Override
	public Project loadProject(Integer id) {
		return projectAccess.selectProjectsByID(id);
	}

	/**
	 * Saves the Project to the database.
	 * 
	 * @param project The project to save
	 * @throws DatabaseException When the database encountered an error
	 * @return saved Project Object
	 */
	@Override
	public Project saveProject(Project project) throws DatabaseException {
		project.setLastChange(Calendar.getInstance().getTime());
		Project saved = projectAccess.saveProject(project);

		if (saved == null) {
			throw new DatabaseException();
		}

		return saved;
	}

	/**
	 * Copies the given Project to a different user.
	 * 
	 * @param oldId ID of parentProject
	 * @param newTitle Title of new Project
	 * @param newDescription New description of copy
	 * @param newUser User that copied
	 * @return the new project
	 * @throws SameUserException When user tried to copy from himself
	 */
	@Override
	public Project copyProject(Integer oldId, String newTitle, String newDescription, User newUser)
			throws SameUserException {

		Project oldProject = projectAccess.selectProjectsByID(oldId);
		if (newUser.getId().equals(oldProject.getUser().getId())) {
			throw new SameUserException();
		}

		Project newProject = projectAccess.saveProject(new Project(null, newTitle, newDescription,
				Calendar.getInstance().getTime(), new ArrayList<>(), new ArrayList<>(), newUser, oldProject, false));

		// Set ids of Features, Products and Artefacts to null so that new database
		// entries are created.
		for (Feature feature : oldProject.getFeatureList()) {

			Feature tmpFeature = featureAccess.selectFeaturesByID(feature.getId());

			List<Artefact> tempList = new ArrayList<>();
			for (Artefact artefact : tmpFeature.getArtefactList()) {
				Artefact tmpArtefact = artefactAccess.selectArtefactsByID(artefact.getId());
				tmpArtefact.setId(null);
				Artefact newArtefact = artefactAccess.saveArtefact(tmpArtefact);
				tempList.add(newArtefact);
			}
			tmpFeature.setArtefactList(tempList);
			
			List<Feature> tempFeatArtList = copyFeatureArtefactsHelper(tmpFeature); // Added
			tmpFeature.setFeatureArtefactList(tempFeatArtList);
			
			tmpFeature.setId(null);
			Feature newFeature = featureAccess.saveFeature(tmpFeature);
			newProject.addFeature(newFeature);
		}
		newProject = projectAccess.saveProject(newProject);

		// Saving the associations of Features with Products
		List<Product> products = oldProject.getProductList();
		Map<String, List<Feature>> productFeatures = new HashMap<>();
		for (Product product : products) {
			product.setId(null);
			productFeatures.put(product.getTitle(), product.getFeatureList());
			product.setFeatureList(new ArrayList<>());
		}
		newProject.setProductList(products);

		newProject = projectAccess.saveProject(newProject);

		// Add the new Features to the Products
		List<Product> newProducts = copyFeatureAssociations(newProject, productFeatures);

		newProject.setProductList(newProducts);
		newProject.setLastChange(Calendar.getInstance().getTime());
		newProject = projectAccess.saveProject(newProject);

		return newProject;
	}
	
//	private Map<Integer, List<Feature>> createFeatIdFeatArtsMap(Map<Integer, List<Feature>> tmpFeatArtList) {
//		Map<Integer, List<Feature>> featIdFeatArtsMap = new HashMap<>();
//		if (tmpFeatArtList.isEmpty() == true) {
//			return featIdFeatArtsMap;
//		} else {
//			for (Feature featArt: tmpFeatArtList) {
//				featIdFeatArtsMap.put(featArt.getId(), );
//			}
//		}
//	}
	
	/**
	 * 
	 * @param tmpFeature
	 * @return
	 * @author Hongfei Chen
	 */
	private List<Feature> copyFeatureArtefactsHelper(Feature tmpFeature) {
		log.info("Called copyHelper with feat art id={}", tmpFeature.getId());
		List<Feature> tempFeatArtList = new ArrayList<>();
		List<Feature> currentFeatArtList = tmpFeature.getFeatureArtefactList(); //featureAccess.selectFeatureArtefactsByFeatureID(tmpFeature.getId());
		if (currentFeatArtList.isEmpty() == true) {
			return tempFeatArtList;
		} else {
			for (Feature featArt : currentFeatArtList) {
				Feature tmpFeatArt = featureAccess.selectFeaturesByID(featArt.getId());
				Feature newFeatArt = tmpFeatArt;
				newFeatArt.setId(null);
				newFeatArt = featureAccess.saveFeature(newFeatArt);
				tempFeatArtList.add(newFeatArt);	
				newFeatArt.setFeatureArtefactList(copyFeatureArtefactsHelper(tmpFeatArt));
			}
			return tempFeatArtList;
		}
	}

	/**
	 * Copies the associations of Features with Products.
	 * 
	 * @param newProject The new copy of Project
	 * @param productFeatures The matrix of product and features of original project
	 * @return List of new products
	 */
	private List<Product> copyFeatureAssociations(Project newProject, Map<String, List<Feature>> productFeatures) {
		List<Product> newProducts = newProject.getProductList();
		List<Feature> newFeatures = newProject.getFeatureList();
		for (Product product : newProducts) {
			List<Feature> features = productFeatures.get(product.getTitle());
			for (Feature feature : newFeatures) {
				for (Feature f : features) {
					if (feature.getTitle().equals(f.getTitle())) {
						product.addFeature(feature);
					}
				}
			}
			productAccess.saveProduct(product);
		}
		return newProducts;
	}

	/**
	 * Wrapper for selectProjectsByUserID, returns a List with all Projects by a
	 * given User
	 * 
	 * @author Jannik Gröger
	 * @param user
	 *            The user of which the projects are returned
	 * @return List of projects of User user
	 */
	@Override
	public List<Project> loadAllProjectsOfUser(User user) {

		return projectAccess.selectProjectsByUserID(user.getId());
	}

	public List<Project> loadAllRatedProjectsOfUser(User user) {
		return projectAccess.selectProjectsByRatedUserID(user.getId());
	}
	
	/**
	 * Notifies all children of the project by setting the updatedParent variable
	 * 
	 * @param project The project which was updated
	 * @author Robert Völkner
	 */
	@Override
	public void notifyChildren(Project project) {
		List<Project> projectList = projectAccess.selectProjects();
		Project tempProject;

		for (int i = 0; i < projectList.size(); i++) {

			tempProject = projectList.get(i);
			// check for correct parent
			if (tempProject.getParent() != null && tempProject.getParent().getId().equals(project.getId())) {
				tempProject.setUpdatedparent(true);
				tempProject.setLastChange(Calendar.getInstance().getTime());
				projectAccess.saveProject(tempProject);
			}
		}

	}

}
