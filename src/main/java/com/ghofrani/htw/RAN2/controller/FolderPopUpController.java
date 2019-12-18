package com.ghofrani.htw.RAN2.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ghofrani.htw.RAN2.controller.service.ProjectService;
import com.ghofrani.htw.RAN2.controller.service.TrackingService;
import com.ghofrani.htw.RAN2.controller.service.UserService;
import com.ghofrani.htw.RAN2.database.FolderAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.database.TrackingAccess;
import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.Folder;
import com.ghofrani.htw.RAN2.model.Project;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Handle the HTTP Requests from Copy-Folder, Edit-Folder and New-Folder
 *
 * @author Tim Gugel
 * @author Robert Völkner
 *
 */
@Controller
public class FolderPopUpController {

	/**
	 * Redirectory to special Folder-Page
	 */
	private static final String REDIRECT_F = "redirect:folder?id=";

	/**
	 * Redirectory to special Project-Page
	 */
	private static final String REDIRECT_P = "redirect:project?id=";

	/**
	 * Database Interface for Projects.
	 */
	@Autowired
	ProjectAccess projectAccess;

	/**
	 * Database Interface for Folders.
	 */
	@Autowired
	FolderAccess folderAccess;

	/**
	 * Database Interface for Tracking.
	 */
	@Autowired
	TrackingAccess trackingAccess;

	@Autowired
	TrackingService trackingService;

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	/**
	 * Rename name, if allready exists
	 * 
	 * @param title
	 *            The new title of the Folder
	 * @param names
	 *            List of the used titles in the folder
	 * @return the renamed title
	 */
	private String renameIfExisting(String title, List<String> names) {
		String titleSuffix = "";

		int oldSuffixFirst = title.lastIndexOf('[');
		int oldSuffixLast = title.lastIndexOf(']');

		// check if the title is already a renamed version and remove the version tag
		if (oldSuffixFirst > -1 && oldSuffixLast > -1 && oldSuffixFirst < oldSuffixLast
				&& oldSuffixLast == title.length() - 1) {
			title = title.substring(0, oldSuffixFirst);
		}

		// add the correct version tag
		for (int i = 0; i <= names.size(); i++) {
			if (i != 0) {
				titleSuffix = "[" + i + "]";
			}
			if (!names.contains(title + titleSuffix)) {
				title = title + "" + titleSuffix;
				break;
			}
		}
		return title;
	}

	/**
	 * Creates a new folder and add it to a project
	 * 
	 * @param title
	 *            The new title of the Folder
	 * @param description
	 *            The new description of the Folder
	 * @param projectId
	 *            The Project for the new Folder
	 * @param httpRequest
	 *            The Http Request
	 * @return A redirect to the Project View of the current Project
	 */
	@PostMapping("/createFolder")
	public String createFolder(@RequestParam(value = "titleTextbar_create", required = true) String title,
			@RequestParam(value = "descriptionTextarea_create", required = true) String description,
			@RequestParam(value = "projId_create", required = true) Integer projectId, HttpServletRequest httpRequest) {

		// Load informations
		Project project = projectAccess.selectProjectsByID(projectId);
		List<Folder> projectFolders = project.getFolderList();

		// Build list with all foldernames of the project
		List<String> folderTitles = new ArrayList<>();
		for (int i = 0; i < projectFolders.size(); i++) {
			folderTitles.add(projectFolders.get(i).getTitle());

			// Check for DuplicateTitle
			if (projectFolders.get(i).getTitle().equals(title)) {
				return REDIRECT_P + projectId + "&FolderDuplicateTitleError=true&title=" + title;
			}
		}

		// Rename folder, if name allready exists
		// title = renameIfExisting(title, folderTitles); Not needed, after
		// duplicateTitleWarnings

		// Create new Folder and save
		Folder newFolder = new Folder(title, description);
		Folder savedFolder = folderAccess.saveFolder(newFolder);

		// Create tracking for that folder
		savedFolder = trackingService.trackCreatedFolder(savedFolder, httpRequest);

		// Update tracking for the project
		project = trackingService.trackAddedFolder(savedFolder, project, httpRequest);

		// Add to project and save
		project.addFolder(savedFolder);
		project.setLastChange(Calendar.getInstance().getTime());
		projectAccess.saveProject(project);

		projectService.notifyChildren(project);

		return REDIRECT_P + projectId;
	}
	
