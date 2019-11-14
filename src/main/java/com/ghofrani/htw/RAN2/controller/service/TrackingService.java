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
import com.ghofrani.htw.RAN2.database.FeatureAccess;
import com.ghofrani.htw.RAN2.database.ProductAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.database.helper.TrackingType;
import com.ghofrani.htw.RAN2.model.Artefact;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Feature;
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
	FeatureAccess featureAccess;

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
	 * Adds tracking information to the feature after adding an artefact
	 * 
	 * @param artefact Artefact to be tracked
	 * @param feature Feature that contains artefact 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated feature
	 */
	@Override
	public Feature trackAddedArtefact(Artefact artefact, Feature feature, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String addArt = messages.getMessage("track.addArtefact", null, locale);
		String fromAsset = messages.getMessage("track.fromAsset", null, locale);

		Date currentDate = Calendar.getInstance().getTime();
		Asset asset = artefact.getAsset();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + addArt
				+ artefact.getTitle() + " " + fromAsset + asset.getTitle() + " - " + dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(feature.getId(), TrackingType.FEATURE, currentDate, modified);
		feature.addTrackingEntry(newTrack);

		// Setup the message for the new tracking entry
		String used = "Used by " + SecurityContextHolder.getContext().getAuthentication().getName()
				+ "; created Artefact: " + artefact.getTitle();
		newTrack = new Tracking(asset.getId(), TrackingType.ASSET, currentDate, used);
		asset.addTrackingEntry(newTrack);
		assetAccess.saveAsset(asset);

		return feature;
	}

	/**
	 * Adds tracking information to the feature and asset after editing an artefact
	 * 
	 * @param artefact Artefact to be tracked
	 * @param feature Feature that contains artefact 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated feature
	 */
	@Override
	public Feature trackArtefactInformation(Artefact artefact, Feature feature, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String altArtefact = messages.getMessage("track.altArtefact", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + altArtefact
				+ artefact.getTitle() + " - " + dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(feature.getId(), TrackingType.FEATURE, currentDate, modified);
		feature.addTrackingEntry(newTrack);

		Asset asset = artefact.getAsset();
		newTrack = new Tracking(asset.getId(), TrackingType.ASSET, currentDate, modified);
		asset.addTrackingEntry(newTrack);
		assetAccess.saveAsset(asset);

		return featureAccess.saveFeature(feature);
	}

	/**
	 * Adds tracking information to the feature and asset after deleting an artefact
	 * 
	 * @param artefact Artefact to be tracked
	 * @param feature Feature that contains artefact 
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated feature
	 */
	@Override
	public Feature trackDeletedArtefact(Artefact artefact, Feature feature, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String delArtefact = messages.getMessage("track.delArtefact", null, locale);
		String by = messages.getMessage("track.by", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String deleted = delArtefact + artefact.getTitle() + by
				+ SecurityContextHolder.getContext().getAuthentication().getName() + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(feature.getId(), TrackingType.FEATURE, currentDate, deleted);
		feature.addTrackingEntry(newTrack);

		Asset asset = artefact.getAsset();
		newTrack = new Tracking(asset.getId(), TrackingType.ASSET, currentDate, deleted);
		asset.addTrackingEntry(newTrack);
		assetAccess.saveAsset(asset);

		return featureAccess.saveFeature(feature);
	}

	/**
	 * Adds tracking information for the creation to the feature
	 * 
	 * @param savedFeature Feature to be tracked
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated feature
	 */
	@Override
	public Feature trackCreatedFeature(Feature savedFeature, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String created = messages.getMessage("track.created", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String create = created + SecurityContextHolder.getContext().getAuthentication().getName() + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(savedFeature.getId(), TrackingType.FEATURE, currentDate, create);
		savedFeature.addTrackingEntry(newTrack);
		return featureAccess.saveFeature(savedFeature);

	}

	/**
	 * Adds tracking information to the project after adding a feature
	 * 
	 * @param feature Feature to be tracked
	 * @param project Project containing feature
	 * @param httpRequest RequestObject RequestObject
	 * @return return the updated project
	 */
	@Override
	public Project trackAddedFeature(Feature feature, Project project, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String addFeature = messages.getMessage("track.addFeature", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + addFeature
				+ feature.getTitle() + " - " + dateFormat.format(currentDate);
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
	 * Adds tracking information to the new feature after copying it from the parent
	 * feature
	 * 
	 * @param parent ParentFeature of feature
	 * @param newFeature new childFeature
	 * @param httpRequest RequestObject RequestObject
	 * @return return the copied feature
	 */
	@Override
	public Feature trackCopiedFeature(Feature parent, Feature newFeature, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String copied = messages.getMessage("track.copied", null, locale);
		String fromFeature = messages.getMessage("track.fromFeature", null, locale);

		Date currentDate = Calendar.getInstance().getTime();
		List<Tracking> oldTracks = parent.getTrackingList();
		Tracking tempTrack = null;

		// Set the new Id for the trackings of the copied feature
		for (int i = 0; i < oldTracks.size(); i++) {
			tempTrack = oldTracks.get(i);
			tempTrack.setItemid(newFeature.getId());
			newFeature.addTrackingEntry(tempTrack);
		}

		// Setup the message for the new tracking entry
		String copy = copied + SecurityContextHolder.getContext().getAuthentication().getName() + " " + fromFeature
				+ parent.getTitle() + " - " + dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(newFeature.getId(), TrackingType.FEATURE, currentDate, copy);
		newFeature.addTrackingEntry(newTrack);

		return featureAccess.saveFeature(newFeature);

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
	 * Adds tracking information to the feature after editing it
	 * 
	 * @param feature Feature to be tracked
	 * @param httpRequest RequestObject
	 * @return return the updated feature
	 */
	@Override
	public Feature trackFeatureInformation(Feature feature, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String mod = messages.getMessage("track.modified", null, locale);
		String altFeature = messages.getMessage("track.altFeature", null, locale);

		Date currentDate = Calendar.getInstance().getTime();

		// Setup the message for the new tracking entry
		String modified = mod + SecurityContextHolder.getContext().getAuthentication().getName() + altFeature + " - "
				+ dateFormat.format(currentDate);
		Tracking newTrack = new Tracking(feature.getId(), TrackingType.FEATURE, currentDate, modified);
		feature.addTrackingEntry(newTrack);
		return featureAccess.saveFeature(feature);

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
	 * Adds tracking information to the project after deleting a feature
	 * 
	 * @param projectId ID of project containing feature
	 * @param featureId ID of feature thats tracking will be deleted
	 * @param httpRequest RequestObject
	 */
	@Override
	public void trackDeletedFeature(Integer projectId, Integer featureId, HttpServletRequest httpRequest) {

		Locale locale = resolver.resolveLocale(httpRequest);
		String delFeature = messages.getMessage("track.delFeature", null, locale);
		String by = messages.getMessage("track.by", null, locale);

		Date currentDate = Calendar.getInstance().getTime();
		Project project = projectAccess.selectProjectsByID(projectId);
		Feature feature = featureAccess.selectFeaturesByID(featureId);

		// Setup the message for the new tracking entry
		String deleted = delFeature + feature.getTitle() + by
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
