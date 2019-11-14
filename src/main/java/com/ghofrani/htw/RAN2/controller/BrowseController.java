package com.ghofrani.htw.RAN2.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ghofrani.htw.RAN2.controller.service.IUserService;
import com.ghofrani.htw.RAN2.database.AssetAccess;
import com.ghofrani.htw.RAN2.database.FeatureAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Feature;
import com.ghofrani.htw.RAN2.model.Project;
import com.ghofrani.htw.RAN2.model.User;

/**
 * 
 * 
 * 
 * @author Jannik Gröger
 *
 */
@Controller
public class BrowseController {

	/**
	 * Database Interface for Projects.
	 */
	@Autowired
	private ProjectAccess projectAccess;

	@Autowired
	private FeatureAccess featureAccess;

	@Autowired
	private AssetAccess assetAccess;

	@Autowired
	private UserAccess userAccess;

	@Autowired
	IUserService userService;

	@RequestMapping(path = "/browse")
	public String loadBrowseView(Model model, @RequestParam("type") String type,
			@RequestParam(value = "search", required = false) String searchWord) {

		if (searchWord != null) {
			model.addAttribute("searchWord", searchWord);
		}

		if (type.equals("project")) {
			// Retrieve all projects
			List<Project> projectList = projectAccess.selectProjects();

			// Filter for searchWord
			if (searchWord != null) {
				Iterator<Project> iter = projectList.iterator();

				while (iter.hasNext()) {
					Project p = iter.next();

					if (!p.getTitle().toLowerCase().contains(searchWord.toLowerCase())) {
						iter.remove();
					}
				}
			}

			model.addAttribute("projectList", projectList);

		} else if (type.equals("feature")) {
			// Retrieve all features
			List<Feature> featureList = featureAccess.selectFeatures();

			Iterator<Feature> iter = featureList.iterator();

			List<FeatureEntry> entries = new LinkedList<FeatureEntry>();
			
			User u;
			
			while (iter.hasNext()) {
				Feature f = iter.next();
				List<Project> projectList = projectAccess.selectProjectsByFeatureID(f.getId());
				if(projectList.size() == 0) {
					u = new User();
				}
				else {
					u = projectList.get(0).getUser();
				}

				// Filter for searchWord
				if (searchWord == null || f.getTitle().toLowerCase().contains(searchWord.toLowerCase())) {
					entries.add(new FeatureEntry(f, u));
				}

			}

			model.addAttribute("featureEntries", entries);
		} else if (type.equals("asset")) {
			// Retrieve all assets
			List<Asset> assetList = assetAccess.selectAssets();

			Iterator<Asset> iter = assetList.iterator();

			List<AssetEntry> entries = new LinkedList<AssetEntry>();

			while (iter.hasNext()) {
				Asset a = iter.next();
				User u = userAccess.selectUsersByID(Integer.parseInt(a.getMetadata().get("Uploaded by")));

				// Filter for searchWord
				if (searchWord == null || a.getTitle().toLowerCase().contains(searchWord.toLowerCase())) {
					entries.add(new AssetEntry(a, u));
				}

			}

			model.addAttribute("assetEntries", entries);
		} else {
			// Retrieve all projects
			List<Project> projectList = projectAccess.selectProjects();

			model.addAttribute("projectList", projectList);
		}

		return "browse";
	}

	private class FeatureEntry {

		Feature feature;
		User owner;

		public FeatureEntry(Feature feature, User owner) {
			super();
			this.feature = feature;
			this.owner = owner;
		}

		/**
		 * @return the feature
		 */
		public Feature getFeature() {
			return feature;
		}

		/**
		 * @param feature
		 *            the feature to set
		 */
		public void setFeature(Feature feature) {
			this.feature = feature;
		}

		/**
		 * @return the owner
		 */
		public User getOwner() {
			return owner;
		}

		/**
		 * @param owner
		 *            the owner to set
		 */
		public void setOwner(User owner) {
			this.owner = owner;
		}

	}

	private class AssetEntry {

		Asset asset;
		User owner;

		public AssetEntry(Asset asset, User owner) {
			super();
			this.asset = asset;
			this.owner = owner;
		}

		/**
		 * @return the asset
		 */
		public Asset getAsset() {
			return asset;
		}

		/**
		 * @param asset
		 *            the asset to set
		 */
		public void setAsset(Asset asset) {
			this.asset = asset;
		}

		/**
		 * @return the owner
		 */
		public User getOwner() {
			return owner;
		}

		/**
		 * @param owner
		 *            the owner to set
		 */
		public void setOwner(User owner) {
			this.owner = owner;
		}

	}

}
