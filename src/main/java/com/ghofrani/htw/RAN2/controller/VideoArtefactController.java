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
import com.ghofrani.htw.RAN2.model.VideoArtefact;

/**
 * This controller handles the add, change and remove operations for artefacts.
 * These can be used with "/addVideoArtefact", "/changeVideoArtefact" and
 * "removeVideoArtefact".
 * 
 * @author Stefan Schmidt
 *
 */
@Controller
public class VideoArtefactController {

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
	 * @param httpRequest httpRequestObject
	 * @return The website url to open
	 */
	@PostMapping("/addVideoArtefact")
	public String addVideoArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assetId") Integer assetId, @RequestParam(value = "start") String start,
			@RequestParam(value = "end") String end, HttpServletRequest httpRequest) {

		log.info("Called addVideoArtefact with featureId={}, assetId={}, start={}, end={}", featureId, assetId, start,
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

		VideoArtefact videoArtefact = new VideoArtefact(null, title, asset, start, end);
		feature.addArtefact(videoArtefact);
		videoArtefact.setFeature(feature);

		feature = trackingService.trackAddedArtefact(videoArtefact, feature, httpRequest);

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
	 * @param httpRequest httpRequestObject
	 * @return The website url to open
	 */
	@PostMapping("/changeVideoArtefact")
	public String changeVideoArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "title") String title, @RequestParam(value = "assetId") Integer assetId,
			@RequestParam(value = "artefactId") Integer artefactId, @RequestParam(value = "start") String start,
			@RequestParam(value = "end") String end, HttpServletRequest httpRequest) {

		log.info("Called changeVideoArtefact with featureId{}, assetId={}, artefactId={}, start={}, end={}, ",
				featureId, assetId, artefactId, start, end);

		Feature feature = featureAccess.selectFeaturesByID(featureId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		Artefact videoArtefact = artefactAccess.selectArtefactsByID(artefactId);
		List<Artefact> artefacts = new ArrayList<>(feature.getArtefactList());

		for (Artefact a : artefacts) {
			if (a.getId().equals(videoArtefact.getId())) {
				feature.removeArtefact(videoArtefact);
				log.info("Removed Artefact: {}, from Feature: {}", videoArtefact.getTitle(), feature.getTitle());
			} else if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FEATURE_ID + featureId + "&ArtefactDuplicateTitleError=true&title=" + title;
			}
		}

		VideoArtefact artefact = new VideoArtefact(artefactId, title, asset, start, end);
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
	@PostMapping("/removeVideoArtefact")
	public String removeVideoArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "artefactId") Integer artefactId, HttpServletRequest httpRequest) {

		log.info("Called removeVideoArtefact with featureId={}, artefactId={}", featureId, artefactId);

		Feature feature = featureAccess.selectFeaturesByID(featureId);

		Artefact videoArtefact = artefactAccess.selectArtefactsByID(artefactId);

		feature = trackingService.trackDeletedArtefact(videoArtefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		List<Artefact> artefactList = artefactAccess.selectArtefactsByFeatureID(featureId);

		// find the artefact and remove it
		for (Artefact a : artefactList) {
			if (a.getId().equals(videoArtefact.getId())) {
				feature.removeArtefact(a);
				featureAccess.saveFeature(feature);
				artefactAccess.deleteArtefact(videoArtefact);
			}
		}

		return REDIRECT_FEATURE_ID + featureId;
	}
}
