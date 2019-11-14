package com.ghofrani.htw.RAN2.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.service.FeatureService;
import com.ghofrani.htw.RAN2.controller.service.TrackingService;
import com.ghofrani.htw.RAN2.database.ArtefactAccess;
import com.ghofrani.htw.RAN2.database.AssetAccess;
import com.ghofrani.htw.RAN2.database.FeatureAccess;
import com.ghofrani.htw.RAN2.model.Artefact;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Feature;
import com.ghofrani.htw.RAN2.model.OtherArtefact;

/**
 * @author Jannik Gröger
 *
 */

@Controller
public class OtherArtefactController {

	private static final String REDIRECT_FEATURE_ID = "redirect:feature?id=";

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private FeatureService featureService;

	@Autowired
	private TrackingService trackingService;

	@Autowired
	private FeatureAccess featureAccess;

	@Autowired
	private AssetAccess assetAccess;

	@Autowired
	private ArtefactAccess artefactAccess;

	/**
	 * Adds a new Artefact of type Other
	 * @param featureId ID of the feature associated with the artefact
	 * @param title Title of the new Artefact
	 * @param assetId ID of the asset associated with the artefact
	 * @param httpRequest Request Object
	 * @return Redirect to FeatureView
	 */
	@PostMapping("/addOtherArtefact")
	public String addOtherArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assetId") Integer assetId, HttpServletRequest httpRequest) {

		log.info("Called addOtherArtefact with featureId={}, assetId={}", featureId, assetId);

		Feature feature = featureAccess.selectFeaturesByID(featureId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		// Check for duplicate Artefact titles in feature
		List<Artefact> artefacts = feature.getArtefactList();

		for (Artefact a : artefacts) {
			if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FEATURE_ID + featureId + "&ArtefactDuplicateTitleError=true&title=" + title;
			}
		}

		OtherArtefact otherArtefact = new OtherArtefact(null, title, asset, "", "");
		feature.addArtefact(otherArtefact);
		otherArtefact.setFeature(feature);

		feature = trackingService.trackAddedArtefact(otherArtefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		featureAccess.saveFeature(feature);

		return REDIRECT_FEATURE_ID + featureId;
	}

	/**
	 * Changes an artefact of type Other
	 * @param featureId ID of the feature associated with the artefact
	 * @param title Title of the new Artefact
	 * @param assetId ID of the asset associated with the artefact
	 * @param httpRequest Request Object
	 * @param artefactId ID of the artefact to be changed
	 * @return Redirect to the featureView 
	 */
	@PostMapping("/changeOtherArtefact")
	public String changeOtherArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "title") String title, @RequestParam(value = "assetId") Integer assetId,
			@RequestParam(value = "artefactId") Integer artefactId, HttpServletRequest httpRequest) {

		log.info("Called changeOtherArtefact with featureId{}, assetId={}, artefactId={},", featureId, assetId,
				artefactId);

		Feature feature = featureAccess.selectFeaturesByID(featureId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		Artefact otherArtefact = artefactAccess.selectArtefactsByID(artefactId);

		List<Artefact> artefactList = new ArrayList<>(feature.getArtefactList());

		for (Artefact a : artefactList) {
			if (a.getId().equals(otherArtefact.getId())) {
				feature.removeArtefact(otherArtefact);
				log.info("Removed Artefact: {}, from Feature: {}", otherArtefact.getTitle(), feature.getTitle());
			} else if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FEATURE_ID + featureId + "&ArtefactDuplicateTitleError=true&title=" + title;
			}
		}

		OtherArtefact artefact = new OtherArtefact(artefactId, title, asset, null, null);
		feature.addArtefact(artefact);
		artefact.setFeature(feature);

		feature = trackingService.trackArtefactInformation(artefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		featureAccess.saveFeature(feature);

		return REDIRECT_FEATURE_ID + featureId + "&assetId=" + assetId;
	}

	/**
	 * Removes an artefact of type Other
	 * @param featureId ID of the feature associated with the artefact
	 * @param artefactId ID of the artefact to be removed
	 * @param httpRequest Request Object
	 * @return Redirect to featureView
	 */
	@PostMapping("/removeOtherArtefact")
	public String removeOtherArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "artefactId") Integer artefactId, HttpServletRequest httpRequest) {

		log.info("Called removeOtherArtefact with featureId={}, artefactId={}", featureId, artefactId);

		Feature feature = featureAccess.selectFeaturesByID(featureId);

		Artefact otherArtefact = artefactAccess.selectArtefactsByID(artefactId);

		feature = trackingService.trackDeletedArtefact(otherArtefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		List<Artefact> artefactList = artefactAccess.selectArtefactsByFeatureID(featureId);

		for (Artefact a : artefactList) {
			if (a.getId().equals(otherArtefact.getId())) {
				feature.removeArtefact(a);
				featureAccess.saveFeature(feature);
				artefactAccess.deleteArtefact(otherArtefact);
			}
		}

		return REDIRECT_FEATURE_ID + featureId;
	}
}
