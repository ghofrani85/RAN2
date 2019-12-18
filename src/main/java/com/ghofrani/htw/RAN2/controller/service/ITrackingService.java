package com.ghofrani.htw.RAN2.controller.service;

import javax.servlet.http.HttpServletRequest;

import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Folder;
import com.ghofrani.htw.RAN2.model.Product;
import com.ghofrani.htw.RAN2.model.Project;

/**
 * Provides an interface for handling interactions.
 * 
 * @author Robert Völkner
 *
 */
public interface ITrackingService {

	/**
	 * Adds tracking information to the folder after adding an file
	 * 
	 * @param file File to be tracked
	 * @param folder Folder that contains file 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated folder
	 */
	Folder trackAddedFile(File file, Folder folder, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the folder and asset after editing an file
	 * 
	 * @param file File to be tracked
	 * @param folder Folder that contains file 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated folder
	 */
	Folder trackFileInformation(File file, Folder folder, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the folder and asset after deleting an file
	 * 
	 * @param file File to be tracked
	 * @param folder Folder that contains file 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated folder
	 */
	Folder trackDeletedFile(File file, Folder folder, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information for the creation to the folder
	 * 
	 * @param folder Folder to be tracked
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated folder
	 */
	Folder trackCreatedFolder(Folder folder, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the new folder after copying it from the parent
	 * folder
	 * 
	 * @param parent ParentFolder of folder
	 * @param newFolder new childFolder
	 * @param httpRequest RequestObject RequestObject
	 * @return return the copied folder
	 */
	Folder trackCopiedFolder(Folder parent, Folder newFolder, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the folder after editing it
	 * 
	 * @param folder Folder to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated folder
	 */
	Folder trackFolderInformation(Folder folder, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the project after adding a folder
	 * 
	 * @param folder Folder to be tracked
	 * @param project Project containing folder
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated project
	 */
	Project trackAddedFolder(Folder folder, Project project, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the project after adding a product
	 * 
	 * @param product Product to be tracked
	 * @param project Project containing product
	 * @param httpRequest RequestObject Request Object
	 * @return return the updated project
	 */
	Project trackAddedProduct(Product product, Project project, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information for the creation to the project
	 * 
	 * @param project Project to be tracked
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated project
	 */
	Project trackCreatedProject(Project project, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the new project after copying it from the parent
	 * project
	 * 
	 * @param parentId Id of parentProject
	 * @param newProject newProject Object
	 * @param httpRequest RequestObject Request Object
	 */
	void trackCopiedProject(Integer parentId, Project newProject, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the project after deleting a folder
	 * 
	 * @param projectId ID of project containing folder
	 * @param folderId ID of folder thats tracking will be deleted
	 * @param httpRequest RequestObject
	 */
	void trackDeletedFolder(Integer projectId, Integer folderId, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the project after deleting a product
	 * 
	 * @param projectId ID of project containg product
	 * @param productId ID of product thats track should be deleted
	 * @param httpRequest RequestObject
	 */
	void trackDeletedProduct(Integer projectId, Integer productId, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the project after editing it
	 * 
	 * @param project project to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated project
	 */
	Project trackProjectInformation(Project project, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information for the creation to the asset
	 * 
	 * @param asset Asset to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated asset
	 */
	Asset trackCreatedAsset(Asset asset, HttpServletRequest httpRequest);

	/**
	 * Adds tracking information to the asset after editing it
	 * 
	 * @param asset Asset to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated asset
	 */
	Asset trackAssetInformation(Asset asset, HttpServletRequest httpRequest);
	
	/**
	 * Adds tracking information for the rating to the project
	 * 
	 * @param project Project to be tracked
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated project
	 */
	Project trackRatedProject(Project project, HttpServletRequest httpRequest);

}
