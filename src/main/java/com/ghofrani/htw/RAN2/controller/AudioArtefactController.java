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
import com.ghofrani.htw.RAN2.model.AudioArtefact;
import com.ghofrani.htw.RAN2.model.Feature;

/**
 * This controller handles the add, change and remove operations for artefacts.
 * These can be used with "/addAudioArtefact", "/changeAudioArtefact" and
 * "removeAudioArtefact".
 * 
 * @author Stefan Schmidt
 *
 */
@Controller
public class AudioArtefactController {

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
	 * @param start The start of the audio artefact
	 * @param end The end of the audio artefact
	 * @param httpRequest RequestObject
	 * @return The website url to open
	 */
	@PostMapping("/addAudioArtefact")
	public String addAudioArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assetId") Integer assetId, @RequestParam(value = "start") String start,
			@RequestParam(value = "end") String end, HttpServletRequest httpRequest) {

		log.info("Called addAudioArtefact with featureId={}, assetId={}, start={}, end={}", featureId, assetId, start,
				end);

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

		AudioArtefact audioArtefact = new AudioArtefact(null, title, asset, start, end);
		feature.addArtefact(audioArtefact);
		audioArtefact.setFeature(feature);

		feature = trackingService.trackAddedArtefact(audioArtefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		featureAccess.saveFeature(feature);

		return REDIRECT_FEATURE_ID + featureId;
	}
	
	/**
	 * @param assetId The ID of the asset in the database
	 * @param featureId The ID of the feature in the database
	 * @param title The title of the artefact
	 * @param artefactId The ID of the artefact in the database
	 * @param start The start of the audio artefact
	 * @param end The end of the audio artefact
	 * @param httpRequest RequestObject
	 * @return The website url to open
	 */
	@PostMapping("/changeAudioArtefact")
	public String changeAudioArtefact(@RequestParam(value = "assetId") Integer assetId,
			@RequestParam(value = "featureId") Integer featureId, @RequestParam(value = "title") String title,
			@RequestParam(value = "artefactId") Integer artefactId, @RequestParam(value = "start") String start,
			@RequestParam(value = "end") String end, HttpServletRequest httpRequest) {

		log.info("Called changeAudioArtefact with artefactId={}, start={}, end={}", artefactId, start, end);

		Feature feature = featureAccess.selectFeaturesByID(featureId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		Artefact audioArtefact = artefactAccess.selectArtefactsByID(artefactId);
		List<Artefact> artefacts = new ArrayList<>(feature.getArtefactList());

		for (Artefact a : artefacts) {
			if (a.getId().equals(audioArtefact.getId())) {
				feature.removeArtefact(audioArtefact);
				log.info("Removed Artefact: {}, from Feature: {}", audioArtefact.getTitle(), feature.getTitle());
			} else if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FEATURE_ID + featureId + "&ArtefactDuplicateTitleError=true&title=" + title;
			}
		}

		AudioArtefact artefact = new AudioArtefact(artefactId, title, asset, start, end);
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
	 * @param httpRequest Request Object
	 * @return the website url to open
	 */
	@PostMapping("/removeAudioArtefact")
	public String removeAudioController(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "artefactId") Integer artefactId, HttpServletRequest httpRequest) {

		log.info("Called removeAudioArtefact with artefactId={}", artefactId);

		Feature feature = featureAccess.selectFeaturesByID(featureId);

		Artefact audioArtefact = artefactAccess.selectArtefactsByID(artefactId);

		feature = trackingService.trackDeletedArtefact(audioArtefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		List<Artefact> artefactList = artefactAccess.selectArtefactsByFeatureID(featureId);

		// find the artefact and remove it
		for (Artefact a : artefactList) {
			if (a.getId().equals(audioArtefact.getId())) {
				feature.removeArtefact(a);
				featureAccess.saveFeature(feature);
				artefactAccess.deleteArtefact(audioArtefact);
			}
		}

		return REDIRECT_FEATURE_ID + featureId;
	}
}
