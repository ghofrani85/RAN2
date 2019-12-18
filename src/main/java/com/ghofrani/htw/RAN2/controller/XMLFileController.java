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
import com.ghofrani.htw.RAN2.controller.service.FolderService;
import com.ghofrani.htw.RAN2.controller.service.TrackingService;
import com.ghofrani.htw.RAN2.database.FileAccess;
import com.ghofrani.htw.RAN2.database.AssetAccess;
import com.ghofrani.htw.RAN2.database.FolderAccess;
import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Folder;
import com.ghofrani.htw.RAN2.model.XMLFile;

/**
 * This controller handles the add, change and remove operations for files.
 * These can be used with "/addXMLFile", "/changeXMLFile" and
 * "removeXMLFile".
 * 
 * @author Stefan Schmidt
 *
 */
@Controller
public class XMLFileController {

	private static final String REDIRECT_FOLDER_ID = "redirect:folder?id=";

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private FolderService folderService;

	@Autowired
	private TrackingService trackingService;

	@Autowired
	private FolderAccess folderAccess;

	@Autowired
	private AssetAccess assetAccess;

	@Autowired
	private FileAccess fileAccess;

	/**
	 * @param folderId The ID of the folder in the database
	 * @param title The title of the file
	 * @param assetId The ID of the asset in the database
	 * @param nodes the Nodes data
	 * @param httpRequest httpRequestObject
	 * @return The website url to open
	 */
	@PostMapping("/addXMLFile")
	public String addXMLFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assetId") Integer assetId, @RequestParam(value = "nodes") String nodes,
			HttpServletRequest httpRequest) {

		log.info("Called addXMLFile with folderId={}, assetId={}, nodes={}", folderId, assetId, nodes);

		Folder folder = folderAccess.selectFoldersByID(folderId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		// Check for duplicate File titles in folder
		List<File> files = folder.getFileList();

		for (File a : files) {
			if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FOLDER_ID + folderId + "&FileDuplicateTitleError=true&title=" + title;
			}
		}

		XMLFile xmlFile = new XMLFile(null, title, asset, nodes);
		folder.addFile(xmlFile);
		xmlFile.setFolder(folder);

		folder = trackingService.trackAddedFile(xmlFile, folder, httpRequest);

		folderService.notifyChildren(folder);

		folderAccess.saveFolder(folder);

		return REDIRECT_FOLDER_ID + folderId;
	}

	/**
	 * @param assetId The ID of the asset in the database
	 * @param folderId The ID of the folder in the database
	 * @param title The title of the file
	 * @param fileId The ID of the file in the database
	 * @param nodes The new nodes data
	 * @param httpRequest httpRequestObject
	 * @return The website url to open
	 */
	@PostMapping("/changeXMLFile")
	public String changeXMLFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "title") String title, @RequestParam(value = "assetId") Integer assetId,
			@RequestParam(value = "fileId") Integer fileId, @RequestParam(value = "nodes") String nodes,
			HttpServletRequest httpRequest) {

		log.info("Called changeXMLFile with folderId={}, assetId={}, fileId={}, nodes={}", folderId, assetId,
				fileId, nodes);

		Folder folder = folderAccess.selectFoldersByID(folderId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		File xmlFile = fileAccess.selectFilesByID(fileId);
		List<File> files = new ArrayList<>(folder.getFileList());

		// Check if newTitle is aready used by other file in folder
		for (File a : files) {
			if (a.getId().equals(xmlFile.getId())) {
				folder.removeFile(xmlFile);
				log.info("Removed File: {}, from Folder: {}", xmlFile.getTitle(), folder.getTitle());
			} else if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FOLDER_ID + folderId + "&FileDuplicateTitleError=true&title=" + title;
			}
		}

		XMLFile file = new XMLFile(fileId, title, asset, nodes);
		folder.addFile(file);
		file.setFolder(folder);

		folder = trackingService.trackFileInformation(file, folder, httpRequest);

		folderService.notifyChildren(folder);

		folderAccess.saveFolder(folder);

		return REDIRECT_FOLDER_ID + folderId + "&assetId=" + assetId;
	}

	/**
	 * @param folderId The ID of the folder in the database
	 * @param fileId The ID of the file in the database
	 * @param httpRequest httpRequestObject
	 * @return the website url to open
	 */
	@PostMapping("/removeXMLFile")
	public String removeXMLFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "fileId") Integer fileId, HttpServletRequest httpRequest) {

		log.info("Called removeXMLFile with folderId={}, fileId={}", folderId, fileId);

		Folder folder = folderAccess.selectFoldersByID(folderId);

		File xmlFile = fileAccess.selectFilesByID(fileId);

		folder = trackingService.trackDeletedFile(xmlFile, folder, httpRequest);

		folderService.notifyChildren(folder);

		List<File> fileList = fileAccess.selectFilesByFolderID(folderId);

		// find the file and remove it
		for (File a : fileList) {
			if (a.getId().equals(xmlFile.getId())) {
				folder.removeFile(a);
				folderAccess.saveFolder(folder);
				fileAccess.deleteFile(xmlFile);
			}
		}

		return REDIRECT_FOLDER_ID + folderId;
	}
}
