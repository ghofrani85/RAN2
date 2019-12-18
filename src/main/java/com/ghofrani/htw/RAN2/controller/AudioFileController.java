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
import com.ghofrani.htw.RAN2.model.AudioFile;
import com.ghofrani.htw.RAN2.model.Folder;

/**
 * This controller handles the add, change and remove operations for files.
 * These can be used with "/addAudioFile", "/changeAudioFile" and
 * "removeAudioFile".
 * 
 * @author Stefan Schmidt
 *
 */
@Controller
public class AudioFileController {

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
	 * @param folderId The ID of the folder in the database
	 * @param title The title of the file
	 * @param assetId The ID of the asset in the database
	 * @param start The start of the audio file
	 * @param end The end of the audio file
	 * @param httpRequest RequestObject
	 * @return The website url to open
	 */
	@PostMapping("/addAudioFile")
	public String addAudioFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assetId") Integer assetId, @RequestParam(value = "start") String start,
			@RequestParam(value = "end") String end, HttpServletRequest httpRequest) {

		log.info("Called addAudioFile with folderId={}, assetId={}, start={}, end={}", folderId, assetId, start,
				end);

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

		AudioFile audioFile = new AudioFile(null, title, asset, start, end);
		folder.addFile(audioFile);
		audioFile.setFolder(folder);

		folder = trackingService.trackAddedFile(audioFile, folder, httpRequest);

		folderService.notifyChildren(folder);

		folderAccess.saveFolder(folder);

		return REDIRECT_FEATURE_ID + folderId;
	}
	
	/**
	 * @param assetId The ID of the asset in the database
	 * @param folderId The ID of the folder in the database
	 * @param title The title of the file
	 * @param fileId The ID of the file in the database
	 * @param start The start of the audio file
	 * @param end The end of the audio file
	 * @param httpRequest RequestObject
	 * @return The website url to open
	 */
	@PostMapping("/changeAudioFile")
	public String changeAudioFile(@RequestParam(value = "assetId") Integer assetId,
			@RequestParam(value = "folderId") Integer folderId, @RequestParam(value = "title") String title,
			@RequestParam(value = "fileId") Integer fileId, @RequestParam(value = "start") String start,
			@RequestParam(value = "end") String end, HttpServletRequest httpRequest) {

		log.info("Called changeAudioFile with fileId={}, start={}, end={}", fileId, start, end);

		Folder folder = folderAccess.selectFoldersByID(folderId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		File audioFile = fileAccess.selectFilesByID(fileId);
		List<File> files = new ArrayList<>(folder.getFileList());

		for (File a : files) {
			if (a.getId().equals(audioFile.getId())) {
				folder.removeFile(audioFile);
				log.info("Removed File: {}, from Folder: {}", audioFile.getTitle(), folder.getTitle());
			} else if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FEATURE_ID + folderId + "&FileDuplicateTitleError=true&title=" + title;
			}
		}

		AudioFile file = new AudioFile(fileId, title, asset, start, end);
		folder.addFile(file);
		file.setFolder(folder);

		folder = trackingService.trackFileInformation(file, folder, httpRequest);

		folderService.notifyChildren(folder);

		folderAccess.saveFolder(folder);

		return REDIRECT_FEATURE_ID + folderId + "&assetId=" + assetId;
	}

	
	/**
	 * @param folderId The ID of the folder in the database
	 * @param fileId The ID of the file in the database
	 * @param httpRequest Request Object
	 * @return the website url to open
	 */
	@PostMapping("/removeAudioFile")
	public String removeAudioController(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "fileId") Integer fileId, HttpServletRequest httpRequest) {

		log.info("Called removeAudioFile with fileId={}", fileId);

		Folder folder = folderAccess.selectFoldersByID(folderId);

		File audioFile = fileAccess.selectFilesByID(fileId);

		folder = trackingService.trackDeletedFile(audioFile, folder, httpRequest);

		folderService.notifyChildren(folder);

		List<File> fileList = fileAccess.selectFilesByFolderID(folderId);

		// find the file and remove it
		for (File a : fileList) {
			if (a.getId().equals(audioFile.getId())) {
				folder.removeFile(a);
				folderAccess.saveFolder(folder);
				fileAccess.deleteFile(audioFile);
			}
		}

		return REDIRECT_FEATURE_ID + folderId;
	}
}
