package com.ghofrani.htw.RAN2.controller.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.error.DatabaseException;
import com.ghofrani.htw.RAN2.controller.error.SameUserException;
import com.ghofrani.htw.RAN2.database.FileAccess;
import com.ghofrani.htw.RAN2.database.FolderAccess;
import com.ghofrani.htw.RAN2.database.ProductAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.Folder;
import com.ghofrani.htw.RAN2.model.Product;
import com.ghofrani.htw.RAN2.model.Project;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Implementation of IProjectService for use with the ProjectAccess database
 * interface.
 * 
 * @author Daniel Wahlmann
 *
 */
@Service
public class ProjectService implements IProjectService {
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ProjectAccess projectAccess;

	@Autowired
	private ProductAccess productAccess;

	@Autowired
	private FolderAccess folderAccess;

	@Autowired
	private FileAccess fileAccess;

	/**
	 * Loads the Project with the given id.
	 * 
	 * @param id the id of the project to load
	 * @return The Project loaded
	 */
	@Override
	public Project loadProject(Integer id) {
		return projectAccess.selectProjectsByID(id);
	}

	/**
	 * Saves the Project to the database.
	 * 
	 * @param project The project to save
	 * @throws DatabaseException When the database encountered an error
	 * @return saved Project Object
	 */
	@Override
	public Project saveProject(Project project) throws DatabaseException {
		project.setLastChange(Calendar.getInstance().getTime());
		Project saved = projectAccess.saveProject(project);

		if (saved == null) {
			throw new DatabaseException();
		}

		return saved;
	}

	/**
	 * Copies the given Project to a different user.
	 * 
	 * @param oldId ID of parentProject
	 * @param newTitle Title of new Project
	 * @param newDescription New description of copy
	 * @param newUser User that copied
	 * @return the new project
	 * @throws SameUserException When user tried to copy from himself
	 */
	@Override
	public Project copyProject(Integer oldId, String newTitle, String newDescription, User newUser)
			throws SameUserException {

		Project oldProject = projectAccess.selectProjectsByID(oldId);
		if (newUser.getId().equals(oldProject.getUser().getId())) {
			throw new SameUserException();
		}

		Project newProject = projectAccess.saveProject(new Project(null, newTitle, newDescription,
				Calendar.getInstance().getTime(), new ArrayList<>(), new ArrayList<>(), newUser, oldProject, false));

		// Set ids of Folders, Products and Files to null so that new database
		// entries are created.
		for (Folder folder : oldProject.getFolderList()) {

			Folder tmpFolder = folderAccess.selectFoldersByID(folder.getId());

			List<File> tempList = new ArrayList<>();
			for (File file : tmpFolder.getFileList()) {
				File tmpFile = fileAccess.selectFilesByID(file.getId());
				tmpFile.setId(null);
				File newFile = fileAccess.saveFile(tmpFile);
				tempList.add(newFile);
			}
			tmpFolder.setFileList(tempList);
			
			List<Folder> tempFeatArtList = copyFolderFilesHelper(tmpFolder); // Added
			tmpFolder.setFolderFileList(tempFeatArtList);
			
			tmpFolder.setId(null);
			Folder newFolder = folderAccess.saveFolder(tmpFolder);
			newProject.addFolder(newFolder);
		}
		newProject = projectAccess.saveProject(newProject);

		// Saving the associations of Folders with Products
		List<Product> products = oldProject.getProductList();
		Map<String, List<Folder>> productFolders = new HashMap<>();
		for (Product product : products) {
			product.setId(null);
			productFolders.put(product.getTitle(), product.getFolderList());
			product.setFolderList(new ArrayList<>());
		}
		newProject.setProductList(products);

		newProject = projectAccess.saveProject(newProject);

		// Add the new Folders to the Products
		List<Product> newProducts = copyFolderAssociations(newProject, productFolders);

		newProject.setProductList(newProducts);
		newProject.setLastChange(Calendar.getInstance().getTime());
		newProject = projectAccess.saveProject(newProject);

		return newProject;
	}
	
//	private Map<Integer, List<Folder>> createFeatIdFeatArtsMap(Map<Integer, List<Folder>> tmpFeatArtList) {
//		Map<Integer, List<Folder>> featIdFeatArtsMap = new HashMap<>();
//		if (tmpFeatArtList.isEmpty() == true) {
//			return featIdFeatArtsMap;
//		} else {
//			for (Folder featArt: tmpFeatArtList) {
//				featIdFeatArtsMap.put(featArt.getId(), );
//			}
//		}
//	}
	
	/**
	 * 
	 * @param tmpFolder
	 * @return
	 * @author Hongfei Chen
	 */
	private List<Folder> copyFolderFilesHelper(Folder tmpFolder) {
		log.info("Called copyHelper with feat art id={}", tmpFolder.getId());
		List<Folder> tempFeatArtList = new ArrayList<>();
		List<Folder> currentFeatArtList = tmpFolder.getFolderFileList(); //folderAccess.selectFolderFilesByFolderID(tmpFolder.getId());
		if (currentFeatArtList.isEmpty() == true) {
			return tempFeatArtList;
		} else {
			for (Folder featArt : currentFeatArtList) {
				Folder tmpFeatArt = folderAccess.selectFoldersByID(featArt.getId());
				Folder newFeatArt = tmpFeatArt;
				newFeatArt.setId(null);
				newFeatArt = folderAccess.saveFolder(newFeatArt);
				tempFeatArtList.add(newFeatArt);	
				newFeatArt.setFolderFileList(copyFolderFilesHelper(tmpFeatArt));
			}
			return tempFeatArtList;
		}
	}

	/**
	 * Copies the associations of Folders with Products.
	 * 
	 * @param newProject The new copy of Project
	 * @param productFolders The matrix of product and folders of original project
	 * @return List of new products
	 */
	private List<Product> copyFolderAssociations(Project newProject, Map<String, List<Folder>> productFolders) {
		List<Product> newProducts = newProject.getProductList();
		List<Folder> newFolders = newProject.getFolderList();
		for (Product product : newProducts) {
			List<Folder> folders = productFolders.get(product.getTitle());
			for (Folder folder : newFolders) {
				for (Folder f : folders) {
					if (folder.getTitle().equals(f.getTitle())) {
						product.addFolder(folder);
					}
				}
			}
			productAccess.saveProduct(product);
		}
		return newProducts;
	}

	/**
	 * Wrapper for selectProjectsByUserID, returns a List with all Projects by a
	 * given User
	 * 
	 * @author Jannik Gröger
	 * @param user
	 *            The user of which the projects are returned
	 * @return List of projects of User user
	 */
	@Override
	public List<Project> loadAllProjectsOfUser(User user) {

		return projectAccess.selectProjectsByUserID(user.getId());
	}

	public List<Project> loadAllRatedProjectsOfUser(User user) {
		return projectAccess.selectProjectsByRatedUserID(user.getId());
	}
	
	/**
	 * Notifies all children of the project by setting the updatedParent variable
	 * 
	 * @param project The project which was updated
	 * @author Robert Völkner
	 */
	@Override
	public void notifyChildren(Project project) {
		List<Project> projectList = projectAccess.selectProjects();
		Project tempProject;

		for (int i = 0; i < projectList.size(); i++) {

			tempProject = projectList.get(i);
			// check for correct parent
			if (tempProject.getParent() != null && tempProject.getParent().getId().equals(project.getId())) {
				tempProject.setUpdatedparent(true);
				tempProject.setLastChange(Calendar.getInstance().getTime());
				projectAccess.saveProject(tempProject);
			}
		}

	}

}
