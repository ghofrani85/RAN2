package com.ghofrani.htw.RAN2.controller.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.database.FolderAccess;
import com.ghofrani.htw.RAN2.model.Folder;

/**
 * Implementation of IFolderService for use with the FoldertAccess database
 * interface.
 * 
 * @author Daniel Wahlmann
 *
 */
@Service
public class FolderService implements IFolderService {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private FolderAccess folderAccess;

	/**
	 * Deletes the folder with the given id.
	 * 
	 * @param id
	 *            the id of the Folder to be deleted
	 */
	@Override
	public void deleteFolder(Integer id) {

		Folder folder = loadFolder(id);
		folderAccess.deleteFolder(folder);
	}

	/**
	 * Loads the folder with the given id.
	 * 
	 * @param id id of the folder to be loaded
	 * @return The loaded folder
	 */
	@Override
	public Folder loadFolder(Integer id) {
		return folderAccess.selectFoldersByID(id);
	}

	/**
	 * Notifies all children of the folder by setting the updatedParent variable
	 * 
	 * @param folder Folder that has been updated
	 * @author Robert Völkner
	 */
	@Override
	public void notifyChildren(Folder folder) {

		List<Folder> folderList = folderAccess.selectFolders();
		Folder tempFolder;
		log.info("Change to folder {} with title {}, notifying children", folder.getId(), folder.getTitle());

		for (int i = 0; i < folderList.size(); i++) {

			tempFolder = folderList.get(i);
			// check for correct parent
			if (tempFolder.getParent() != null && tempFolder.getParent().getId().equals(folder.getId())) {
				tempFolder.setUpdatedparent(true);
				log.info("Set updatedparent of folder {} with title {}.", tempFolder.getId(), tempFolder.getTitle());
				folderAccess.saveFolder(tempFolder);
			}
		}

	}

}
