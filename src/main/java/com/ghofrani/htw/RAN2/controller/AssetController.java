package com.ghofrani.htw.RAN2.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Import spring framework libs
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.service.IUserService;
import com.ghofrani.htw.RAN2.controller.service.TrackingService;
import com.ghofrani.htw.RAN2.database.ArtefactAccess;
import com.ghofrani.htw.RAN2.database.AssetAccess;
import com.ghofrani.htw.RAN2.database.FeatureAccess;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.Artefact;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.AudioArtefact;
import com.ghofrani.htw.RAN2.model.Feature;
import com.ghofrani.htw.RAN2.model.Metadata;
import com.ghofrani.htw.RAN2.model.OtherArtefact;
import com.ghofrani.htw.RAN2.model.PictureArtefact;
import com.ghofrani.htw.RAN2.model.TextArtefact;
import com.ghofrani.htw.RAN2.model.Tracking;
import com.ghofrani.htw.RAN2.model.User;
import com.ghofrani.htw.RAN2.model.VideoArtefact;
import com.ghofrani.htw.RAN2.model.XMLArtefact;

@Controller
public class AssetController {

	private static final String REDIRECT = "redirect:asset?id=";
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private AssetAccess assetAccess;

	@Autowired
	private FeatureAccess featureAccess;

	@Autowired
	private ArtefactAccess artefactAccess;

	@Autowired
	IUserService userService;

	@Autowired
	UserAccess userAccess;

	@Autowired
	TrackingService trackingService;

