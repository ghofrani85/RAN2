package com.ghofrani.htw.RAN2.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.error.EmailExistsException;
import com.ghofrani.htw.RAN2.controller.error.UsernameExistsException;
import com.ghofrani.htw.RAN2.controller.event.OnRegistrationCompleteEvent;
import com.ghofrani.htw.RAN2.controller.service.IUserService;
import com.ghofrani.htw.RAN2.controller.setup.ConfigData;
import com.ghofrani.htw.RAN2.controller.token.AbstractToken;
import com.ghofrani.htw.RAN2.controller.transmission.UserData;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Controller for the registration process.
 * 
 * @author Daniel Wahlmann
 *
 */
@Controller
public class RegistrationController {

	private static final String NOTICE = "notice";

	private static final String REGISTER = "register";

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private IUserService service;

	@Autowired
	private MessageSource messages;

	@Autowired
	private LocaleResolver resolver;

	@Autowired
	private ConfigData configData;

	/**
	 * Creates the registration View and binds the UserData object.
	 * 
	 * @param request RequestObject
	 * @param model ModelObject for the View
	 * @return The registration View.
	 */
	@GetMapping(path = "/register")
	public String registrationForm(WebRequest request, Model model) {

		log.info("Called registration");
		String tosText;
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(configData.getTosTextPath()));
			tosText = new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			log.error("Error while trying to read tos file: {}", e.getMessage());
			tosText = "";
		}

		UserData userData = new UserData();
		model.addAttribute("user", userData);
		model.addAttribute("termsOfUse", tosText);

		return REGISTER;
	}

	/**
	 * Registers a new User account.
	 * 
	 * @param userData Data of new User
	 * @param result ResultObject
	 * @param request RequestObject
	 * @param errors Errors
	 * @param httpRequest httpRequestObject
	 * @return the registration view with a success message on successful
	 *         registration or error messages on validation failure.
	 */
	@PostMapping(path = "/register")
	public ModelAndView registerAccount(@ModelAttribute("user") @Valid UserData userData, BindingResult result,
			WebRequest request, Errors errors, HttpServletRequest httpRequest) {

		log.info("Called registerAccount");

		User newUser = null;

		if (!result.hasErrors()) {
			newUser = createUserAccount(userData, result);
		}
		if (newUser == null) {
			return new ModelAndView(REGISTER, "user", userData);
		}

		try {
			String appUrl = httpRequest.getRequestURL().toString(); // to provide a valid url for the token link.
			log.info("Publishing OnRegistrationEvent");
			eventPublisher.publishEvent(
					new OnRegistrationCompleteEvent(newUser, resolver.resolveLocale(httpRequest), appUrl));
		} catch (Exception e) {
			result.rejectValue("email", "message.sendMailError");
			service.deleteUser(newUser);
			return new ModelAndView(REGISTER, "user", userData);
		}

		log.info("Registration successful");

		return new ModelAndView("redirect:register?success");
	}

	/**
	 * Activates the account if the registrationconfirm URL is called with a valid
	 * verification token.
	 * 
	 * @param request RequestObject
	 * @param model ModelObject for the view
	 * @param token TokenString
	 * @param httpRequest httpRequestObject
	 * @return a success of failure message depending on the validity of the
	 *         registration token.
	 */
	@GetMapping(path = "/register/registrationconfirm")
	public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token,
			HttpServletRequest httpRequest) {
		log.info("Called confirmRegistration");

		Locale locale = resolver.resolveLocale(httpRequest);

		AbstractToken verificationToken = service.getVerificationToken(token);
		if (verificationToken == null) {
			String message = messages.getMessage("auth.message.invalidToken", null, locale);
			model.addAttribute("message", message);
			return NOTICE;
		}

		User user = verificationToken.getUser();
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpirationDate().getTime() - cal.getTime().getTime()) <= 0) {
			String messageValue = messages.getMessage("auth.message.expired", null, locale);
			model.addAttribute("message", messageValue);
			return NOTICE;
		}

		user.setEnabled(true);
		service.saveRegisteredUser(user);

		service.deleteVerificationToken(verificationToken);

		log.info("Account successfully activated");

		String message = messages.getMessage("auth.messsage.registerSuccess", null, locale);
		model.addAttribute("message", message);
		return NOTICE;
	}

	/**
	 * Creates a new user account.
	 * 
	 * @param userData Data of new User
	 * @param result ResultObject
	 * @return the user object associated with the newly created user account or
	 *         null, if the email or username already exists.
	 */
	private User createUserAccount(UserData userData, BindingResult result) {

		log.info("Called createUserAccount");

		User user = null;
		try {
			user = service.registerNewUserAccount(userData);
		} catch (EmailExistsException e) {
			log.warn("Email already exists");
			result.rejectValue("email", "message.regErrorEmail");
			return null;
		} catch (UsernameExistsException e) {
			log.warn("Username already exists");
			result.rejectValue("username", "message.regErrorUsername");
			return null;
		}
		log.info("Sucessfully created user account");
		return user;
	}
}
