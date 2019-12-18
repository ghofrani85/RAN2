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
import com.ghofrani.htw.RAN2.model.OtherFile;

/**
 * @author Jannik Gröger
 *
 */

@Controller
public class OtherFileController {

	private static final String REDIRECT_FEATURE_ID = "redirect:folder?id=";

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
	 * Adds a new File of type Other
	 * @param folderId ID of the folder associated with the file
	 * @param title Title of the new File
	 * @param assetId ID of the asset associated with the file
	 * @param httpRequest Request Object
	 * @return Redirect to FolderView
	 */
	@PostMapping("/addOtherFile")
	public String addOtherFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assetId") Integer assetId, HttpServletRequest httpRequest) {

		log.info("Called addOtherFile with folderId={}, assetId={}", folderId, assetId);

		Folder folder = folderAccess.selectFoldersByID(folderId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		// Check for duplicate File titles in folder
		List<File> files = folder.getFileList();

		for (File a : files) {
			if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FEATURE_ID + folderId + "&FileDuplicateTitleError=true&title=" + title;
			}
		}

		OtherFile otherFile = new OtherFile(null, title, asset, "", "");
		folder.addFile(otherFile);
		otherFile.setFolder(folder);

		folder = trackingService.trackAddedFile(otherFile, folder, httpRequest);

		folderService.notifyChildren(folder);

		folderAccess.saveFolder(folder);

		return REDIRECT_FEATURE_ID + folderId;
	}

	/**
	 * Changes an file of type Other
	 * @param folderId ID of the folder associated with the file
	 * @param title Title of the new File
	 * @param assetId ID of the asset associated with the file
	 * @param httpRequest Request Object
	 * @param fileId ID of the file to be changed
	 * @return Redirect to the folderView 
	 */
	@PostMapping("/changeOtherFile")
	public String changeOtherFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "title") String title, @RequestParam(value = "assetId") Integer assetId,
			@RequestParam(value = "fileId") Integer fileId, HttpServletRequest httpRequest) {

		log.info("Called changeOtherFile with folderId{}, assetId={}, fileId={},", folderId, assetId,
				fileId);

		Folder folder = folderAccess.selectFoldersByID(folderId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		File otherFile = fileAccess.selectFilesByID(fileId);

		List<File> fileList = new ArrayList<>(folder.getFileList());

		for (File a : fileList) {
			if (a.getId().equals(otherFile.getId())) {
				folder.removeFile(otherFile);
				log.info("Removed File: {}, from Folder: {}", otherFile.getTitle(), folder.getTitle());
			} else if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FEATURE_ID + folderId + "&FileDuplicateTitleError=true&title=" + title;
			}
		}

		OtherFile file = new OtherFile(fileId, title, asset, null, null);
		folder.addFile(file);
		file.setFolder(folder);

		folder = trackingService.trackFileInformation(file, folder, httpRequest);

		folderService.notifyChildren(folder);

		folderAccess.saveFolder(folder);

		return REDIRECT_FEATURE_ID + folderId + "&assetId=" + assetId;
	}

	/**
	 * Removes an file of type Other
	 * @param folderId ID of the folder associated with the file
	 * @param fileId ID of the file to be removed
	 * @param httpRequest Request Object
	 * @return Redirect to folderView
	 */
	@PostMapping("/removeOtherFile")
	public String removeOtherFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "fileId") Integer fileId, HttpServletRequest httpRequest) {

		log.info("Called removeOtherFile with folderId={}, fileId={}", folderId, fileId);

		Folder folder = folderAccess.selectFoldersByID(folderId);

		File otherFile = fileAccess.selectFilesByID(fileId);

		folder = trackingService.trackDeletedFile(otherFile, folder, httpRequest);

		folderService.notifyChildren(folder);

		List<File> fileList = fileAccess.selectFilesByFolderID(folderId);

		for (File a : fileList) {
			if (a.getId().equals(otherFile.getId())) {
				folder.removeFile(a);
				folderAccess.saveFolder(folder);
				fileAccess.deleteFile(otherFile);
			}
		}

		return REDIRECT_FEATURE_ID + folderId;
	}
}
