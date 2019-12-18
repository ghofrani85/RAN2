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
import com.ghofrani.htw.RAN2.model.PictureFile;

/**
 * This controller handles the add, change and remove operations for files.
 * These can be used with "/addPictureFile", "/changePictureFile" and
 * "removePictureFile".
 * 
 * @author Stefan Schmidt
 *
 */
@Controller
public class PictureFileController {

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
	 * Creates a new pictureFile
	 * @param folderId The ID of the folder in the database
	 * @param title The title of the file
	 * @param assetId The ID of the asset in the database
	 * @param x X-Offset for image
	 * @param y Y-Offset for image
	 * @param width Width of file
	 * @param height Height of file
	 * @param httpRequest httpRequestObject
	 * @return redirect to Folder
	 */
	@PostMapping("/addPictureFile")
	public String addPictureFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "assetId") Integer assetId, @RequestParam(value = "x") String x,
			@RequestParam(value = "y") String y, @RequestParam(value = "width") String width,
			@RequestParam(value = "height") String height, HttpServletRequest httpRequest) {

		log.info("Called addPictureFile with folderId={}, assetId={}, x={}, y={}, width={}, height={}", folderId,
				assetId, x, y, width, height);

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

		PictureFile pictureFile = new PictureFile(null, title, asset, x, y, width, height);
		folder.addFile(pictureFile);
		pictureFile.setFolder(folder);

		folder = trackingService.trackAddedFile(pictureFile, folder, httpRequest);

		folderService.notifyChildren(folder);

		folderAccess.saveFolder(folder);

		return REDIRECT_FOLDER_ID + folderId;
	}

	/**
	 *  Changes an pictureFile
	 * @param folderId The ID of the folder in the database
	 * @param title The title of the file
	 * @param assetId The ID of the asset in the database
	 * @param fileId The ID of the file in the database
	 * @param x X-Offset for image
	 * @param y Y-Offset for image
	 * @param width Width of file
	 * @param height Height of file
	 * @param httpRequest httpRequestObject
	 * @return redirect to Folder
	 */
	@PostMapping("/changePictureFile")
	public String changePictureFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "title") String title, @RequestParam(value = "assetId") Integer assetId,
			@RequestParam(value = "fileId") Integer fileId, @RequestParam(value = "x") String x,
			@RequestParam(value = "y") String y, @RequestParam(value = "width") String width,
			@RequestParam(value = "height") String height, HttpServletRequest httpRequest) {

		log.info(
				"Called changePictureFile with folderId={}, assetId={}, fileId={}, x={}, y={}, width={}, height={}",
				folderId, assetId, fileId, x, y, width, height);

		Folder folder = folderAccess.selectFoldersByID(folderId);
		Asset asset = assetAccess.selectAssetsByID(assetId);

		File pictureFile = fileAccess.selectFilesByID(fileId);
		List<File> files = new ArrayList<>(folder.getFileList());

		for (File a : files) {
			if (a.getId().equals(pictureFile.getId())) {
				folder.removeFile(pictureFile);
				log.info("Removed File: {}, from Folder: {}", pictureFile.getTitle(), folder.getTitle());
			} else if (a.getTitle().equals(title)) {
				// Duplicate Title, show error Window
				return REDIRECT_FOLDER_ID + folderId + "&FileDuplicateTitleError=true&title=" + title;
			}
		}

		PictureFile file = new PictureFile(fileId, title, asset, x, y, width, height);
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
	@PostMapping("/removePictureFile")
	public String removePictureFile(@RequestParam(value = "folderId") Integer folderId,
			@RequestParam(value = "fileId") Integer fileId, HttpServletRequest httpRequest) {

		log.info("Called removePictureFile with folderId={}, fileId={}", folderId, fileId);

		Folder folder = folderAccess.selectFoldersByID(folderId);

		File pictureFile = fileAccess.selectFilesByID(fileId);

		folder = trackingService.trackDeletedFile(pictureFile, folder, httpRequest);

		folderService.notifyChildren(folder);

		List<File> fileList = fileAccess.selectFilesByFolderID(folderId);

		// find the file and remove it
		for (File a : fileList) {
			if (a.getId().equals(pictureFile.getId())) {
				folder.removeFile(a);
				folderAccess.saveFolder(folder);
				fileAccess.deleteFile(pictureFile);
			}
		}

		return REDIRECT_FOLDER_ID + folderId;
	}
}
