package com.ghofrani.htw.RAN2.controller;

import java.util.List;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

// Import spring framework libs
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import com.ghofrani.htw.RAN2.controller.service.FeatureService;
import com.ghofrani.htw.RAN2.controller.service.IUserService;
import com.ghofrani.htw.RAN2.controller.service.TrackingService;
import com.ghofrani.htw.RAN2.database.ArtefactAccess;
import com.ghofrani.htw.RAN2.database.AssetAccess;
import com.ghofrani.htw.RAN2.database.FeatureAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.database.TrackingAccess;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.Artefact;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Feature;
import com.ghofrani.htw.RAN2.model.Project;
import com.ghofrani.htw.RAN2.model.Tracking;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Feature Controller for the Feature View
 * 
 * 
 * 
 * @author Jannik Groeger
 *
 */
@Controller
public class FeatureController {

	private static final String REDIRECT = "redirect:feature?id=";

	@Autowired
	private ProjectAccess projectAccess;

	@Autowired
	private FeatureAccess featureAccess;

	@Autowired
	private AssetAccess assetAccess;

	@Autowired
	private ArtefactAccess artefactAccess;

	@Autowired
	IUserService userService;

	@Autowired
	UserAccess userAccess;

	@Autowired
	TrackingService trackingService;
	
	@Autowired
	FeatureService featureService; // Added

	@Autowired
	private MessageSource messages;

	/**
	 * Database Interface for Tracking.
	 */
	@Autowired
	TrackingAccess trackingAccess;