	/**
	 * 
	 * @param title
	 * @param description
	 * @param folderId
	 * @return
	 * @author Hongfei Chen
	 */
	// TODO: Add tracking
	@PostMapping("/createFolderFile")
	public String createFolderFile(@RequestParam(value = "titleTextbar_create", required = true) String title,
			@RequestParam(value = "descriptionTextarea_create", required = true) String description,
			@RequestParam(value = "featId_create", required = true) Integer folderId) {

		// Load informations
		Folder folder = folderAccess.selectFoldersByID(folderId);
		List<Folder> folderFolderFiles = folder.getFolderFileList();

		// Build list with all folderFile names of the project
		List<String> folderFileTitles = new ArrayList<>();
		for (int i = 0; i < folderFolderFiles.size(); i++) {
			folderFileTitles.add(folderFolderFiles.get(i).getTitle());

			// Check for DuplicateTitle
			if (folderFolderFiles.get(i).getTitle().equals(title)) {
				return REDIRECT_F + folderId + "&FolderDuplicateTitleError=true&title=" + title; //CHECK this, could be wrong
			}
		}


		// Create new Folder and save
		Folder newFolderFile = new Folder(title, description);
		Folder savedFolderFile = folderAccess.saveFolder(newFolderFile);

		// Create tracking for that folderFile
		//savedFolderFile = trackingService.trackCreatedFolder(savedFolderFile, httpRequest);

		// Update tracking for the folder
		//project = trackingService.trackAddedFolder(savedFolder, project, httpRequest);

		// Add to folder and save
		folder.addFolderFile(savedFolderFile);
		folderAccess.saveFolder(folder);

		return REDIRECT_F + folderId;
	}
	

	/**
	 * Copy a folder from an existing project to another project with optional
	 * editing of title and description.
	 * 
	 * @param title
	 *            The new title of the Folder
	 * @param description
	 *            The new description of the Folder
	 * @param folderId
	 *            The id of the Folder that gets copied
	 * @param projectId
	 *            The id of the Project where to copy to
	 * @param httpRequest
	 *            The Http Request
	 * @return A redirect to the Folder View of the new Folder
	 */
	@PostMapping("/copyFolder")
	public String copyFolder(@RequestParam(value = "titleTextbar_copy", required = true) String title,
			@RequestParam(value = "descriptionTextarea_copy") String description,
			@RequestParam(value = "featId_copy") Integer folderId,
			@RequestParam(value = "projId_copy", required = false) Integer projectId, HttpServletRequest httpRequest) {

		if (projectId == null) {
			return REDIRECT_F + folderId;
		}

		// Load information
		Project project = projectAccess.selectProjectsByID(projectId);
		List<Folder> projectFolders = project.getFolderList();
		Folder folder = folderAccess.selectFoldersByID(folderId);

		// Retrieve currently active User
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		User currentUser = userService.loadUser(user.getUsername());

		// Check if User is authorized
		if (!project.getUser().getId().equals(currentUser.getId()) && !currentUser.getRoles().contains("ROLE_ADMIN")) {
			return "redirect:overview";
		}

		// Build list with all foldernames of the project
		List<String> folderTitles = new ArrayList<>();
		for (int i = 0; i < projectFolders.size(); i++) {
			folderTitles.add(projectFolders.get(i).getTitle());
		}

		// Rename folder, if name already exists
		title = renameIfExisting(title, folderTitles);

		List<File> fileList = folder.getFileList();
		fileList.forEach(art -> art.setId(null));
		
		// ADDED FolderFileList
		List<Folder> folderFileList = folder.getFolderFileList();
		fileList.forEach(featart -> featart.setId(null));

		// Create new folder with (maybe modified) param of the copied folder
		Folder newFolder = new Folder(null, title, description, fileList, folder, folderFileList);
		Folder savedFolder = folderAccess.saveFolder(newFolder);

		fileList = savedFolder.getFileList();
		List<File> updatedFileList = new ArrayList<>();
		for (int i = 0; i < fileList.size(); i++) {
			File tempFile = fileList.get(i);
			tempFile.setFolder(savedFolder);
			updatedFileList.add(tempFile);
		}

		savedFolder.setFileList(updatedFileList);
		savedFolder = folderAccess.saveFolder(newFolder);

		// Create tracking for copied folder from parent folder
		savedFolder = trackingService.trackCopiedFolder(folder, savedFolder, httpRequest);

		// Update tracking for the project
		project = trackingService.trackAddedFolder(savedFolder, project, httpRequest);

		// Save
		project.addFolder(savedFolder);
		project.setLastChange(Calendar.getInstance().getTime());
		projectAccess.saveProject(project);

		projectService.notifyChildren(project);

		return REDIRECT_F + savedFolder.getId();
	}

}
