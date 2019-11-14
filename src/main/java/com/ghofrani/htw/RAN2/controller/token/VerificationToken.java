package com.ghofrani.htw.RAN2.controller.token;

import java.sql.Date;

import com.ghofrani.htw.RAN2.model.User;

/**
 * Verification Token used for activation of new accounts via email.
 * 
 * @author Daniel Wahlmann
 *
 */
public class VerificationToken extends AbstractToken {

	/**
	 * Default Constructor.
	 */
	public VerificationToken() {
		this.id = null;
		this.token = null;
		this.user = null;
		this.expirationDate = calculateExpirationDate(EXPIRATION);
	}

	/**
	 * Constructs a new verification token.
	 * 
	 * @param token TokenString
	 * @param user User associated with that token
	 */
	public VerificationToken(String token, User user) {
		this.id = null;
		this.token = token;
		this.user = user;
		this.expirationDate = calculateExpirationDate(EXPIRATION);
	}

	/**
	 * Constructs a verification token with given data (e.g. from the database).
	 * 
	 * @param id ID of the token
	 * @param token tokenString
	 * @param user User associated with that token
	 * @param expirationDate Date on which the token becomes invalid
	 */
	public VerificationToken(Integer id, String token, User user, Date expirationDate) {
		this.id = id;
		this.token = token;
		this.user = user;
		this.expirationDate = new Date(expirationDate.getTime());
	}

}
