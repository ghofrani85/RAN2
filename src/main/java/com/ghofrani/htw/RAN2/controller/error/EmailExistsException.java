package com.ghofrani.htw.RAN2.controller.error;

/**
 * Should be thrown when a new user tries to register an account with an e-mail
 * address that is already associated with an existing account.
 * 
 * @author Daniel Wahlmann
 *
 */
public class EmailExistsException extends RuntimeException {

	private static final long serialVersionUID = -3022257918282124861L;

	public EmailExistsException() {
		super();
	}

	public EmailExistsException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public EmailExistsException(final String message) {
		super(message);
	}

	public EmailExistsException(final Throwable cause) {
		super(cause);
	}
}
