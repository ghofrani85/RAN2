package com.ghofrani.htw.RAN2.controller.service;

import com.ghofrani.htw.RAN2.model.Folder;

/**
 * Provides an interface for handling interactions with the folder database.
 * 
 * @author Daniel Wahlmann
 *
 */
public interface IFolderService {

	/**
	 * Deletes the folder with the given id.
	 * 
	 * @param id
	 *            the id of the Folder to be deleted
	 */
	void deleteFolder(Integer id);

	/**
	 * Loads the folder with the given id.
	 * 
	 * @param id id of the folder to be loaded
	 * @return The loaded folder
	 */
	Folder loadFolder(Integer id);

	/**
	 * Notifies all children of the folder by setting the updatedParent variable
	 * 
	 * @param folder Folder that has been updated
	 * @author Robert Völkner
	 */
	void notifyChildren(Folder folder);
}
