package com.ghofrani.htw.RAN2.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Controller for the admin view. Handles http requests.
 * 
 * @author Robert Völkner
 *
 */
@Controller
public class AdminViewController {

	/**
	 * Database Interface for Users.
	 */
	@Autowired
	private UserAccess userAccess;

	/**
	 * Creates the admin view and binds the userList object.
	 * 
	 * @param request
	 *            WebRequestObject
	 * @param model
	 *            ModelObject
	 * @return the admin view.
	 */
	@GetMapping(path = "/admin")
	@Secured("ROLE_ADMIN")
	public String userSettingsForm(WebRequest request, Model model) {

		List<User> userList = new ArrayList<User>(userAccess.selectUsers());

		// Show Admin seperate from other users
		List<User> iterList = new ArrayList<User>(userList);
		for (User u : iterList) {

			if (u.getRoles().contains("ROLE_ADMIN")) {
				model.addAttribute("admin", u);
				userList.remove(u);
			}
		}

		model.addAttribute("userList", userList);

		return "/admin";
	}

	/**
	 * Deactivates a specific account.
	 * 
	 * @param userId
	 *            ID of the User to be deactivated
	 * @param result
	 *            BindingResult Object
	 * @param request
	 *            RequestObject
	 * @param errors
	 *            Errors Object
	 * @return Website
	 */
	@Secured("ROLE_ADMIN")
	@PostMapping(path = "/deactivateUser")
	public ModelAndView deactivateUser(@ModelAttribute("id") Integer userId, BindingResult result, WebRequest request,
			Errors errors) {

		User user = userAccess.selectUsersByID(userId);
		List<User> userList = userAccess.selectUsers();

		if (user.getRoles().contains("ROLE_ADMIN")) {
			return new ModelAndView("redirect:admin", "userList", userList);
		}

		user.setLocked(true);
		userAccess.updateUser(user);

		return new ModelAndView("redirect:admin", "userList", userList);

	}

	/**
	 * Reactivates a specific account.
	 * 
	 * @param userId
	 *            ID of the User to be reactivated
	 * @param result
	 *            BindingResult Object
	 * @param request
	 *            RequestObject
	 * @param errors
	 *            Errors Object
	 * @return Website
	 */
	@Secured("ROLE_ADMIN")
	@PostMapping(path = "/reactivateUser")
	public ModelAndView reactivateUser(@ModelAttribute("id") Integer userId, BindingResult result, WebRequest request,
			Errors errors) {

		User user = userAccess.selectUsersByID(userId);
		user.setLocked(false);
		userAccess.updateUser(user);
		List<User> userList = userAccess.selectUsers();
		return new ModelAndView("redirect:admin", "userList", userList);

	}

}
