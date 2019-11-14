package com.ghofrani.htw.RAN2.controller.error;

/**
 * Should be thrown when a user tries to execute a method on an object that he
 * owns that can only be executed on objects he does not own.
 * 
 * @author Daniel Wahlmann
 *
 */
public class SameUserException extends RuntimeException {

	private static final long serialVersionUID = -3500979687233807649L;

	public SameUserException() {
		super();
	}

	public SameUserException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public SameUserException(final String message) {
		super(message);
	}

	public SameUserException(final Throwable cause) {
		super(cause);
	}

}