	/**
	 * Called to generate AssetView
	 * 
	 * @param id
	 *            ID of the asset to be shown
	 * @param featureId
	 *            ID of the current opened Feature, null when no Feature is opened
	 * @param artefactId
	 *            ID artefact currently shown, null of no artefact is selected
	 * @param AssetDuplicateTitleError
	 *            if not null, show duplicate Title Error Message
	 * @param duplicateTitle
	 *            Title that was duplicated
	 * @param model
	 *            Thymeleaf Model Object
	 * @return Website
	 * @throws Exception
	 *             When assetFile couldn't be read
	 */
	@RequestMapping(path = "/asset", params = "id")
	public String loadAsset(@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "featureId", required = false) Integer featureId,
			@RequestParam(value = "artefactId", required = false) Integer artefactId,
			@RequestParam(value = "AssetDuplicateTitleError", required = false) String AssetDuplicateTitleError,
			@RequestParam(value = "title", required = false) String duplicateTitle, Model model) throws Exception {

		log.debug("Called loadAsset with id={}", id);

		Asset asset = assetAccess.selectAssetsByID(id);

		if (AssetDuplicateTitleError != null) {
			model.addAttribute("showAssetDuplicateTitleError", true);
			model.addAttribute("duplicateTitle", duplicateTitle);
		}

		// Sets spaces instead of underscores(wont be saved to database)
		asset.setDescription(asset.getDescription().replaceAll("_", " "));

		model.addAttribute("asset", asset);
		if (featureId != null) {
			Feature feature = featureAccess.selectFeaturesByID(featureId);
			model.addAttribute("feature", feature);
		}

		// Load Metadata from Asset and fill list
		Map<String, String> metadataMap = asset.getMetadata();

		// Replace UserID with username
		User owner = userAccess.selectUsersByID(Integer.parseInt(metadataMap.get("Uploaded by")));
		metadataMap.put("Uploaded by", owner.getUsername());

		model.addAttribute("filesize", metadataMap.get("Size"));
		model.addAttribute("fileUploader",  metadataMap.get("Uploaded by"));
		model.addAttribute("fileName",  metadataMap.get("Filename"));
		model.addAttribute("fileFormat",  metadataMap.get("FileFormat"));
		model.addAttribute("uploadDate",  metadataMap.get("Uploaded on"));

		// Retrieves List of Features the asset is included in
		List<Feature> usedInFeatures = featureAccess.selectFeaturesByAssetID(asset.getId());
		model.addAttribute("usedInFeatures", usedInFeatures);

		// Load Assetfile
		InputStream fileInStream = asset.getURLResource().getInputStream();

		// Retrieve currently active User
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		User currentUser = userService.loadUser(user.getUsername());

		// Determine if currentUser has editRights
		if (currentUser.getRoles().contains("ROLE_ADMIN") || owner.getId().equals(currentUser.getId())) {
			model.addAttribute("hasEditRights", true);
		}

		Artefact artefact = null;
		if (artefactId != null) {
			artefact = artefactAccess.selectArtefactsByID(artefactId);
			model.addAttribute("artefact", artefact);
		}

		// Load ArtefactEditor for type of Asset/Artefact
		switch (asset.getType()) {
		case PICTURE:
			PictureArtefact pictureArtefact = null;
			if (artefact != null) {
				pictureArtefact = (PictureArtefact) artefact;
			}
			loadImageEditor(model, asset, pictureArtefact);

			break;
		case VIDEO:
			VideoArtefact videoArtefact = null;
			if (artefact != null) {
				videoArtefact = (VideoArtefact) artefact;
			}
			loadVideoEditor(model, asset, videoArtefact);

			break;
		case AUDIO:
			AudioArtefact audioArtefact = null;
			if (artefact != null) {
				audioArtefact = (AudioArtefact) artefact;
			}
			loadAudioEditor(model, asset, audioArtefact);

			break;
		case XML:
			XMLArtefact xmlArtefact = null;
			if (artefact != null) {
				xmlArtefact = (XMLArtefact) artefact;
			}
			loadXMLEditor(model, asset, xmlArtefact);

			break;
		case TEXT:
			TextArtefact textArtefact = null;
			if (artefact != null) {
				textArtefact = (TextArtefact) artefact;
			}
			loadTextEditor(model, asset.getURLResource().getInputStream(), textArtefact);

			break;
		case OTHER:
			OtherArtefact otherArtefact = null;
			if (artefact != null) {
				otherArtefact = (OtherArtefact) artefact;
			}

			if (metadataMap.get("FileFormat").equals("pdf")) {
				model.addAttribute("pdfSrc", asset.getURL());
				model.addAttribute("assetIsPDF", true);
			}

			model.addAttribute("assetIsOther", true);

			break;
		default:
			throw new Exception("AssetType undefined");
		}

		// Load all tracking information of the project
		List<Tracking> trackingList = asset.getTrackingList();
		String[] history = new String[trackingList.size()];
		Tracking tempTrack = null;
		for (int i = 0; i < history.length; i++) {
			tempTrack = trackingList.get(i);
			history[i] = tempTrack.getText();
		}

		model.addAttribute("history", history);

		return "asset";
	}

	/**
	 * Method is called to display the xmlAssetEditor when the asset is an XML file
	 * 
	 * @param model
	 *            Model of the current controller
	 * @param artefact
	 *            The xml-Artefact to be displayed
	 * @param asset
	 *            The asset currently viewed
	 * @throws IOException
	 *             isThrown when xml couldn't be parsed
	 * @author Robert Völkner
	 * @author Jannik Groeger
	 */
	public void loadXMLEditor(Model model, Asset asset, XMLArtefact artefact) throws IOException {
		model.addAttribute("assetIsXML", true);

		String xmlContent = new BufferedReader(new InputStreamReader(asset.getURLResource().getInputStream())).lines()
				.parallel().collect(Collectors.joining("\n"));

		model.addAttribute("xmlContent", xmlContent);

		if (artefact != null) {
			model.addAttribute("nodes", artefact.getNodes());
		}

	}

	/**
	 * Method is called to display the imageAssetEditor when the asset is an Image
	 * 
	 * @param model
	 *            Model of the current controller
	 * @param artefact
	 *            The image-artefct to be displayed
	 * @param asset
	 *            asset to be shown
	 * @author Jannik Groeger
	 */
	public void loadImageEditor(Model model, Asset asset, PictureArtefact artefact) {
		model.addAttribute("assetIsImage", true);

		// Set Imagedata
		model.addAttribute("imageData", asset.getURL());

		if (artefact != null) {
			model.addAttribute("x", artefact.getX());
			model.addAttribute("y", artefact.getY());
			model.addAttribute("w", artefact.getWidth());
			model.addAttribute("h", artefact.getHeight());
		}

	}

	/**
	 * Method is called to display the textAssetEditor when the asset is a text.
	 * 
	 * @param model
	 *            model of the current controller
	 * @param textFile
	 *            textStream to be displayed
	 * @param artefact
	 *            the TextArtefact to be displayed
	 * @author Lukas Beckmann
	 */
	public void loadTextEditor(Model model, InputStream textFile, TextArtefact artefact) {
		model.addAttribute("assetIsText", true);

		String text = new BufferedReader(new InputStreamReader(textFile)).lines().parallel()
				.collect(Collectors.joining("\n"));
		model.addAttribute("textData", text);
		if (artefact != null) {
			model.addAttribute("artefactExists", true);
			model.addAttribute("artefactId", artefact.getId());
			model.addAttribute("action", "/changeTextArtefact");
			model.addAttribute("startPos", artefact.getStart());
			model.addAttribute("endPos", artefact.getEnd());
			model.addAttribute("title", artefact.getTitle());
		} else {
			model.addAttribute("artefactExists", false);
			model.addAttribute("action", "addTextArtefact");
		}

	}

	/**
	 * returns the representation of a String containing milliseconds in the form:
	 * hh:mm:ss:ms
	 * 
	 * @param msAsString
	 *            milliseconds as String
	 * @return A String of the format hh:mm:ss.ms
	 */
	private String getStringRepresentation(String msAsString) {
		int ms = Integer.parseInt(msAsString);
		return String.format("%02d:%02d.%03d", TimeUnit.MILLISECONDS.toMinutes(ms),
				TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)),
				ms % 1000);

	}

	/**
	 * Method is called to set the model attributes for the video editor.
	 * 
	 * @param model
	 *            model of the current controller.
	 * @param asset
	 *            asset to load
	 * @param videoArtefact
	 *            The videoArtefact to be shown
	 * @author Lukas Beckmann
	 */
	public void loadVideoEditor(Model model, Asset asset, VideoArtefact videoArtefact) {
		model.addAttribute("assetIsVideo", true);
		model.addAttribute("videoSource", asset.getURL());
		if (videoArtefact != null) {
			model.addAttribute("artefactExists", true);
			model.addAttribute("artefactId", videoArtefact.getId());
			model.addAttribute("action", "/changeVideoArtefact");
			model.addAttribute("startTime", videoArtefact.getStart());
			model.addAttribute("startTimeAsText", getStringRepresentation(videoArtefact.getStart()));
			model.addAttribute("endTime", videoArtefact.getEnd());
			model.addAttribute("endTimeAsText", getStringRepresentation(videoArtefact.getEnd()));
			model.addAttribute("title", videoArtefact.getTitle());
		} else {
			model.addAttribute("artefactExists", false);
			model.addAttribute("action", "addVideoArtefact");
			model.addAttribute("startTime", 0);
			model.addAttribute("startTimeAsText", "00:00.000");
			model.addAttribute("endTime", 0);
			model.addAttribute("endTimeAsText", "00:00.000");
		}
	}

	/**
	 * Method is called to set the model attributes for the audio editor.
	 * 
	 * @param model
	 *            model of the current controller.
	 * @param asset
	 *            asset to load.
	 * @param audioArtefact
	 *            The audio-artefact to be shown
	 * @author Lukas Beckmann
	 */
	public void loadAudioEditor(Model model, Asset asset, AudioArtefact audioArtefact) {
		model.addAttribute("assetIsAudio", true);
		model.addAttribute("audioSource", asset.getURL());
		if (audioArtefact != null) {
			model.addAttribute("artefactExists", true);
			model.addAttribute("artefactId", audioArtefact.getId());
			model.addAttribute("action", "/changeAudioArtefact");
			model.addAttribute("startTime", audioArtefact.getStart());
			model.addAttribute("startTimeAsText", getStringRepresentation(audioArtefact.getStart()));
			model.addAttribute("endTime", audioArtefact.getEnd());
			model.addAttribute("endTimeAsText", getStringRepresentation(audioArtefact.getEnd()));
			model.addAttribute("title", audioArtefact.getTitle());
		} else {
			model.addAttribute("artefactExists", false);
			model.addAttribute("action", "addAudioArtefact");
			model.addAttribute("startTime", 0);
			model.addAttribute("startTimeAsText", "00:00.000");
			model.addAttribute("endTime", 0);
			model.addAttribute("endTimeAsText", "00:00.000");
		}

	}

	/**
	 * 
	 * Edits the title and description of the current asset.
	 * 
	 * @param assetId
	 *            The id of the current Project.
	 * @param title
	 *            The new title.
	 * @param description
	 *            The new description.
	 * @param featureId
	 *            ID of the current feature, null for none selected
	 * @param artefactId
	 *            ID of the current Artefact
	 * @param httpRequest
	 *            httpRequest Object
	 * @return A redirect to the AssetView of the current Asset, null for none
	 *         selected
	 */
	@PostMapping("/editasset")
	public String editAsset(@RequestParam(value = "id", required = true) Integer assetId,
			@RequestParam(value = "title", required = true) String title,
			@RequestParam(value = "description") String description,
			@RequestParam(value = "featureId", required = false) Integer featureId,
			@RequestParam(value = "artefactId", required = false) Integer artefactId, HttpServletRequest httpRequest) {

		log.debug("Called editAsset with id={} title={} description={}", assetId, title, description);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		// Retrieve currently active User
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		User currentUser = userService.loadUser(user.getUsername());

		List<Asset> assetList = assetAccess.selectAssets();

		for (Asset a : assetList) {
			// int userID = Integer.parseInt(a.getMetadata().get("Uploaded by"));
			// Not yet finally decided if names should be unique only to user or not
			// if(userID == currentUser.getId()) {

			if (!(a.getId().equals(asset.getId())) && a.getTitle().equals(title)) {
				return "redirect:asset?id=" + asset.getId() + "&AssetDuplicateTitleError=true&title=" + title;
			}
		}

		asset.setTitle(title);
		asset.setDescription(description);

		asset = trackingService.trackAssetInformation(asset, httpRequest);

		assetAccess.saveAsset(asset);

		String url = REDIRECT + assetId;

		if (featureId != null) {
			url += "&featureId=" + featureId;
		}

		if (artefactId != null) {
			url += "&artefactId=" + artefactId;
		}

		return url;
	}

	/**
	 * Deletes an existing Asset
	 * 
	 * @param assetId
	 *            The id of the Asset
	 * @return A redirect to the Feature View of the current Project.
	 */
	@PostMapping("/deleteasset")
	public String deleteAsset(@RequestParam(value = "id", required = true) Integer assetId) {

		log.debug("Called deleteAsset with id={}", assetId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		assetAccess.deleteAsset(asset);

		return "redirect:overview";
	}
}
