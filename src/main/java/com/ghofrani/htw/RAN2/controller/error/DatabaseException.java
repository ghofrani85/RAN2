package com.ghofrani.htw.RAN2.controller.error;

/**
 * Generic database Exception.
 * 
 * @author Daniel Wahlmann
 *
 */
public class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1995457470674542329L;

	public DatabaseException() {
		super();
	}

	public DatabaseException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DatabaseException(final String message) {
		super(message);
	}

	public DatabaseException(final Throwable cause) {
		super(cause);
	}
}