	/**
	 * Loads the FeatureView
	 * 
	 * @param id
	 *            ID of the Feature that is loaded
	 * @param AssetDuplicateTitleError
	 *            if not null, an AssetDuplicateTitleError was thrown
	 * @param FeatureDuplicateTitleError
	 *            if not null, an FeatureDuplicateTitleError was thrown
	 * @param ArtefactDuplicateTitleError
	 *            if not null, an ArtefactDuplicateTitleError was thrown
	 * @param DailyUploadVolumeReached
	 *            if not null, an DailyUploadVolumeReached was thrown
	 * @param FileTooBig
	 *            if not null, an FileTooBigError was thrown
	 * @param invalidFileError
	 *            if not null, an invalidFileError was thrown
	 * @param invalidURLError
	 *            if not null, an invalidURLError was thrown
	 * @param request
	 *            Request Object for the View
	 * @param duplicateTitle
	 *            Title that is duplicate
	 * @param model
	 *            Model Object for the View
	 * @return The loaded View
	 */
	@RequestMapping(path = "/feature", params = "id")
	public String loadFeature(@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "AssetDuplicateTitleError", required = false) String AssetDuplicateTitleError,
			@RequestParam(value = "FeatureDuplicateTitleError", required = false) String FeatureDuplicateTitleError,
			@RequestParam(value = "ArtefactDuplicateTitleError", required = false) String ArtefactDuplicateTitleError,
			@RequestParam(value = "DailyUploadVolumeReached", required = false) String DailyUploadVolumeReached,
			@RequestParam(value = "FileTooBig", required = false) String FileTooBig,
			@RequestParam(value = "invalidFileError", required = false) String invalidFileError,
			@RequestParam(value = "invalidURLError", required = false) String invalidURLError, WebRequest request,
			@RequestParam(value = "title", required = false) String duplicateTitle, Model model) {

		if (AssetDuplicateTitleError != null) {
			model.addAttribute("showAssetDuplicateTitleError", true);
			model.addAttribute("duplicateTitle", duplicateTitle);
		}

		if (FeatureDuplicateTitleError != null) {
			model.addAttribute("showFeatureDuplicateTitleError", true);
			model.addAttribute("duplicateTitle", duplicateTitle);
		}

		if (ArtefactDuplicateTitleError != null) {
			model.addAttribute("showArtefactDuplicateTitleError", true);
			model.addAttribute("duplicateTitle", duplicateTitle);
		}

		if (DailyUploadVolumeReached != null) {
			String title = messages.getMessage("user.dailyUploadLimitReachedTitle", null, request.getLocale());
			String message = messages.getMessage("user.dailyUploadLimitReached", null, request.getLocale());
			String messagePart2 = messages.getMessage("user.dailyUploadLimitReached2", null, request.getLocale());
			model.addAttribute("showError", true);
			model.addAttribute("errorTitle", title);
			model.addAttribute("errorMessage", message + " " + DailyUploadVolumeReached + " " + messagePart2);
		}

		if (FileTooBig != null) {
			String title = messages.getMessage("user.fileTooBigTitle", null, request.getLocale());
			String message = messages.getMessage("user.fileTooBig", null, request.getLocale());
			model.addAttribute("showError", true);
			model.addAttribute("errorTitle", title);
			model.addAttribute("errorMessage", message + " " + FileTooBig);
		}

		if (invalidFileError != null) {
			model.addAttribute("showInvalidFileError", true);
		}

		if (invalidURLError != null) {
			model.addAttribute("showInvalidURLError", true);
		}

		// Database not yet filled with Features
		Feature feature = featureAccess.selectFeaturesByID(id);

		// Load all tracking information of the feature
		List<Tracking> trackingList = feature.getTrackingList();
		String[] history = new String[0];
		if (trackingList.isEmpty() == false) {
			history = new String[trackingList.size()];
			Tracking tempTrack = null;
			for (int i = 0; i < history.length; i++) {
				tempTrack = trackingList.get(i);
				history[i] = tempTrack.getText();
			}
		}

		// Fetch Artefacts of Feature
		List<Artefact> artefactList = artefactAccess.selectArtefactsByFeatureID(id);

		// Load all Assets to chooseAsset
		List<Asset> allAssetList = assetAccess.selectAssets();

		// Determine parentProjects
		List<Project> parentProjects = projectAccess.selectProjectsByFeatureID(id);
		
		// Determine parentFeature
		List<Feature> parentFeatures = featureAccess.selectFeatureByFeatureArtefactID(id);

		// Check if link to updated parent feature is needed
		if (feature.isUpdatedparent()) {
			model.addAttribute("parentFeatureId", feature.getParent().getId());
		}
		
		// Load all featureArtefacts ADDED
		List<Feature> featureArtefactList = featureAccess.selectFeatureArtefactsByFeatureID(id);
		
		// Load the number of featureArtefacts for each featArt ADDED
		for (Feature featArt : featureArtefactList) {
			List<Feature> subFeatArtList = featureAccess.selectFeatureArtefactsByFeatureID(featArt.getId());
			featArt.setNumOfFeatArt(subFeatArtList.size());
		}
		
		model.addAttribute("feature", feature);
		model.addAttribute("artefacts", artefactList);
		model.addAttribute("featureArtefacts", featureArtefactList); // ADDED
		model.addAttribute("allAssets", allAssetList);
		model.addAttribute("history", history);
		//model.addAttribute("parentProjects", parentProjects);

		// Retrieve currently active User
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		User currentUser = userService.loadUser(user.getUsername());
		
		Project parent;
		if (parentProjects.isEmpty() == false) {
			parent = parentProjects.get(0);
		} else {
			List<Feature> grandparentFeatures = featureAccess.selectFeatureByFeatureArtefactID(parentFeatures.get(0).getId());//(parentFeatures.get(0).getId());
			while (grandparentFeatures.isEmpty() == false) {
				if (parentFeatures.contains(grandparentFeatures.get(0)) == false) {
					parentFeatures.add(0,grandparentFeatures.get(0));
				}
				grandparentFeatures = featureAccess.selectFeatureByFeatureArtefactID(grandparentFeatures.get(0).getId());
			}
			parent = projectAccess.selectProjectsByFeatureID(parentFeatures.get(0).getId()).get(0);
		}
		model.addAttribute("parentFeatures", parentFeatures); // ADDED
		model.addAttribute("parentProject", parent); //ADDED
		
		if (parent.getUser().getId().equals(currentUser.getId()) || currentUser.getRoles().contains("ROLE_ADMIN")) {
			model.addAttribute("hasEditRights", true);
		}
		
		if (currentUser.getRoles().contains("ROLE_ADMIN")) {
			List<Project> projectList = projectAccess.selectProjects();
			model.addAttribute("projectList", projectList);
			if (projectList.size() == 0) {
				model.addAttribute("listIsEmpty", true);
			}
		} else {
			List<Project> projectList = projectAccess.selectProjectsByUserID(currentUser.getId());
			model.addAttribute("projectList", projectList);
			if (projectList.size() == 0) {
				model.addAttribute("listIsEmpty", true);
			}
		}

		return "feature";
	}


	/**
	 * Edits the title and description of the current feature.
	 * 
	 * @param featureId
	 *            The id of the current feature.
	 * @param title
	 *            The new title.
	 * @param description
	 * 
	 *            The new description.
	 * @param httpRequest
	 *            The HttpRequest Object for the View
	 * @return The loaded View
	 */
	@PostMapping("/editfeature")
	public String editFeature(@RequestParam(value = "id", required = true) Integer featureId,
			@RequestParam(value = "title", required = true) String title,
			@RequestParam(value = "description") String description, HttpServletRequest httpRequest) {

		Feature feature = featureAccess.selectFeaturesByID(featureId);

		List<Project> parentProjects = projectAccess.selectProjectsByFeatureID(feature.getId());
		List<Feature> parentFeatures = featureAccess.selectFeatureByFeatureArtefactID(featureId);
		
		Project project;
		if (parentProjects.isEmpty() == false) {
			project = parentProjects.get(0);
		} else {
			List<Feature> grandparentFeatures = featureAccess.selectFeatureByFeatureArtefactID(parentFeatures.get(0).getId());
			while (grandparentFeatures.isEmpty() == false) {
				parentFeatures.add(grandparentFeatures.get(0));
				grandparentFeatures = featureAccess.selectFeatureByFeatureArtefactID(grandparentFeatures.get(0).getId());
			}
			project = projectAccess.selectProjectsByFeatureID(grandparentFeatures.get(0).getId()).get(0);
		}

		for (Feature f : project.getFeatureList()) {
			if (f.getTitle().equals(title) && !f.getId().equals(featureId)) {
				return "redirect:feature?id=" + featureId + "&FeatureDuplicateTitleError=true&title=" + title;
			}
		}

		feature.setTitle(title);
		feature.setDescription(description);

		// Track alteration of feature information
		feature = trackingService.trackFeatureInformation(feature, httpRequest);

		featureAccess.saveFeature(feature);

		return REDIRECT + featureId;
	}

	/**
	 * Resets updatedParent and redirects to the parents feature view
	 * 
	 * @param featureId
	 *            The id of the current feature.
	 * @param parentId
	 *            The id of the current features parent.
	 * @return A redirect to the featureView of the parent feature.
	 * @author Robert Völkner
	 */

	@RequestMapping("/parentfeature")
	public String parentFeature(@RequestParam(value = "id", required = true) Integer featureId,
			@RequestParam(value = "parentId", required = true) String parentId) {

		Feature feature = featureAccess.selectFeaturesByID(featureId);
		feature.setUpdatedparent(false);
		featureAccess.saveFeature(feature);

		return REDIRECT + parentId;
	}
	
	/**
	 * 
	 * @param featureId
	 * @param featureArtefactId
	 * @return redirect to feature view
	 * @author Hongfei Chen
	 */
	// TODO: add tracking
	@PostMapping("/deletefeatureartefact")
	public String deleteFetureArtefact(@RequestParam(value = "featureArtefactId", required = true) Integer featureArtefactId) { 
		
		int featureId = featureAccess.selectFeatureByFeatureArtefactID(featureArtefactId).get(0).getId();
		
		featureService.deleteFeature(featureArtefactId);
		
		//Feature feature = featureService.loadFeature(featureId);
		
		return REDIRECT + featureId;
	}

}
