package com.ghofrani.htw.RAN2.controller.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import com.ghofrani.htw.RAN2.database.AssetAccess;
import com.ghofrani.htw.RAN2.database.FolderAccess;
import com.ghofrani.htw.RAN2.database.ProductAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.database.helper.TrackingType;
import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Folder;
import com.ghofrani.htw.RAN2.model.Product;
import com.ghofrani.htw.RAN2.model.Project;
import com.ghofrani.htw.RAN2.model.Tracking;

/**
 * Implementation of ITrackingService
 * 
 * @author Robert Völkner
 *
 */
@Service
public class TrackingService implements ITrackingService {

	@Autowired
	FolderAccess folderAccess;

	@Autowired
	ProjectAccess projectAccess;

	@Autowired
	AssetAccess assetAccess;

	@Autowired
	ProductAccess productAccess;

	@Autowired
	private MessageSource messages;

	@Autowired
	private LocaleResolver resolver;

	DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	/**
	 * Adds tracking information to the folder after adding an file
	 * 
	 * @param file File to be tracked
	 * @param folder Folder that contains file 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated folder
	 */
	@Override
	public Folder trackAddedFile(File file, Folder folder, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String addArt = messages.getMessage("track.addFile", null, locale);
		String fromAsset = messages.getMessage("track.fromAsset", null, locale);

		Date currentDate = Calendar.getInstance().getTime();
		Asset asset = file.getAsset();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + addArt
				+ file.getTitle() + " " + fromAsset + asset.getTitle() + " - " + dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(folder.getId(), TrackingType.FOLDER, currentDate, modified);
		folder.addTrackingEntry(newTrack);

		// Setup the message for the new tracking entry
		String used = "Used by " + SecurityContextHolder.getContext().getAuthentication().getName()
				+ "; created File: " + file.getTitle();
		newTrack = new Tracking(asset.getId(), TrackingType.ASSET, currentDate, used);
		asset.addTrackingEntry(newTrack);
		assetAccess.saveAsset(asset);

		return folder;
	}

