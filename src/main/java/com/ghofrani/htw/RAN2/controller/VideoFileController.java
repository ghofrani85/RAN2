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
import com.ghofrani.htw.RAN2.model.VideoFile;

/**
 * This controller handles the add, change and remove operations for files.
 * These can be used with "/addVideoFile", "/changeVideoFile" and
 * "removeVideoFile".
 * 
 * @author Stefan Schmidt
 *
 */
@Controller
public class VideoFileController {

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
	 * @param start The start of the audio file
	 * @param end The end of the audio file
	 * @param httpRequest httpRequestObject
	 * @return The website url to open
	 */
	@PostMapping("/addVideoFile")
	public String addVideoFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assetId") Integer assetId, @RequestParam(value = "start") String start,
			@RequestParam(value = "end") String end, HttpServletRequest httpRequest) {

		log.info("Called addVideoFile with folderId={}, assetId={}, start={}, end={}", folderId, assetId, start,
				end);

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

		VideoFile videoFile = new VideoFile(null, title, asset, start, end);
		folder.addFile(videoFile);
		videoFile.setFolder(folder);

		folder = trackingService.trackAddedFile(videoFile, folder, httpRequest);

		folderService.notifyChildren(folder);

		folderAccess.saveFolder(folder);

		return REDIRECT_FOLDER_ID + folderId;
	}

	/**
	 * @param assetId The ID of the asset in the database
	 * @param folderId The ID of the folder in the database
	 * @param title The title of the file
	 * @param fileId The ID of the file in the database
	 * @param start The start of the audio file
	 * @param end The end of the audio file
	 * @param httpRequest httpRequestObject
	 * @return The website url to open
	 */
	@PostMapping("/changeVideoFile")
	public String changeVideoFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "title") String title, @RequestParam(value = "assetId") Integer assetId,
			@RequestParam(value = "fileId") Integer fileId, @RequestParam(value = "start") String start,
			@RequestParam(value = "end") String end, HttpServletRequest httpRequest) {

		log.info("Called changeVideoFile with folderId{}, assetId={}, fileId={}, start={}, end={}, ",
				folderId, assetId, fileId, start, end);

		Folder folder = folderAccess.selectFoldersByID(folderId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		File videoFile = fileAccess.selectFilesByID(fileId);
		List<File> files = new ArrayList<>(folder.getFileList());

		for (File a : files) {
			if (a.getId().equals(videoFile.getId())) {
				folder.removeFile(videoFile);
				log.info("Removed File: {}, from Folder: {}", videoFile.getTitle(), folder.getTitle());
			} else if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FOLDER_ID + folderId + "&FileDuplicateTitleError=true&title=" + title;
			}
		}

		VideoFile file = new VideoFile(fileId, title, asset, start, end);
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
	@PostMapping("/removeVideoFile")
	public String removeVideoFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "fileId") Integer fileId, HttpServletRequest httpRequest) {

		log.info("Called removeVideoFile with folderId={}, fileId={}", folderId, fileId);

		Folder folder = folderAccess.selectFoldersByID(folderId);

		File videoFile = fileAccess.selectFilesByID(fileId);

		folder = trackingService.trackDeletedFile(videoFile, folder, httpRequest);

		folderService.notifyChildren(folder);

		List<File> fileList = fileAccess.selectFilesByFolderID(folderId);

		// find the file and remove it
		for (File a : fileList) {
			if (a.getId().equals(videoFile.getId())) {
				folder.removeFile(a);
				folderAccess.saveFolder(folder);
				fileAccess.deleteFile(videoFile);
			}
		}

		return REDIRECT_FOLDER_ID + folderId;
	}
}
