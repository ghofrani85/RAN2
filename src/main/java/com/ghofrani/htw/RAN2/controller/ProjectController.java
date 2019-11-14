package com.ghofrani.htw.RAN2.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.error.DatabaseException;
import com.ghofrani.htw.RAN2.controller.error.SameUserException;
import com.ghofrani.htw.RAN2.controller.service.IFeatureService;
import com.ghofrani.htw.RAN2.controller.service.IProductService;
import com.ghofrani.htw.RAN2.controller.service.IProjectService;
import com.ghofrani.htw.RAN2.controller.service.IUserService;
import com.ghofrani.htw.RAN2.controller.service.TrackingService;
import com.ghofrani.htw.RAN2.database.ArtefactAccess;
import com.ghofrani.htw.RAN2.database.FeatureAccess;
import com.ghofrani.htw.RAN2.database.ProjectAccess;
import com.ghofrani.htw.RAN2.model.Feature;
import com.ghofrani.htw.RAN2.model.Product;
import com.ghofrani.htw.RAN2.model.Project;
import com.ghofrani.htw.RAN2.model.User;

/**
 * View Controller for the Project Page.
 * 
 * Handles requests for the Project View and creating, editing and deleting
 * Products.
 * 
 * @author Daniel Wahlmann
 *
 */
@Controller
public class ProjectController {

	private static final String DUPLICATE_TITLE = "duplicateTitle";

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private static final String REDIRECT = "redirect:project?id=";

	@Autowired
	private IProjectService projectService;

	@Autowired
	private TrackingService trackingService;

	@Autowired
	private IFeatureService featureService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IUserService userService;
	
	@Autowired
	private ArtefactAccess artefactAccess;
	
	@Autowired
	private ProjectAccess projectAccess;
	
	@Autowired
	private FeatureAccess featureAccess;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private LocaleResolver resolver;

