package com.ghofrani.htw.RAN2.controller;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.event.OnForgotPasswordEvent;
import com.ghofrani.htw.RAN2.controller.service.IUserService;
import com.ghofrani.htw.RAN2.controller.token.AbstractToken;
import com.ghofrani.htw.RAN2.controller.transmission.PasswordReset;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Controller for sending emails with password reset tokens in case the user has
 * forgotten his password.
 * 
 * @author Daniel Wahlmann
 *
 */
@Controller
public class ForgotPasswordController {

	private static final String NOTICE = "notice";

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private MessageSource messages;

	@Autowired
	private IUserService userService;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private LocaleResolver resolver;

	/**
	 * Handles requests to the forgot password form.
	 * 
	 * @param email Email of user who forgot password
	 * @param model Model Object for View
	 * @param request RequestObject
	 * @param httpRequest httpRequestObject
	 * @return Notice Data with info
	 */
	@PostMapping("/forgotpassword")
	public String forgotPassword(@ModelAttribute("email") String email, Model model, WebRequest request,
			HttpServletRequest httpRequest) {

		log.info("Called forgotPassword");
		User user = null;

		try {
			user = userService.loadUser(email);
		} catch (UsernameNotFoundException e) {
			String message = messages.getMessage("user.emailError", null, resolver.resolveLocale(httpRequest));
			model.addAttribute("message", message);
			return NOTICE;
		}

		try {
			String appUrl = httpRequest.getRequestURL().toString(); // to make sure the email contains a valid url.
			eventPublisher.publishEvent(new OnForgotPasswordEvent(user, resolver.resolveLocale(httpRequest), appUrl));
		} catch (Exception e) {
			String message = messages.getMessage("user.sendMailError", null, resolver.resolveLocale(httpRequest));
			model.addAttribute("message", message);
			return NOTICE;
		}

		log.info("Password reset token sent");

		String message = messages.getMessage("user.passwordReset", null, resolver.resolveLocale(httpRequest));
		model.addAttribute("message", message);
		return NOTICE;
	}

	/**
	 * Checks if the password reset token is valid. If it is, grants a
	 * CHANGE_PASSWORD_PRIVILEGE.
	 * 
	 * @param request RequestObject
	 * @param model Model Object for View
	 * @param token Token to be checked
	 * @param httpRequest httpRequestObject
	 * @return Notice with info
	 */
	@GetMapping(path = "/forgotpassword/passwordreset")
	public String resetPassword(WebRequest request, Model model, @RequestParam("token") String token,
			HttpServletRequest httpRequest) {

		log.info("resetPassword called");

		Locale locale = resolver.resolveLocale(httpRequest);

		AbstractToken passwordResetToken = userService.getPasswordResetToken(token);
		if (passwordResetToken == null) {
			String message = messages.getMessage("user.password.invalidToken", null, locale);
			model.addAttribute("message", message);
			return NOTICE;
		}

		User user = passwordResetToken.getUser();
		Calendar cal = Calendar.getInstance();
		if ((passwordResetToken.getExpirationDate().getTime() - cal.getTime().getTime()) <= 0) {
			String message = messages.getMessage("user.password.tokenExpired", null, locale);
			model.addAttribute("message", message);
			return NOTICE;
		}

		Authentication auth = new UsernamePasswordAuthenticationToken(user, null,
				Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
		SecurityContextHolder.getContext().setAuthentication(auth);

		userService.deletePasswordResetToken(passwordResetToken);
		log.info("Valid password reset token received, granted change password privilege");
		return "redirect:/updatePassword";
	}

	/**
	 * Provides the form for entering a new password.
	 * 
	 * @param request RequestObject
	 * @param model ModelObject for View
	 * @return Generated View for entering password
	 */
	@GetMapping(path = "/updatePassword")
	public String updatePasswordForm(WebRequest request, Model model) {

		log.info("GET updatePassword called");

		PasswordReset passwordReset = new PasswordReset();
		model.addAttribute("passwordReset", passwordReset);

		return "updatePassword";
	}

	/**
	 * Updates the password.
	 * 
	 * @param passwordReset DatatransferObject for reseting password
	 * @param result ResultObject
	 * @param request RequestObject
	 * @param error If there was an Error
	 * @return redirect to Login
	 */
	@PostMapping(path = "/updatePassword")
	public ModelAndView updatePassword(@ModelAttribute("passwordReset") @Valid PasswordReset passwordReset,
			BindingResult result, WebRequest request, Error error) {
		log.info("POST updatePassword called");

		if (result.hasErrors()) {
			return new ModelAndView("updatePassword", "passwordReset", passwordReset);
		}

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userService.changePassword(user, passwordReset.getPassword());

		log.info("Password successfully updated");
		return new ModelAndView("redirect:login");
	}

}
