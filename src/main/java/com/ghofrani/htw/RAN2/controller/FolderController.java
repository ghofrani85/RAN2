package com.ghofrani.htw.RAN2.controller;

import java.util.List;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

// Import spring framework libs
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import com.ghofrani.htw.RAN2.controller.service.FolderService;
import com.ghofrani.htw.RAN2.controller.service.IUserService;
import com.ghofrani.htw.RAN2.controller.service.TrackingService;
import com.ghofrani.htw.RAN2.database.FileAccess;
import com.ghofrani.htw.RAN2.database.AssetAccess;
import com.ghofrani.htw.RAN2.database.FolderAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.database.TrackingAccess;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Folder;
import com.ghofrani.htw.RAN2.model.Project;
import com.ghofrani.htw.RAN2.model.Tracking;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Folder Controller for the Folder View
 * 
 * 
 * 
 * @author Jannik Groeger
 *
 */
@Controller
public class FolderController {

	private static final String REDIRECT = "redirect:folder?id=";

	@Autowired
	private ProjectAccess projectAccess;

	@Autowired
	private FolderAccess folderAccess;

	@Autowired
	private AssetAccess assetAccess;

	@Autowired
	private FileAccess fileAccess;

	@Autowired
	IUserService userService;

	@Autowired
	UserAccess userAccess;

	@Autowired
	TrackingService trackingService;
	
	@Autowired
	FolderService folderService; // Added

	@Autowired
	private MessageSource messages;

	/**
	 * Database Interface for Tracking.
	 */
	@Autowired
	TrackingAccess trackingAccess;

