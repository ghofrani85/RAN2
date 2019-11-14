package com.ghofrani.htw.RAN2.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The model to represent a user at application level.
 * 
 * @author Tobias Powelske
 * @author Daniel Wahlmann
 *
 */
public class User {
	private Integer id;
	private String username;
	private String password;
	private boolean enabled;
	private boolean locked;
	private String email;
	private List<String> roles;
	private Date registrationDate;
	private Long totalDataVolume;
	private Long dailyUploadVolume;

	/**
	 * Constructor for a new user.
	 */
	public User() {
		this.id = null;
		this.username = null;
		this.password = null;
		this.enabled = false;
		this.locked = false;
		this.email = null;
		this.roles = new ArrayList<>();
		this.registrationDate = null;
		this.totalDataVolume = null;
		this.dailyUploadVolume = null;
	}

	/**
	 * Constructor for an existing user.
	 * 
	 * @param id
	 *            the id of the user
	 * @param username
	 *            the username of the user
	 * @param password
	 *            the password of the user
	 * @param email
	 *            the email of the user
	 * @param enabled
	 *            whether the user is enabled or not
	 * @param locked
	 *            if the user is locked by admin
	 * @param roles
	 *            Roles this user has
	 * @param registrationDate
	 *            the registration Date
	 * @param totalDataVolume
	 *            the total uploaded data in byte.
	 * @param dailyUpload
	 *            the data uploaded in the last day in byte.
	 */
	public User(Integer id, String username, String password, String email, boolean enabled, boolean locked,
			List<String> roles, Date registrationDate, Long totalDataVolume, Long dailyUpload) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.enabled = enabled;
		this.locked = locked;
		if (roles != null) {
			this.roles = roles;
		} else {
			this.roles = new ArrayList<>();
		}
		if (registrationDate != null) {
			this.registrationDate = new Date(registrationDate.getTime());
		} else {
			this.registrationDate = new Date();
		}

		this.totalDataVolume = totalDataVolume;
		this.dailyUploadVolume = dailyUpload;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	 * @return the roles
	 */
	public List<String> getRoles() {
		return this.roles;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	/**
	 * @param role
	 *            the role to add to the roles
	 */
	public void addRole(String role) {
		if (role != null && !role.isEmpty()) {
			this.roles.add(role);
		}
	}

	/**
	 * @return the lock attribute
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Sets the lock attribute.
	 * 
	 * @param locked
	 *            the attribute to be set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dailyUploadVolume == null) ? 0 : dailyUploadVolume.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (locked ? 1231 : 1237);
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((registrationDate == null) ? 0 : registrationDate.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((totalDataVolume == null) ? 0 : totalDataVolume.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/**
	 * Equals method, only compares the ids because the content is saved in the
	 * database.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		if (this.id == null || other.getId() == null) {
			return false;
		}
		return this.id.equals(other.getId());
	}

	/**
	 * @return the registrationDate
	 */
	public Date getRegistrationDate() {
		return new Date(registrationDate.getTime());
	}

	/**
	 * @param registrationDate
	 *            the registrationDate to set
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = new Date(registrationDate.getTime());
	}

	/**
	 * @return the totalDataVolume
	 */
	public Long getTotalDataVolume() {
		return totalDataVolume;
	}

	/**
	 * @param totalDataVolume
	 *            the totalDataVolume to set
	 */
	public void setTotalDataVolume(Long totalDataVolume) {
		this.totalDataVolume = totalDataVolume;
	}

	/**
	 * @return the dailyUpload
	 */
	public Long getDailyUploadVolume() {
		return dailyUploadVolume;
	}

	/**
	 * @param dailyUpload
	 *            the dailyUpload to set
	 */
	public void setDailyUploadVolume(Long dailyUpload) {
		this.dailyUploadVolume = dailyUpload;
	}
}
