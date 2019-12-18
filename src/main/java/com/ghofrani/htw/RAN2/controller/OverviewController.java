package com.ghofrani.htw.RAN2.controller;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList; // ArrayList Added CHANGE

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import com.ghofrani.htw.RAN2.controller.service.IUserService;
import com.ghofrani.htw.RAN2.controller.service.TrackingService;
import com.ghofrani.htw.RAN2.database.FolderAccess;
import com.ghofrani.htw.RAN2.database.ProductAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.Folder;
import com.ghofrani.htw.RAN2.model.Product;
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
public class OverviewController {

	/**
	 * Database Interface for Projects.
	 */
	@Autowired
	private ProjectAccess projectAccess;
	
	@Autowired
	private ProductAccess productAccess; // Added CHANGE
	
	@Autowired
	private FolderAccess folderAccess; // Added CHANGE

	@Autowired
	private TrackingService trackingService;

	@Autowired
	private IUserService userService;

	@Autowired
	private UserAccess userAccess;


	/**
	 * Loads overview of projects for User
	 * @param model Model Object for the View
	 * @param userId ID of User of which the overview should be loaded
	 * @param ProjectDuplicateTitleError If not null, there was an ProjectDuplicateTitleError
	 * @param duplicateTitle The title that was duplicate if an ProjectDuplicateTitleError was encountered
	 * @return The generated view
	 */
	@RequestMapping(path = "/overview")
	public String loadOverview(Model model,
			@RequestParam(value = "userId", required = false, defaultValue = "-1") int userId,
			@RequestParam(value = "ProjectDuplicateTitleError", required = false) String ProjectDuplicateTitleError,
			@RequestParam(value = "title", required = false) String duplicateTitle) {

		if (ProjectDuplicateTitleError != null) {
			model.addAttribute("showDuplicateTitleError", true);
			model.addAttribute("duplicateTitle", duplicateTitle);
		}

		// Retrieve currently active User
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		User currentUser = userService.loadUser(user.getUsername());

		User ownerOfProjects;
		if (userId > 0) {
			// Show Projects of other User
			ownerOfProjects = userAccess.selectUsersByID(userId);
		} else {
			// Show Projects of current User
			ownerOfProjects = currentUser;
		}

		// Determine if currentUser has editRights
		if (currentUser.getRoles().contains("ROLE_ADMIN") || ownerOfProjects.getId().equals(currentUser.getId())) {
			model.addAttribute("hasEditRights", true);
		}

		// Show other Title if the library does not belong to current user
		if (!ownerOfProjects.getId().equals(currentUser.getId())) {
			model.addAttribute("projectOwner", ownerOfProjects.getUsername());
		}

		// Retrieve all projects by current User
		List<Project> projectList = projectAccess.selectProjectsByUserID(ownerOfProjects.getId());

		model.addAttribute("projectList", projectList);

		return "overview";
	}

	/**
	 * Creates a new Project
	 * @param title Title of the new Project
	 * @param description Description of the new Project
	 * @param httpRequest Request Object
	 * @return Generated View of the new Project
	 * @throws JSONException When JSON data could not be parsed
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/newProject")
	public String createNewProject(@RequestParam("title") String title, @RequestParam("description") String description,
			HttpServletRequest httpRequest) throws JSONException {

		// Retrieve currently active User
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		User currentUser = userService.loadUser(user.getUsername());

		if (currentUser == null) {
			return "redirect:logout";
		}

		// prevent projects with same title
		List<Project> projectList = projectAccess.selectProjectsByUserID(currentUser.getId());
		for (int i = 0; i < projectList.size(); i++) {
			if (projectList.get(i).getTitle().equals(title)) {
				return "redirect:overview?ProjectDuplicateTitleError=true&title=" + title;
			}
		}

		if (userService.loadUser(user.getUsername()) == null) {
			// System.out.println("LoadUser returns null for User " + user.getUsername());
		}
		
		// fourFeatsList Added CHANGE
		Folder learnFeat = folderAccess.saveFolder(new Folder("Learning Data", "Upload your learning data here"));
		Folder testFeat = folderAccess.saveFolder(new Folder("Testing Data", "Upload your testing data here"));
		Folder codeFeat = folderAccess.saveFolder(new Folder("Code", "Upload your code here"));
		Folder modelFeat = folderAccess.saveFolder(new Folder("Model", "Upload your model here"));
		// nnProduct Added CHANGE
		Product nnProduct = new Product ("Neural Network", "Upload your project here.");
		nnProduct.addFolder(learnFeat);
		nnProduct.addFolder(testFeat);
		nnProduct.addFolder(codeFeat);
		nnProduct.addFolder(modelFeat);
		nnProduct = productAccess.saveProduct(nnProduct);
		
		//CHANGE
		//Project p = projectAccess.saveProject(new Project(null, title, description, Calendar.getInstance().getTime(),
				//LinkedList<Product>(), new LinkedList<Folder>(), currentUser, null, false));
		Project p = projectAccess.saveProject(new Project(null, title, description, Calendar.getInstance().getTime(),
									new LinkedList<Product>(), new LinkedList<Folder>(), currentUser, null, false));
		p.addProduct(nnProduct);
		p.addFolder(learnFeat);
		p.addFolder(testFeat);
		p.addFolder(codeFeat);
		p.addFolder(modelFeat);
		p = projectAccess.saveProject(p);

		// Create Tracking for project
		p = trackingService.trackCreatedProject(p, httpRequest);

		projectAccess.saveProject(p);
		return "redirect:overview";
	}
	
	/**
	 * Deletes a project
	 * @param projectId ID of the project to be deleted
	 * @return redirect to the overview of projects
	 * @throws JSONException When JSON could not be read
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/deleteProject")
	public String deleteProject(@RequestParam("projectId") int projectId) throws JSONException {

		Project p = projectAccess.selectProjectsByID(projectId);

		// Retrieve currently active User
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		User currentUser = userService.loadUser(user.getUsername());

		// Determine if currentUser has editRights
		if (currentUser.getRoles().contains("ROLE_ADMIN") || p.getUser().getId().equals(currentUser.getId())) {
			projectAccess.deleteProject(p);
		}

		return "redirect:overview";
	}
}
