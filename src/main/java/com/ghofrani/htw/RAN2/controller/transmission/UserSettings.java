package com.ghofrani.htw.RAN2.controller.transmission;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.ghofrani.htw.RAN2.controller.validation.PasswordFits;
import com.ghofrani.htw.RAN2.controller.validation.PasswordMatches;
import com.ghofrani.htw.RAN2.controller.validation.ValidEmail;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Data Transfer Object for transmitting the user settings data.
 * 
 * @author Robert Völkner
 *
 */
@PasswordMatches
public class UserSettings {

	private Integer userId;

	@ValidEmail
	@NotNull
	@NotEmpty
	private String email;

	@NotNull
	@NotEmpty
	private String username;

	@NotNull
	private String oldPassword;

	@NotNull
	@PasswordFits
	private String newPassword;
	private String repeatNewPassword;

	@NotNull
	private Map<String, String> languageMap;

	/**
	 * Default Constructor
	 */
	public UserSettings() {
		this.userId = null;
		this.email = null;
		this.username = null;
		this.oldPassword = null;
		this.newPassword = null;
		this.repeatNewPassword = null;
		this.languageMap = new HashMap<String, String>();

	}

	/**
	 * Constructor for user settings from an existing user.
	 * 
	 * @param user
	 *            The user where to take the settings from.
	 */
	public UserSettings(User user) {
		this.userId = user.getId();
		this.email = user.getEmail();
		this.username = user.getUsername();
		this.oldPassword = user.getPassword();
		this.newPassword = "";
		this.repeatNewPassword = "";
		this.languageMap = new HashMap<String, String>();

	}

	/**
	 * 
	 * @return the languageMap
	 */
	public Map<String, String> getLanguageMap() {
		return languageMap;
	}

	/**
	 * 
	 * @param languageMap Map for the languages
	 */
	public void setLanguageMap(Map<String, String> languageMap) {
		this.languageMap = languageMap;
	}

	/**
	 * @return the userID
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userID to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

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
	 * @return the username
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
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setOldPassword(String password) {
		this.oldPassword = password;
	}

	/**
	 * 
	 * @return the new password
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * 
	 * @param newPassword the new password
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * 
	 * @return the repeated new password
	 */
	public String getRepeatNewPassword() {
		return repeatNewPassword;
	}

	/**
	 * 
	 * @param repeatNewPassword the new password repeated
	 */
	public void setRepeatNewPassword(String repeatNewPassword) {
		this.repeatNewPassword = repeatNewPassword;
	}

	/**
	 * Converts the user information to a string
	 */
	public String toString() {
		return "" + this.userId + " " + this.email + " " + this.username + " " + this.oldPassword.length() + " "
				+ this.newPassword.length() + " " + this.repeatNewPassword.length();
	}

}
