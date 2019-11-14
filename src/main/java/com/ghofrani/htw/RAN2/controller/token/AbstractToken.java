package com.ghofrani.htw.RAN2.controller.token;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import com.ghofrani.htw.RAN2.model.User;

/**
 * Prototype for verification tokens.
 * 
 * @author Daniel Wahlmann
 *
 */
public class AbstractToken {

	protected static final int EXPIRATION = 60 * 24;
	protected Integer id;
	protected String token;
	protected User user;
	protected Date expirationDate;

	/**
	 * Default Constructor.
	 */
	public AbstractToken() {
		super();
	}

	/**
	 * Calculates the expiration date of the verification token.
	 * 
	 * @param expirationTimeInMinutes
	 *            The time in minutes the token is valid.
	 * @return the expiration date.
	 */
	protected Date calculateExpirationDate(int expirationTimeInMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Timestamp(cal.getTime().getTime()));
		cal.add(Calendar.MINUTE, expirationTimeInMinutes);

		return new Date(cal.getTime().getTime());
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
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the expirationDate
	 */
	public Date getExpirationDate() {
		return new Date(expirationDate.getTime());
	}

	/**
	 * @param expirationDate
	 *            the expirationDate to set
	 */
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = new Date(expirationDate.getTime());
	}

}