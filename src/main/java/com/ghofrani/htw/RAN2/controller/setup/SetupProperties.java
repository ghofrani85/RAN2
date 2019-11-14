package com.ghofrani.htw.RAN2.controller.setup;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import com.ghofrani.htw.RAN2.controller.validation.ValidEmail;

/**
 * Stores and provides data used for setup of the application on startup.
 * 
 * @author Daniel Wahlmann
 *
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = "setup")
public class SetupProperties {

	/**
	 * The admin email.
	 */
	@NotEmpty
	@ValidEmail
	private String email;

	/**
	 * The admin username.
	 */
	@NotEmpty
	private String username;

	/**
	 * The admin password.
	 */
	@Length(min = 8, max = 256)
	private String password;

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the firstName
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
