package com.ghofrani.htw.RAN2.controller.transmission;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.ghofrani.htw.RAN2.controller.validation.PasswordMatches;

/**
 * Data transfer object for updating the password.
 * 
 * @author Daniel Wahlmann
 *
 */
@PasswordMatches
public class PasswordReset {

	@NotNull
	@Length(min = 8)
	private String password;
	private String matchingPassword;

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

}
