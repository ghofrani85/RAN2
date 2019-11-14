package com.ghofrani.htw.RAN2.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import com.ghofrani.htw.RAN2.controller.setup.ConfigData;
import com.ghofrani.htw.RAN2.controller.transmission.UserSettings;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Controller for the process of changing user settings.
 * 
 * @author Robert Völkner
 *
 */
@Controller
public class UserSettingsController {

	@Autowired
	private UserAccess userAccess;

	@Autowired
	private SecurityAccess secAccess;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ConfigData configData;

	@Autowired
	private LocaleResolver resolver;

	/**
	 * 
	 * Creates the settings view and binds the user object.
	 * 
	 * @param request
	 *            WebRequestObject
	 * @param model
	 *            The data model.
	 * @param httpRequest
	 *            The Http Request
	 * @return the userSettings page
	 */
	@GetMapping(path = "/userSettings")
	public String userSettingsForm(WebRequest request, Model model, HttpServletRequest httpRequest) {

		UserSettings userSettings = currentUserSettings();

		userSettings.setLanguageMap(createLanguageMap(httpRequest));

		model.addAttribute("user", userSettings);

		return "/userSettings";
	}

	/**
	 * 
	 * Edits the settings of an user account.
	 * 
	 * @param userSettings
	 *            The Usersettings of the user
	 * @param result
	 *            Results Object
	 * @param request
	 *            WebRequestObject
	 * @param errors
	 *            Errors Object
	 * @param httpRequest
	 *            The Http Request
	 * @return the updated usersettings page
	 */
	@PostMapping(path = "/userSettings")
	public ModelAndView editUserSettings(@ModelAttribute("user") @Valid UserSettings userSettings, BindingResult result,
			WebRequest request, Errors errors, HttpServletRequest httpRequest) {

		User oldUser = userAccess.selectUsersByID(currentUserSettings().getUserId()); // load the old user data
		UserSettings oldUserSettings = new UserSettings(oldUser);

		oldUserSettings.setLanguageMap(createLanguageMap(httpRequest));

		if (result.hasErrors()) {

			return new ModelAndView("userSettings", "user", oldUserSettings);

		} else if (!passwordEncoder.matches(userSettings.getOldPassword(), oldUser.getPassword())) { // check for
																										// correct old
																										// password

			return new ModelAndView("userSettings", "user", oldUserSettings);

		} else if (!userSettings.getRepeatNewPassword().equals(userSettings.getNewPassword())) { // check for correct
																									// repeated password

			return new ModelAndView("userSettings", "user", oldUserSettings);

		} else if (userSettings.getNewPassword().length() == 0) { // case user doesn't change password

			User updatedUser = new User(currentUserSettings().getUserId(), userSettings.getUsername(),
					oldUser.getPassword(), userSettings.getEmail(), oldUser.isEnabled(), oldUser.isLocked(),
					oldUser.getRoles(), oldUser.getRegistrationDate(), oldUser.getTotalDataVolume(),
					oldUser.getDailyUploadVolume());
			userAccess.updateUser(updatedUser);
			UserSettings updatedUserSettings = new UserSettings(updatedUser);
			secAccess.updateSecurityContext(updatedUserSettings);

			updatedUserSettings.setLanguageMap(createLanguageMap(httpRequest));

			return new ModelAndView("userSettings", "user", updatedUserSettings);

		} else { // case user does change password

			User updatedUser = new User(currentUserSettings().getUserId(), userSettings.getUsername(),
					passwordEncoder.encode(userSettings.getNewPassword()), userSettings.getEmail(), oldUser.isEnabled(),
					oldUser.isLocked(), oldUser.getRoles(), oldUser.getRegistrationDate(), oldUser.getTotalDataVolume(),
					oldUser.getDailyUploadVolume());
			userAccess.updateUser(updatedUser);
			UserSettings updatedUserSettings = new UserSettings(updatedUser);
			secAccess.updateSecurityContext(updatedUserSettings);

			updatedUserSettings.setLanguageMap(createLanguageMap(httpRequest));

			return new ModelAndView("userSettings", "user", updatedUserSettings);

		}

	}

	/**
	 * Gets the current user settings from the security context.
	 *
	 * @return user settings of the current user.
	 */
	public UserSettings currentUserSettings() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		List<User> user = userAccess.selectUsersByEmail(username);
		return new UserSettings(user.get(0));
	}

	/**
	 * Creates the hashmap of all available languages.
	 * 
	 * @param httpRequest httpRequestObject
	 * @return a hashmap of all languages
	 */
	public Map<String, String> createLanguageMap(HttpServletRequest httpRequest) {

		List<String> languageTags = configData.getSupportedLanguages();
		Locale currentLocale = resolver.resolveLocale(httpRequest);

		Map<String, String> languageMap = new HashMap<String, String>();
		for (String languageTag : languageTags) {
			Locale l = Locale.forLanguageTag(languageTag);

			languageMap.put(languageTag, l.getDisplayLanguage(currentLocale));
		}

		return languageMap;
	}

}
