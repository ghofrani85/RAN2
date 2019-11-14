package com.ghofrani.htw.RAN2.controller.token;

import java.sql.Date;

import com.ghofrani.htw.RAN2.model.User;

/**
 * Token for resetting the password if it was forgotten.
 * 
 * @author Daniel Wahlmann
 *
 */
public class PasswordResetToken extends AbstractToken {

	/**
	 * Default Constructor.
	 */
	public PasswordResetToken() {
		this.id = null;
		this.token = null;
		this.user = null;
		this.expirationDate = calculateExpirationDate(EXPIRATION);
	}

	/**
	 * Creates a new password reset token.
	 * 
	 * @param token TokenString
	 * @param user User associated with that token
	 */
	public PasswordResetToken(String token, User user) {
		this.id = null;
		this.token = token;
		this.user = user;
		this.expirationDate = calculateExpirationDate(EXPIRATION);
	}

	/**
	 * Constructs a password reset token with the given data.
	 * 
	 * @param id ID of the token
	 * @param token tokenString
	 * @param user User associated with that token
	 * @param expirationDate Date on which the token becomes invalid
	 */
	public PasswordResetToken(Integer id, String token, User user, Date expirationDate) {
		this.id = id;
		this.token = token;
		this.user = user;
		this.expirationDate = new Date(expirationDate.getTime());
	}

}