	/**
	 * Adds tracking information to the folder and asset after editing an file
	 * 
	 * @param file File to be tracked
	 * @param folder Folder that contains file 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated folder
	 */
	@Override
	public Folder trackFileInformation(File file, Folder folder, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String altFile = messages.getMessage("track.altFile", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + altFile
				+ file.getTitle() + " - " + dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(folder.getId(), TrackingType.FOLDER, currentDate, modified);
		folder.addTrackingEntry(newTrack);

		Asset asset = file.getAsset();
		newTrack = new Tracking(asset.getId(), TrackingType.ASSET, currentDate, modified);
		asset.addTrackingEntry(newTrack);
		assetAccess.saveAsset(asset);

		return folderAccess.saveFolder(folder);
	}

	/**
	 * Adds tracking information to the folder and asset after deleting an file
	 * 
	 * @param file File to be tracked
	 * @param folder Folder that contains file 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated folder
	 */
	@Override
	public Folder trackDeletedFile(File file, Folder folder, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String delFile = messages.getMessage("track.delFile", null, locale);
		String by = messages.getMessage("track.by", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String deleted = delFile + file.getTitle() + by
				+ SecurityContextHolder.getContext().getAuthentication().getName() + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(folder.getId(), TrackingType.FOLDER, currentDate, deleted);
		folder.addTrackingEntry(newTrack);

		Asset asset = file.getAsset();
		newTrack = new Tracking(asset.getId(), TrackingType.ASSET, currentDate, deleted);
		asset.addTrackingEntry(newTrack);
		assetAccess.saveAsset(asset);

		return folderAccess.saveFolder(folder);
	}

	/**
	 * Adds tracking information for the creation to the folder
	 * 
	 * @param savedFolder Folder to be tracked
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated folder
	 */
	@Override
	public Folder trackCreatedFolder(Folder savedFolder, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String created = messages.getMessage("track.created", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String create = created + SecurityContextHolder.getContext().getAuthentication().getName() + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(savedFolder.getId(), TrackingType.FOLDER, currentDate, create);
		savedFolder.addTrackingEntry(newTrack);
		return folderAccess.saveFolder(savedFolder);

	}

	/**
	 * Adds tracking information to the project after adding a folder
	 * 
	 * @param folder Folder to be tracked
	 * @param project Project containing folder
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated project
	 */
	@Override
	public Project trackAddedFolder(Folder folder, Project project, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String addFolder = messages.getMessage("track.addFolder", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + addFolder
				+ folder.getTitle() + " - " + dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(project.getId(), TrackingType.PROJECT, currentDate, modified);
		project.addTrackingEntry(newTrack);
		return project;

	}

	/**
	 * Adds tracking information to the project after adding a product
	 * 
	 * @param product Product to be tracked
	 * @param project Project containing product
	 * @param httpRequest RequestObject Request Object
	 * @return return the updated project
	 */
	@Override
	public Project trackAddedProduct(Product product, Project project, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String addProduct = messages.getMessage("track.addProduct", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + addProduct
				+ product.getTitle() + " - " + dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(project.getId(), TrackingType.PROJECT, currentDate, modified);
		project.addTrackingEntry(newTrack);
		return project;

	}

	/**
	 * Adds tracking information for the creation to the project
	 * 
	 * @param project Project to be tracked
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated project
	 */
	@Override
	public Project trackCreatedProject(Project project, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String created = messages.getMessage("track.created", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String create = created + SecurityContextHolder.getContext().getAuthentication().getName() + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(project.getId(), TrackingType.PROJECT, currentDate, create);
		project.addTrackingEntry(newTrack);
		project.setLastChange(Calendar.getInstance().getTime());
		return projectAccess.saveProject(project);

	}

	/**
	 * Adds tracking information to the new folder after copying it from the parent
	 * folder
	 * 
	 * @param parent ParentFolder of folder
	 * @param newFolder new childFolder
	 * @param httpRequest RequestObject RequestObject
	 * @return return the copied folder
	 */
	@Override
	public Folder trackCopiedFolder(Folder parent, Folder newFolder, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String copied = messages.getMessage("track.copied", null, locale);
		String fromFolder = messages.getMessage("track.fromFolder", null, locale);

		Date currentDate = Calendar.getInstance().getTime();
		List<Tracking> oldTracks = parent.getTrackingList();
		Tracking tempTrack = null;

		// Set the new Id for the trackings of the copied folder
		for (int i = 0; i < oldTracks.size(); i++) {
			tempTrack = oldTracks.get(i);
			tempTrack.setItemid(newFolder.getId());
			newFolder.addTrackingEntry(tempTrack);
		}

		// Setup the message for the new tracking entry
		String copy = copied + SecurityContextHolder.getContext().getAuthentication().getName() + " " + fromFolder
				+ parent.getTitle() + " - " + dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(newFolder.getId(), TrackingType.FOLDER, currentDate, copy);
		newFolder.addTrackingEntry(newTrack);

		return folderAccess.saveFolder(newFolder);

	}

	/**
	 * Adds tracking information to the new project after copying it from the parent
	 * project
	 * 
	 * @param parentId Id of parentProject
	 * @param newProject newProject Object
	 * @param httpRequest RequestObject Request Object
	 */
	@Override
	public void trackCopiedProject(Integer parentId, Project newProject, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String copied = messages.getMessage("track.copied", null, locale);
		String fromProject = messages.getMessage("track.fromProject", null, locale);

		Date currentDate = Calendar.getInstance().getTime();
		Project parent = projectAccess.selectProjectsByID(parentId);
		List<Tracking> oldTracks = parent.getTrackingList();
		Tracking tempTrack = null;

		// Set the new Id for the trackings of the copied project
		for (int i = 0; i < oldTracks.size(); i++) {
			tempTrack = oldTracks.get(i);
			tempTrack.setItemid(newProject.getId());
			newProject.addTrackingEntry(tempTrack);
		}

		// Setup the message for the new tracking entry
		String copy = copied + SecurityContextHolder.getContext().getAuthentication().getName() + " " + fromProject
				+ parent.getTitle() + " - " + dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(newProject.getId(), TrackingType.PROJECT, currentDate, copy);
		newProject.addTrackingEntry(newTrack);
		newProject.setLastChange(Calendar.getInstance().getTime());
		projectAccess.saveProject(newProject);

	}

	/**
	 * Adds tracking information for the creation to the asset
	 * 
	 * @param asset Asset to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated asset
	 */
	@Override
	public Asset trackCreatedAsset(Asset asset, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String created = messages.getMessage("track.created", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String create = created + SecurityContextHolder.getContext().getAuthentication().getName() + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(asset.getId(), TrackingType.ASSET, currentDate, create);
		asset.addTrackingEntry(newTrack);
		return assetAccess.saveAsset(asset);

	}

	/**
	 * Adds tracking information to the asset after editing it
	 * 
	 * @param asset Asset to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated asset
	 */
	@Override
	public Asset trackAssetInformation(Asset asset, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String altAsset = messages.getMessage("track.altAsset", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + altAsset + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(asset.getId(), TrackingType.ASSET, currentDate, modified);
		asset.addTrackingEntry(newTrack);
		return assetAccess.saveAsset(asset);

	}

	/**
	 * Adds tracking information to the folder after editing it
	 * 
	 * @param folder Folder to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated folder
	 */
	@Override
	public Folder trackFolderInformation(Folder folder, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String altFolder = messages.getMessage("track.altFolder", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + altFolder + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(folder.getId(), TrackingType.FOLDER, currentDate, modified);
		folder.addTrackingEntry(newTrack);
		return folderAccess.saveFolder(folder);

	}

	/**
	 * Adds tracking information to the project after editing it
	 * 
	 * @param project project to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated project
	 */
	@Override
	public Project trackProjectInformation(Project project, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String altProject = messages.getMessage("track.altProject", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + altProject + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(project.getId(), TrackingType.PROJECT, currentDate, modified);
		project.addTrackingEntry(newTrack);
		project.setLastChange(Calendar.getInstance().getTime());
		return projectAccess.saveProject(project);

	}

	/**
	 * Adds tracking information to the project after deleting a folder
	 * 
	 * @param projectId ID of project containing folder
	 * @param folderId ID of folder thats tracking will be deleted
	 * @param httpRequest RequestObject
	 */
	@Override
	public void trackDeletedFolder(Integer projectId, Integer folderId, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String delFolder = messages.getMessage("track.delFolder", null, locale);
		String by = messages.getMessage("track.by", null, locale);

		Date currentDate = Calendar.getInstance().getTime();
		Project project = projectAccess.selectProjectsByID(projectId);
		Folder folder = folderAccess.selectFoldersByID(folderId);

		// Setup the message for the new tracking entry
		String deleted = delFolder + folder.getTitle() + by
				+ SecurityContextHolder.getContext().getAuthentication().getName() + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(project.getId(), TrackingType.PROJECT, currentDate, deleted);
		project.addTrackingEntry(newTrack);
		project.setLastChange(Calendar.getInstance().getTime());
		projectAccess.saveProject(project);
	}

	/**
	 * Adds tracking information to the project after deleting a product
	 * 
	 * @param projectId ID of project containg product
	 * @param productId ID of product thats track should be deleted
	 * @param httpRequest RequestObject
	 */
	@Override
	public void trackDeletedProduct(Integer projectId, Integer productId, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String delProduct = messages.getMessage("track.delProduct", null, locale);
		String by = messages.getMessage("track.by", null, locale);

		Date currentDate = Calendar.getInstance().getTime();
		Project project = projectAccess.selectProjectsByID(projectId);
		Product product = productAccess.selectProductsByID(productId);

		// Setup the message for the new tracking entry
		String deleted = delProduct + product.getTitle() + by
				+ SecurityContextHolder.getContext().getAuthentication().getName() + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(project.getId(), TrackingType.PROJECT, currentDate, deleted);
		project.addTrackingEntry(newTrack);
		project.setLastChange(Calendar.getInstance().getTime());
		projectAccess.saveProject(project);
	}

	/**
	 * Adds tracking information for the rating to the project
	 * 
	 * @param project Project to be tracked
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated project
	 */
	@Override
	public Project trackRatedProject(Project project, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String rated = messages.getMessage("track.reviewed", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String rate = rated + SecurityContextHolder.getContext().getAuthentication().getName() + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(project.getId(), TrackingType.PROJECT, currentDate, rate);
		project.addTrackingEntry(newTrack);
		project.setLastChange(Calendar.getInstance().getTime());
		return projectAccess.saveProject(project);

	}
}
