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
import com.ghofrani.htw.RAN2.model.PictureArtefact;

/**
 * This controller handles the add, change and remove operations for artefacts.
 * These can be used with "/addPictureArtefact", "/changePictureArtefact" and
 * "removePictureArtefact".
 * 
 * @author Stefan Schmidt
 *
 */
@Controller
public class PictureArtefactController {

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
	 * Creates a new pictureArtefact
	 * @param featureId The ID of the feature in the database
	 * @param title The title of the artefact
	 * @param assetId The ID of the asset in the database
	 * @param x X-Offset for image
	 * @param y Y-Offset for image
	 * @param width Width of artefact
	 * @param height Height of artefact
	 * @param httpRequest httpRequestObject
	 * @return redirect to Feature
	 */
	@PostMapping("/addPictureArtefact")
	public String addPictureArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assetId") Integer assetId, @RequestParam(value = "x") String x,
			@RequestParam(value = "y") String y, @RequestParam(value = "width") String width,
			@RequestParam(value = "height") String height, HttpServletRequest httpRequest) {

		log.info("Called addPictureArtefact with featureId={}, assetId={}, x={}, y={}, width={}, height={}", featureId,
				assetId, x, y, width, height);

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

		PictureArtefact pictureArtefact = new PictureArtefact(null, title, asset, x, y, width, height);
		feature.addArtefact(pictureArtefact);
		pictureArtefact.setFeature(feature);

		feature = trackingService.trackAddedArtefact(pictureArtefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		featureAccess.saveFeature(feature);

		return REDIRECT_FEATURE_ID + featureId;
	}

	/**
	 *  Changes an pictureArtefact
	 * @param featureId The ID of the feature in the database
	 * @param title The title of the artefact
	 * @param assetId The ID of the asset in the database
	 * @param artefactId The ID of the artefact in the database
	 * @param x X-Offset for image
	 * @param y Y-Offset for image
	 * @param width Width of artefact
	 * @param height Height of artefact
	 * @param httpRequest httpRequestObject
	 * @return redirect to Feature
	 */
	@PostMapping("/changePictureArtefact")
	public String changePictureArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "title") String title, @RequestParam(value = "assetId") Integer assetId,
			@RequestParam(value = "artefactId") Integer artefactId, @RequestParam(value = "x") String x,
			@RequestParam(value = "y") String y, @RequestParam(value = "width") String width,
			@RequestParam(value = "height") String height, HttpServletRequest httpRequest) {

		log.info(
				"Called changePictureArtefact with featureId={}, assetId={}, artefactId={}, x={}, y={}, width={}, height={}",
				featureId, assetId, artefactId, x, y, width, height);

		Feature feature = featureAccess.selectFeaturesByID(featureId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		Artefact pictureArtefact = artefactAccess.selectArtefactsByID(artefactId);
		List<Artefact> artefacts = new ArrayList<>(feature.getArtefactList());

		for (Artefact a : artefacts) {
			if (a.getId().equals(pictureArtefact.getId())) {
				feature.removeArtefact(pictureArtefact);
				log.info("Removed Artefact: {}, from Feature: {}", pictureArtefact.getTitle(), feature.getTitle());
			} else if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FEATURE_ID + featureId + "&ArtefactDuplicateTitleError=true&title=" + title;
			}
		}

		PictureArtefact artefact = new PictureArtefact(artefactId, title, asset, x, y, width, height);
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
	@PostMapping("/removePictureArtefact")
	public String removePictureArtefact(@RequestParam(value = "featureId") Integer featureId,
			@RequestParam(value = "artefactId") Integer artefactId, HttpServletRequest httpRequest) {

		log.info("Called removePictureArtefact with featureId={}, artefactId={}", featureId, artefactId);

		Feature feature = featureAccess.selectFeaturesByID(featureId);

		Artefact pictureArtefact = artefactAccess.selectArtefactsByID(artefactId);

		feature = trackingService.trackDeletedArtefact(pictureArtefact, feature, httpRequest);

		featureService.notifyChildren(feature);

		List<Artefact> artefactList = artefactAccess.selectArtefactsByFeatureID(featureId);

		// find the artefact and remove it
		for (Artefact a : artefactList) {
			if (a.getId().equals(pictureArtefact.getId())) {
				feature.removeArtefact(a);
				featureAccess.saveFeature(feature);
				artefactAccess.deleteArtefact(pictureArtefact);
			}
		}

		return REDIRECT_FEATURE_ID + featureId;
	}
}
