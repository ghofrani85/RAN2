package com.ghofrani.htw.RAN2.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.controller.transmission.UserSettings;

/**
 * Helper-class for updating the SecurityContextHolder after changing the user
 * settings of the logged in account. Needed because of the different
 * interpretation of the user class from spring.
 * 
 * @author Robert Völkner
 *
 */
@Component
public class SecurityAccess {

	private SecurityAccess() {
	}

	/**
	 *  Updates SecurityContextHolder with new user settings.
	 * @param updatedUser User that was updated
	 */
	public void updateSecurityContext(UserSettings updatedUser) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = new User(updatedUser.getEmail(), updatedUser.getOldPassword(), auth.getAuthorities());
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
				auth.getAuthorities());
		token.setDetails(auth.getDetails());
		SecurityContextHolder.getContext().setAuthentication(token);

	}

}
