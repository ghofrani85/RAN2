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
import com.ghofrani.htw.RAN2.database.FolderAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Folder;
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
	private FolderAccess folderAccess;

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

		} else if (type.equals("folder")) {
			// Retrieve all folders
			List<Folder> folderList = folderAccess.selectFolders();

			Iterator<Folder> iter = folderList.iterator();

			List<FolderEntry> entries = new LinkedList<FolderEntry>();
			
			User u;
			
			while (iter.hasNext()) {
				Folder f = iter.next();
				List<Project> projectList = projectAccess.selectProjectsByFolderID(f.getId());
				if(projectList.size() == 0) {
					u = new User();
				}
				else {
					u = projectList.get(0).getUser();
				}

				// Filter for searchWord
				if (searchWord == null || f.getTitle().toLowerCase().contains(searchWord.toLowerCase())) {
					entries.add(new FolderEntry(f, u));
				}

			}

			model.addAttribute("folderEntries", entries);
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

	private class FolderEntry {

		Folder folder;
		User owner;

		public FolderEntry(Folder folder, User owner) {
			super();
			this.folder = folder;
			this.owner = owner;
		}

		/**
		 * @return the folder
		 */
		public Folder getFolder() {
			return folder;
		}

		/**
		 * @param folder
		 *            the folder to set
		 */
		public void setFolder(Folder folder) {
			this.folder = folder;
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
