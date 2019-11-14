package com.ghofrani.htw.RAN2.controller.error;

/**
 * Should be thrown when a new user tries to register an account with an
 * username address that is already associated with an existing account.
 * 
 * @author Daniel Wahlmann
 *
 */
public class UsernameExistsException extends RuntimeException {

	private static final long serialVersionUID = -7908227912859669341L;

	public UsernameExistsException() {
		super();
	}

	public UsernameExistsException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UsernameExistsException(final String message) {
		super(message);
	}

	public UsernameExistsException(final Throwable cause) {
		super(cause);
	}
}
