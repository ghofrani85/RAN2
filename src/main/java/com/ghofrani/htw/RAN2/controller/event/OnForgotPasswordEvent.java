package com.ghofrani.htw.RAN2.controller.event;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.ghofrani.htw.RAN2.model.User;

/**
 * ApplicationEvent implementation for handling a forgotten password.
 * 
 * @author Daniel Wahlmann
 *
 */
public class OnForgotPasswordEvent extends ApplicationEvent {

	private static final long serialVersionUID = 5224707371959690171L;
	private String appUrl;
	private Locale locale;
	private transient User user;

	/**
	 * Constructor.
	 * 
	 * @param user User that caused the event
	 * @param locale Language Object
	 * @param appUrl The URL to the application
	 */
	public OnForgotPasswordEvent(User user, Locale locale, String appUrl) {
		super(user);

		this.user = user;
		this.locale = locale;
		this.appUrl = appUrl;
	}

	/**
	 * @return the appUrl
	 */
	public String getAppUrl() {
		return appUrl;
	}

	/**
	 * @param appUrl
	 *            the appUrl to set
	 */
	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
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

}