	/**
	 * Loads the FolderView
	 * 
	 * @param id
	 *            ID of the Folder that is loaded
	 * @param AssetDuplicateTitleError
	 *            if not null, an AssetDuplicateTitleError was thrown
	 * @param FolderDuplicateTitleError
	 *            if not null, an FolderDuplicateTitleError was thrown
	 * @param FileDuplicateTitleError
	 *            if not null, an FileDuplicateTitleError was thrown
	 * @param DailyUploadVolumeReached
	 *            if not null, an DailyUploadVolumeReached was thrown
	 * @param FileTooBig
	 *            if not null, an FileTooBigError was thrown
	 * @param invalidFileError
	 *            if not null, an invalidFileError was thrown
	 * @param invalidURLError
	 *            if not null, an invalidURLError was thrown
	 * @param request
	 *            Request Object for the View
	 * @param duplicateTitle
	 *            Title that is duplicate
	 * @param model
	 *            Model Object for the View
	 * @return The loaded View
	 */
	@RequestMapping(path = "/folder", params = "id")
	public String loadFolder(@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "AssetDuplicateTitleError", required = false) String AssetDuplicateTitleError,
			@RequestParam(value = "FolderDuplicateTitleError", required = false) String FolderDuplicateTitleError,
			@RequestParam(value = "FileDuplicateTitleError", required = false) String FileDuplicateTitleError,
			@RequestParam(value = "DailyUploadVolumeReached", required = false) String DailyUploadVolumeReached,
			@RequestParam(value = "FileTooBig", required = false) String FileTooBig,
			@RequestParam(value = "invalidFileError", required = false) String invalidFileError,
			@RequestParam(value = "invalidURLError", required = false) String invalidURLError, WebRequest request,
			@RequestParam(value = "title", required = false) String duplicateTitle, Model model) {

		if (AssetDuplicateTitleError != null) {
			model.addAttribute("showAssetDuplicateTitleError", true);
			model.addAttribute("duplicateTitle", duplicateTitle);
		}

		if (FolderDuplicateTitleError != null) {
			model.addAttribute("showFolderDuplicateTitleError", true);
			model.addAttribute("duplicateTitle", duplicateTitle);
		}

		if (FileDuplicateTitleError != null) {
			model.addAttribute("showFileDuplicateTitleError", true);
			model.addAttribute("duplicateTitle", duplicateTitle);
		}

		if (DailyUploadVolumeReached != null) {
			String title = messages.getMessage("user.dailyUploadLimitReachedTitle", null, request.getLocale());
			String message = messages.getMessage("user.dailyUploadLimitReached", null, request.getLocale());
			String messagePart2 = messages.getMessage("user.dailyUploadLimitReached2", null, request.getLocale());
			model.addAttribute("showError", true);
			model.addAttribute("errorTitle", title);
			model.addAttribute("errorMessage", message + " " + DailyUploadVolumeReached + " " + messagePart2);
		}

		if (FileTooBig != null) {
			String title = messages.getMessage("user.fileTooBigTitle", null, request.getLocale());
			String message = messages.getMessage("user.fileTooBig", null, request.getLocale());
			model.addAttribute("showError", true);
			model.addAttribute("errorTitle", title);
			model.addAttribute("errorMessage", message + " " + FileTooBig);
		}

		if (invalidFileError != null) {
			model.addAttribute("showInvalidFileError", true);
		}

		if (invalidURLError != null) {
			model.addAttribute("showInvalidURLError", true);
		}

		// Database not yet filled with Folders
		Folder folder = folderAccess.selectFoldersByID(id);

		// Load all tracking information of the folder
		List<Tracking> trackingList = folder.getTrackingList();
		String[] history = new String[0];
		if (trackingList.isEmpty() == false) {
			history = new String[trackingList.size()];
			Tracking tempTrack = null;
			for (int i = 0; i < history.length; i++) {
				tempTrack = trackingList.get(i);
				history[i] = tempTrack.getText();
			}
		}

		// Fetch Files of Folder
		List<File> fileList = fileAccess.selectFilesByFolderID(id);

		// Load all Assets to chooseAsset
		List<Asset> allAssetList = assetAccess.selectAssets();

		// Determine parentProjects
		List<Project> parentProjects = projectAccess.selectProjectsByFolderID(id);
		
		// Determine parentFolder
		List<Folder> parentFolders = folderAccess.selectFolderByFolderFileID(id);

		// Check if link to updated parent folder is needed
		if (folder.isUpdatedparent()) {
			model.addAttribute("parentFolderId", folder.getParent().getId());
		}
		
		// Load all folderFiles ADDED
		List<Folder> folderFileList = folderAccess.selectFolderFilesByFolderID(id);
		
		// Load the number of folderFiles for each featArt ADDED
		for (Folder featArt : folderFileList) {
			List<Folder> subFeatArtList = folderAccess.selectFolderFilesByFolderID(featArt.getId());
			featArt.setNumOfFeatArt(subFeatArtList.size());
		}
		
		model.addAttribute("folder", folder);
		model.addAttribute("files", fileList);
		model.addAttribute("folderFiles", folderFileList); // ADDED
		model.addAttribute("allAssets", allAssetList);
		model.addAttribute("history", history);
		//model.addAttribute("parentProjects", parentProjects);

		// Retrieve currently active User
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		User currentUser = userService.loadUser(user.getUsername());
		
		Project parent;
		if (parentProjects.isEmpty() == false) {
			parent = parentProjects.get(0);
		} else {
			List<Folder> grandparentFolders = folderAccess.selectFolderByFolderFileID(parentFolders.get(0).getId());//(parentFolders.get(0).getId());
			while (grandparentFolders.isEmpty() == false) {
				if (parentFolders.contains(grandparentFolders.get(0)) == false) {
					parentFolders.add(0,grandparentFolders.get(0));
				}
				grandparentFolders = folderAccess.selectFolderByFolderFileID(grandparentFolders.get(0).getId());
			}
			parent = projectAccess.selectProjectsByFolderID(parentFolders.get(0).getId()).get(0);
		}
		model.addAttribute("parentFolders", parentFolders); // ADDED
		model.addAttribute("parentProject", parent); //ADDED
		
		if (parent.getUser().getId().equals(currentUser.getId()) || currentUser.getRoles().contains("ROLE_ADMIN")) {
			model.addAttribute("hasEditRights", true);
		}
		
		if (currentUser.getRoles().contains("ROLE_ADMIN")) {
			List<Project> projectList = projectAccess.selectProjects();
			model.addAttribute("projectList", projectList);
			if (projectList.size() == 0) {
				model.addAttribute("listIsEmpty", true);
			}
		} else {
			List<Project> projectList = projectAccess.selectProjectsByUserID(currentUser.getId());
			model.addAttribute("projectList", projectList);
			if (projectList.size() == 0) {
				model.addAttribute("listIsEmpty", true);
			}
		}

		return "folder";
	}


	/**
	 * Edits the title and description of the current folder.
	 * 
	 * @param folderId
	 *            The id of the current folder.
	 * @param title
	 *            The new title.
	 * @param description
	 * 
	 *            The new description.
	 * @param httpRequest
	 *            The HttpRequest Object for the View
	 * @return The loaded View
	 */
	@PostMapping("/editfolder")
	public String editFolder(@RequestParam(value = "id", required = true) Integer folderId,
			@RequestParam(value = "title", required = true) String title,
			@RequestParam(value = "description") String description, HttpServletRequest httpRequest) {

		Folder folder = folderAccess.selectFoldersByID(folderId);

		List<Project> parentProjects = projectAccess.selectProjectsByFolderID(folder.getId());
		List<Folder> parentFolders = folderAccess.selectFolderByFolderFileID(folderId);
		
		Project project;
		if (parentProjects.isEmpty() == false) {
			project = parentProjects.get(0);
		} else {
			List<Folder> grandparentFolders = folderAccess.selectFolderByFolderFileID(parentFolders.get(0).getId());
			while (grandparentFolders.isEmpty() == false) {
				parentFolders.add(grandparentFolders.get(0));
				grandparentFolders = folderAccess.selectFolderByFolderFileID(grandparentFolders.get(0).getId());
			}
			project = projectAccess.selectProjectsByFolderID(grandparentFolders.get(0).getId()).get(0);
		}

		for (Folder f : project.getFolderList()) {
			if (f.getTitle().equals(title) && !f.getId().equals(folderId)) {
				return "redirect:folder?id=" + folderId + "&FolderDuplicateTitleError=true&title=" + title;
			}
		}

		folder.setTitle(title);
		folder.setDescription(description);

		// Track alteration of folder information
		folder = trackingService.trackFolderInformation(folder, httpRequest);

		folderAccess.saveFolder(folder);

		return REDIRECT + folderId;
	}

	/**
	 * Resets updatedParent and redirects to the parents folder view
	 * 
	 * @param folderId
	 *            The id of the current folder.
	 * @param parentId
	 *            The id of the current folders parent.
	 * @return A redirect to the folderView of the parent folder.
	 * @author Robert Völkner
	 */

	@RequestMapping("/parentfolder")
	public String parentFolder(@RequestParam(value = "id", required = true) Integer folderId,
			@RequestParam(value = "parentId", required = true) String parentId) {

		Folder folder = folderAccess.selectFoldersByID(folderId);
		folder.setUpdatedparent(false);
		folderAccess.saveFolder(folder);

		return REDIRECT + parentId;
	}
	
	/**
	 * 
	 * @param folderId
	 * @param folderFileId
	 * @return redirect to folder view
	 * @author Hongfei Chen
	 */
	// TODO: add tracking
	@PostMapping("/deletefolderfile")
	public String deleteFetureFile(@RequestParam(value = "folderFileId", required = true) Integer folderFileId) { 
		
		int folderId = folderAccess.selectFolderByFolderFileID(folderFileId).get(0).getId();
		
		folderService.deleteFolder(folderFileId);
		
		//Folder folder = folderService.loadFolder(folderId);
		
		return REDIRECT + folderId;
	}

}
