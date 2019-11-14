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
import com.ghofrani.htw.RAN2.model.XMLArtefact;

/**
 * This controller handles the add, change and remove operations for artefacts.
 * These can be used with "/addXMLArtefact", "/changeXMLArtefact" and
 * "removeXMLArtefact".
 * 
 * @author Stefan Schmidt
 *
 */
@Controller
public class XMLArtefactController {

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
	 * @param featureId The ID of the feature in the database
	 * @param title The title of the artefact
	 * @param assetId The ID of the asset in the database
	 * @param nodes the Nodes data
	 * @param httpRequest httpRequestObject
	 * @return The website url to open
	 */
	@PostMapping("/addXMLArtefact")
	public String addXMLArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assetId") Integer assetId, @RequestParam(value = "nodes") String nodes,
			HttpServletRequest httpRequest) {

		log.info("Called addXMLArtefact with featureId={}, assetId={}, nodes={}", featureId, assetId, nodes);

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

		XMLArtefact xmlArtefact = new XMLArtefact(null, title, asset, nodes);
		feature.addArtefact(xmlArtefact);
		xmlArtefact.setFeature(feature);

		feature = trackingService.trackAddedArtefact(xmlArtefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		featureAccess.saveFeature(feature);

		return REDIRECT_FEATURE_ID + featureId;
	}

	/**
	 * @param assetId The ID of the asset in the database
	 * @param featureId The ID of the feature in the database
	 * @param title The title of the artefact
	 * @param artefactId The ID of the artefact in the database
	 * @param nodes The new nodes data
	 * @param httpRequest httpRequestObject
	 * @return The website url to open
	 */
	@PostMapping("/changeXMLArtefact")
	public String changeXMLArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "title") String title, @RequestParam(value = "assetId") Integer assetId,
			@RequestParam(value = "artefactId") Integer artefactId, @RequestParam(value = "nodes") String nodes,
			HttpServletRequest httpRequest) {

		log.info("Called changeXMLArtefact with featureId={}, assetId={}, artefactId={}, nodes={}", featureId, assetId,
				artefactId, nodes);

		Feature feature = featureAccess.selectFeaturesByID(featureId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		Artefact xmlArtefact = artefactAccess.selectArtefactsByID(artefactId);
		List<Artefact> artefacts = new ArrayList<>(feature.getArtefactList());

		// Check if newTitle is aready used by other artefact in feature
		for (Artefact a : artefacts) {
			if (a.getId().equals(xmlArtefact.getId())) {
				feature.removeArtefact(xmlArtefact);
				log.info("Removed Artefact: {}, from Feature: {}", xmlArtefact.getTitle(), feature.getTitle());
			} else if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FEATURE_ID + featureId + "&ArtefactDuplicateTitleError=true&title=" + title;
			}
		}

		XMLArtefact artefact = new XMLArtefact(artefactId, title, asset, nodes);
		feature.addArtefact(artefact);
		artefact.setFeature(feature);

		feature = trackingService.trackArtefactInformation(artefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		featureAccess.saveFeature(feature);

		return REDIRECT_FEATURE_ID + featureId + "&assetId=" + assetId;
	}

	/**
	 * @param featureId The ID of the feature in the database
	 * @param artefactId The ID of the artefact in the database
	 * @param httpRequest httpRequestObject
	 * @return the website url to open
	 */
	@PostMapping("/removeXMLArtefact")
	public String removeXMLArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "artefactId") Integer artefactId, HttpServletRequest httpRequest) {

		log.info("Called removeXMLArtefact with featureId={}, artefactId={}", featureId, artefactId);

		Feature feature = featureAccess.selectFeaturesByID(featureId);

		Artefact xmlArtefact = artefactAccess.selectArtefactsByID(artefactId);

		feature = trackingService.trackDeletedArtefact(xmlArtefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		List<Artefact> artefactList = artefactAccess.selectArtefactsByFeatureID(featureId);

		// find the artefact and remove it
		for (Artefact a : artefactList) {
			if (a.getId().equals(xmlArtefact.getId())) {
				feature.removeArtefact(a);
				featureAccess.saveFeature(feature);
				artefactAccess.deleteArtefact(xmlArtefact);
			}
		}

		return REDIRECT_FEATURE_ID + featureId;
	}
}
