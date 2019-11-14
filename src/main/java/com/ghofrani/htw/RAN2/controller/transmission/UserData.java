package com.ghofrani.htw.RAN2.controller.transmission;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.ghofrani.htw.RAN2.controller.validation.PasswordMatches;
import com.ghofrani.htw.RAN2.controller.validation.ValidEmail;

/**
 * Data Transfer Object for transmitting the registration data.
 * 
 * @author Daniel Wahlmann
 *
 */
@PasswordMatches(message = "{validation.passwordMatch}")
public class UserData {

	@NotNull
	@NotEmpty
	@ValidEmail(message = "{validation.email}")
	private String email;

	@NotNull
	@NotEmpty
	@Length(min = 2, max = 30)
	private String username;

	@NotNull
	@Length(min = 8, max = 256)
	private String password;
	private String matchingPassword;

	@NotNull
	@AssertTrue
	private boolean acceptTos;

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
		this.email = email.toLowerCase();
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

	/**
	 * @return the matchingPassword
	 */
	public String getMatchingPassword() {
		return matchingPassword;
	}

	/**
	 * @param matchingPassword
	 *            the matchingPassword to set
	 */
	public void setMatchingPassword(String matchingPassword) {
		this.matchingPassword = matchingPassword;
	}

	/**
	 * @return the acceptsUserAgreement
	 */
	public boolean isAcceptTos() {
		return acceptTos;
	}

	/**
	 * @param acceptsUserAgreement
	 *            the acceptsUserAgreement to set
	 */
	public void setAcceptTos(boolean acceptsUserAgreement) {
		this.acceptTos = acceptsUserAgreement;
	}

}