	/**
	 * 
	 * @param id  The Project id.
	 * @param featureDuplicateTitleError if not null there was a duplicate FeatureTitle
	 * @param productDuplicateTitleError if not null there was a duplicate ProductTitle
	 * @param projectDuplicateTitleError if not null there was a duplicate ProjectTitle
	 * @param duplicateTitle Title that was duplicate
	 * @param model The data model.
	 * @return The view for the Project associated with the id.
	 */
	@RequestMapping(path = "/project", params = "id")
	@Secured("ROLE_USER")
	public String loadProject(@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "FeatureDuplicateTitleError", required = false) String featureDuplicateTitleError,
			@RequestParam(value = "ProductDuplicateTitleError", required = false) String productDuplicateTitleError,
			@RequestParam(value = "ProjectDuplicateTitleError", required = false) String projectDuplicateTitleError,
			@RequestParam(value = "title", required = false) String duplicateTitle, Model model, HttpServletRequest request) {

		log.info("Called loadProject with id={}", id);
		
		// Moved CHANGE
		// Retrieve currently active User
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
																	.getContext().getAuthentication().getPrincipal();
		User currentUser = userService.loadUser(user.getUsername());
		
		Project project = projectService.loadProject(id);
		List<Project> projectList = projectService.loadAllProjectsOfUser(currentUser); //Added
		
		//List<Project> ratedProjectList = projectService.loadAllRatedProjectsOfUser(currentUser);
		
		// Allow rating if the user has copied the project
		if (!project.getRatedUserList().contains(currentUser)) {
			for (Project p : projectList) {
				if (p.getParent() != null) {
					if (p.getParent().getId() == id) {
						if (!model.containsAttribute("hasRateRights")) {
							model.addAttribute("hasRateRights", true);
						}
					}
				}
			}
		}

		if (featureDuplicateTitleError != null) {
			model.addAttribute("showFeatureDuplicateTitleError", true);
			model.addAttribute(DUPLICATE_TITLE, duplicateTitle);
		}

		if (productDuplicateTitleError != null) {
			model.addAttribute("showProductDuplicateTitleError", true);
			model.addAttribute(DUPLICATE_TITLE, duplicateTitle);
		}

		if (projectDuplicateTitleError != null) {
			model.addAttribute("showProjectDuplicateTitleError", true);
			model.addAttribute(DUPLICATE_TITLE, duplicateTitle);
		}

		// Check if link to updated parent feature is needed
		if (project.isUpdatedparent()) {
			model.addAttribute("parentFeatureId", project.getParent().getId());
		}
		
		String message = messageSource.getMessage("project.nrArtefacts", null, resolver.resolveLocale(request));
		for (Feature f :project.getFeatureList()) {
			int artefactCount = artefactAccess.selectArtefactsByFeatureID(f.getId()).size();
			f.setDescription(message + " " + artefactCount +"\n" + f.getDescription());
		}

		model.addAttribute("project", project);
		model.addAttribute("products", project.getProductList());
		model.addAttribute("features", project.getFeatureList());
		model.addAttribute("user", project.getUser());

		// Retrieve currently active User Moved
		// currentUser moved up CHANGE

		// Determine if currentUser has editRights
		if (project.getUser().getId().equals(currentUser.getId()) || currentUser.getRoles().contains("ROLE_ADMIN")) {
			model.addAttribute("hasEditRights", true);
		}

		// Determine if currentUser is owner
		if (project.getUser().getId().equals(currentUser.getId())) {
			model.addAttribute("isOwner", true);
		}

		return "project";
	}

	/**
	 * Creates a new Product and adds it to the current Project.
	 * 
	 * @param title
	 *            The title of the Product.
	 * @param description
	 *            The description of the Product.
	 * @param projectId
	 *            The id of the current Project.
	 * @param httpRequest
	 *            httpRequestObject
	 * @return A redirect to the Project View of the current Project.
	 */
	@PostMapping("/createproduct")
	@Secured("ROLE_USER")
	public String createProduct(@RequestParam(value = "apptitle", required = true) String title,
			@RequestParam(value = "appdesc") String description,
			@RequestParam(value = "id", required = true) Integer projectId, HttpServletRequest httpRequest) {

		log.debug("Called createProduct with projectId={} title={} description={}", projectId, title, description);

		Project project = projectService.loadProject(projectId);

		// Check for duplicate title
		for (Product p : project.getProductList()) {
			if (p.getTitle().equals(title)) {
				return REDIRECT + projectId + "&ProductDuplicateTitleError=true&title=" + title;
			}
		}

		Product product = new Product(title, description);

		// Update tracking for product
		project = trackingService.trackAddedProduct(product, project, httpRequest);

		project.addProduct(product);
		try {
			projectService.saveProject(project);
		} catch (DatabaseException e) {
			log.error("Error while saving project.");
			return REDIRECT + projectId + "&dberror";
		}

		projectService.notifyChildren(project);

		return REDIRECT + projectId;
	}

	/**
	 * Edit an existing Product of the current Project.
	 * 
	 * @param projectId
	 *            The id of the current Project.
	 * @param appId
	 *            The id of the Product.
	 * @param title
	 *            The title of the Product.
	 * @param description
	 *            The description of the Product.
	 * @return A redirect to the Project View of the current Project.
	 */
	@PostMapping("/editproduct")
	@Secured("ROLE_USER")
	public String editProduct(@RequestParam(value = "id", required = true) Integer projectId,
			@RequestParam(value = "appid", required = true) Integer appId,
			@RequestParam(value = "apptitle", required = true) String title,
			@RequestParam(value = "appdesc") String description) {

		log.debug("Called editProduct with projectId={} appId=[} title={} description={}", projectId, appId, title,
				description);

		Project project = projectService.loadProject(projectId);

		// Check for duplicate title
		for (Product p : project.getProductList()) {
			if (p.getTitle().equals(title) && !p.getId().equals(appId)) {
				return REDIRECT + projectId + "&ProductDuplicateTitleError=true&title=" + title;
			}
		}

		Product product = productService.loadProduct(appId);

		product.setTitle(title);
		product.setDescription(description);

		productService.saveProduct(product);

		return REDIRECT + projectId;
	}

	/**
	 * Deletes an existing Product of the current Project.
	 * 
	 * @param projectId
	 *            The id of the current Project.
	 * @param appId
	 *            The id of the Project.
	 * @param httpRequest
	 *            httpRequestObject
	 * @return A redirect to the Project View of the current Project.
	 */
	@PostMapping("/deleteproduct")
	@Secured("ROLE_USER")
	public String deleteProduct(@RequestParam(value = "id", required = true) Integer projectId,
			@RequestParam(value = "appid", required = true) Integer appId, HttpServletRequest httpRequest) {

		log.debug("Called deleteProduct with projectId={} appId={}", projectId, appId);

		Product product = productService.loadProduct(appId);

		trackingService.trackDeletedProduct(projectId, product.getId(), httpRequest);

		productService.deleteProduct(product);

		Project project = projectService.loadProject(projectId);
		projectService.notifyChildren(project);

		return REDIRECT + projectId;
	}

	/**
	 * Edits the title and description of the current Project.
	 * 
	 * @param projectId
	 *            The id of the current Project.
	 * @param pTitle
	 *            The new title.
	 * @param pDescription
	 *            The new description.
	 * @param httpRequest
	 *            httpRequestObject
	 * @return A redirect to the Project View of the current Project.
	 */
	@PostMapping("/editproject")
	@Secured("ROLE_USER")
	public String editProject(@RequestParam(value = "id", required = true) Integer projectId,
			@RequestParam(value = "ptitle", required = true) String pTitle,
			@RequestParam(value = "pdesc") String pDescription, HttpServletRequest httpRequest) {

		log.debug("Called editProject with projectId={} pTitle={} pDescription={}", projectId, pTitle, pDescription);

		Project project = projectService.loadProject(projectId);

		User owner = project.getUser();

		List<Project> projectList = projectService.loadAllProjectsOfUser(owner);

		// Check for duplicate Title
		for (Project p : projectList) {

			if (p.getTitle().equals(pTitle) && !(p.getId().equals(project.getId()))) {
				return REDIRECT + projectId + "&ProjectDuplicateTitleError=true&title=" + pTitle;
			}
		}

		project.setTitle(pTitle);
		project.setDescription(pDescription);

		try {
			// Track altered project information
			project = trackingService.trackProjectInformation(project, httpRequest);
			projectService.saveProject(project);
		} catch (DatabaseException e) {
			log.error("Error while saving project.");
			return REDIRECT + projectId + "&dberror";
		}

		return REDIRECT + projectId;
	}

	/**
	 * Deletes the feature with the given id.
	 * 
	 * @param projectId
	 *            The id of the current project.
	 * @param featureId
	 *            The id of the feature to delete.
	 * @param httpRequest
	 *            httpRequestObject
	 * @return redirect to the project view.
	 */
	@PostMapping("/deletefeature")
	@Secured("ROLE_USER")
	public String deleteFeature(@RequestParam(value = "projectId", required = true) Integer projectId,
			@RequestParam(value = "featureId", required = true) Integer featureId, HttpServletRequest httpRequest) {

		trackingService.trackDeletedFeature(projectId, featureId, httpRequest);

		featureService.deleteFeature(featureId);

		Project project = projectService.loadProject(projectId);
		projectService.notifyChildren(project);

		return REDIRECT + projectId;
	}
	
	/**
	 * Copies the project with the given id.
	 * 
	 * @param projectId
	 *            the project to copy.
	 * @param title
	 *            the new title.
	 * @param description
	 *            the new description.
	 * 
	 * @param httpRequest
	 *            httpRequestObject
	 * @return the project view of the copied project.
	 */
	@PostMapping("/copyproject")
	@Secured("ROLE_USER")
	public String copyProject(@RequestParam(value = "id") Integer projectId,
			@RequestParam(value = "title") String title, @RequestParam(value = "description") String description,
			HttpServletRequest httpRequest) {
		String email = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getUsername();
		User user = userService.loadUser(email);
		Project project = null;
		try {
			project = projectService.copyProject(projectId, title, description, user);
		} catch (SameUserException e) {
			log.error("User tried to copy project he already owns");
			return REDIRECT + projectId + "&copyerror";
		}

		if (project != null) {
			// Create tracking for copied project from parent project
			trackingService.trackCopiedProject(projectId, project, httpRequest);
			return REDIRECT + project.getId();
		} else {
			return REDIRECT + projectId + "&copyerror";
		}

	}

	/**
	 * Resets updatedParent and redirects to the parents project view
	 * 
	 * @param projectId
	 *            The id of the current project.
	 * @param parentId
	 *            The id of the current projects parent.
	 * @return A redirect to the projectView of the parent project.
	 * @author Robert Völkner
	 */

	@PostMapping("/parentproject")
	public String parentProject(@RequestParam(value = "id", required = true) Integer projectId,
			@RequestParam(value = "parentId", required = true) String parentId) {

		Project project = projectService.loadProject(projectId);
		project.setUpdatedparent(false);
		projectService.saveProject(project);

		return REDIRECT + parentId;
	}

	// Added rateProject function
	/**
	 * Vote up the copied project
	 * 
	 * @param projectId
	 * 			The id of the current project
	 */
	@PostMapping(path = "/voteupproject")
	@Secured("ROLE_USER")
	public String voteUpProject(@RequestParam(value = "projectId", required = true) Integer projectId,
								HttpServletRequest httpRequest) {
		Project project = projectService.loadProject(projectId);
		String email = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getUsername();
		User user = userService.loadUser(email);
		project.addOneToUpVote();
		project.addRatedUser(user);
		trackingService.trackRatedProject(project, httpRequest);
		projectService.saveProject(project);
		return REDIRECT + projectId;
	}
	
	@PostMapping(path = "/votedownproject")
	@Secured("ROLE_USER")
	public String voteDownProject(@RequestParam(value = "projectId", required = true) Integer projectId,
									HttpServletRequest httpRequest) {
		Project project = projectService.loadProject(projectId);
		String email = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getUsername();
		User user = userService.loadUser(email);
		project.addOneToDownVote();
		project.addRatedUser(user);
		trackingService.trackRatedProject(project, httpRequest);
		projectService.saveProject(project);
		return REDIRECT + projectId;
	}
	
}
