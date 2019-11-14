package com.ghofrani.htw.RAN2.controller.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Custom UserDetailsService implementation.
 * 
 * @author Daniel Wahlmann
 *
 */
@Service("userDetailsService")
@Transactional
public class ProdUserDetailsService implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private UserAccess userAccess;

	/**
	 * Loads an User by email (used as username), extrats the core information and
	 * returns a Spring Security User object.
	 * 
	 * @param email
	 *            The email to look for.
	 * @return A Spring Security User object.
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		List<User> users = userAccess.selectUsersByEmail(email.toLowerCase());
		if (users.isEmpty()) {
			throw new UsernameNotFoundException("No user found with email:" + email);
		}

		User user = users.get(0);

		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;

		log.info("successfully loaded user");

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				user.isEnabled(), accountNonExpired, credentialsNonExpired, !user.isLocked(),
				getAuthorities(user.getRoles()));
	}

	/**
	 * Converts a List of Roles as String to a List of GrantedAuthority.
	 * 
	 * @param roles
	 *            A List of roles.
	 * @return A List of GrantedAuthority
	 */
	private static List<GrantedAuthority> getAuthorities(List<String> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();

		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}

}
