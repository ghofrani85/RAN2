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
import com.ghofrani.htw.RAN2.database.FileAccess;
import com.ghofrani.htw.RAN2.database.AssetAccess;
import com.ghofrani.htw.RAN2.database.FolderAccess;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.AudioFile;
import com.ghofrani.htw.RAN2.model.Folder;
import com.ghofrani.htw.RAN2.model.Metadata;
import com.ghofrani.htw.RAN2.model.OtherFile;
import com.ghofrani.htw.RAN2.model.PictureFile;
import com.ghofrani.htw.RAN2.model.TextFile;
import com.ghofrani.htw.RAN2.model.Tracking;
import com.ghofrani.htw.RAN2.model.User;
import com.ghofrani.htw.RAN2.model.VideoFile;
import com.ghofrani.htw.RAN2.model.XMLFile;

@Controller
public class AssetController {

	private static final String REDIRECT = "redirect:asset?id=";
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private AssetAccess assetAccess;

	@Autowired
	private FolderAccess folderAccess;

	@Autowired
	private FileAccess fileAccess;

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
	 * @param folderId
	 *            ID of the current opened Folder, null when no Folder is opened
	 * @param fileId
	 *            ID file currently shown, null of no file is selected
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
			@RequestParam(value = "folderId", required = false) Integer folderId,
			@RequestParam(value = "fileId", required = false) Integer fileId,
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
		if (folderId != null) {
			Folder folder = folderAccess.selectFoldersByID(folderId);
			model.addAttribute("folder", folder);
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

		// Retrieves List of Folders the asset is included in
		List<Folder> usedInFolders = folderAccess.selectFoldersByAssetID(asset.getId());
		model.addAttribute("usedInFolders", usedInFolders);

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

		File file = null;
		if (fileId != null) {
			file = fileAccess.selectFilesByID(fileId);
			model.addAttribute("file", file);
		}

		// Load FileEditor for type of Asset/File
		switch (asset.getType()) {
		case PICTURE:
			PictureFile pictureFile = null;
			if (file != null) {
				pictureFile = (PictureFile) file;
			}
			loadImageEditor(model, asset, pictureFile);

			break;
		case VIDEO:
			VideoFile videoFile = null;
			if (file != null) {
				videoFile = (VideoFile) file;
			}
			loadVideoEditor(model, asset, videoFile);

			break;
		case AUDIO:
			AudioFile audioFile = null;
			if (file != null) {
				audioFile = (AudioFile) file;
			}
			loadAudioEditor(model, asset, audioFile);

			break;
		case XML:
			XMLFile xmlFile = null;
			if (file != null) {
				xmlFile = (XMLFile) file;
			}
			loadXMLEditor(model, asset, xmlFile);

			break;
		case TEXT:
			TextFile textFile = null;
			if (file != null) {
				textFile = (TextFile) file;
			}
			loadTextEditor(model, asset.getURLResource().getInputStream(), textFile);

			break;
		case OTHER:
			OtherFile otherFile = null;
			if (file != null) {
				otherFile = (OtherFile) file;
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
	 * @param file
	 *            The xml-File to be displayed
	 * @param asset
	 *            The asset currently viewed
	 * @throws IOException
	 *             isThrown when xml couldn't be parsed
	 * @author Robert Völkner
	 * @author Jannik Groeger
	 */
	public void loadXMLEditor(Model model, Asset asset, XMLFile file) throws IOException {
		model.addAttribute("assetIsXML", true);

		String xmlContent = new BufferedReader(new InputStreamReader(asset.getURLResource().getInputStream())).lines()
				.parallel().collect(Collectors.joining("\n"));

		model.addAttribute("xmlContent", xmlContent);

		if (file != null) {
			model.addAttribute("nodes", file.getNodes());
		}

	}

	/**
	 * Method is called to display the imageAssetEditor when the asset is an Image
	 * 
	 * @param model
	 *            Model of the current controller
	 * @param file
	 *            The image-artefct to be displayed
	 * @param asset
	 *            asset to be shown
	 * @author Jannik Groeger
	 */
	public void loadImageEditor(Model model, Asset asset, PictureFile file) {
		model.addAttribute("assetIsImage", true);

		// Set Imagedata
		model.addAttribute("imageData", asset.getURL());

		if (file != null) {
			model.addAttribute("x", file.getX());
			model.addAttribute("y", file.getY());
			model.addAttribute("w", file.getWidth());
			model.addAttribute("h", file.getHeight());
		}

	}

	/**
	 * Method is called to display the textAssetEditor when the asset is a text.
	 * 
	 * @param model
	 *            model of the current controller
	 * @param textFile
	 *            textStream to be displayed
	 * @param file
	 *            the TextFile to be displayed
	 * @author Lukas Beckmann
	 */
	public void loadTextEditor(Model model, InputStream textFile, TextFile file) {
		model.addAttribute("assetIsText", true);

		String text = new BufferedReader(new InputStreamReader(textFile)).lines().parallel()
				.collect(Collectors.joining("\n"));
		model.addAttribute("textData", text);
		if (file != null) {
			model.addAttribute("fileExists", true);
			model.addAttribute("fileId", file.getId());
			model.addAttribute("action", "/changeTextFile");
			model.addAttribute("startPos", file.getStart());
			model.addAttribute("endPos", file.getEnd());
			model.addAttribute("title", file.getTitle());
		} else {
			model.addAttribute("fileExists", false);
			model.addAttribute("action", "addTextFile");
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
	 * @param videoFile
	 *            The videoFile to be shown
	 * @author Lukas Beckmann
	 */
	public void loadVideoEditor(Model model, Asset asset, VideoFile videoFile) {
		model.addAttribute("assetIsVideo", true);
		model.addAttribute("videoSource", asset.getURL());
		if (videoFile != null) {
			model.addAttribute("fileExists", true);
			model.addAttribute("fileId", videoFile.getId());
			model.addAttribute("action", "/changeVideoFile");
			model.addAttribute("startTime", videoFile.getStart());
			model.addAttribute("startTimeAsText", getStringRepresentation(videoFile.getStart()));
			model.addAttribute("endTime", videoFile.getEnd());
			model.addAttribute("endTimeAsText", getStringRepresentation(videoFile.getEnd()));
			model.addAttribute("title", videoFile.getTitle());
		} else {
			model.addAttribute("fileExists", false);
			model.addAttribute("action", "addVideoFile");
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
	 * @param audioFile
	 *            The audio-file to be shown
	 * @author Lukas Beckmann
	 */
	public void loadAudioEditor(Model model, Asset asset, AudioFile audioFile) {
		model.addAttribute("assetIsAudio", true);
		model.addAttribute("audioSource", asset.getURL());
		if (audioFile != null) {
			model.addAttribute("fileExists", true);
			model.addAttribute("fileId", audioFile.getId());
			model.addAttribute("action", "/changeAudioFile");
			model.addAttribute("startTime", audioFile.getStart());
			model.addAttribute("startTimeAsText", getStringRepresentation(audioFile.getStart()));
			model.addAttribute("endTime", audioFile.getEnd());
			model.addAttribute("endTimeAsText", getStringRepresentation(audioFile.getEnd()));
			model.addAttribute("title", audioFile.getTitle());
		} else {
			model.addAttribute("fileExists", false);
			model.addAttribute("action", "addAudioFile");
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
	 * @param folderId
	 *            ID of the current folder, null for none selected
	 * @param fileId
	 *            ID of the current File
	 * @param httpRequest
	 *            httpRequest Object
	 * @return A redirect to the AssetView of the current Asset, null for none
	 *         selected
	 */
	@PostMapping("/editasset")
	public String editAsset(@RequestParam(value = "id", required = true) Integer assetId,
			@RequestParam(value = "title", required = true) String title,
			@RequestParam(value = "description") String description,
			@RequestParam(value = "folderId", required = false) Integer folderId,
			@RequestParam(value = "fileId", required = false) Integer fileId, HttpServletRequest httpRequest) {

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

		if (folderId != null) {
			url += "&folderId=" + folderId;
		}

		if (fileId != null) {
			url += "&fileId=" + fileId;
		}

		return url;
	}

	/**
	 * Deletes an existing Asset
	 * 
	 * @param assetId
	 *            The id of the Asset
	 * @return A redirect to the Folder View of the current Project.
	 */
	@PostMapping("/deleteasset")
	public String deleteAsset(@RequestParam(value = "id", required = true) Integer assetId) {

		log.debug("Called deleteAsset with id={}", assetId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		assetAccess.deleteAsset(asset);

		return "redirect:overview";
	}
}
