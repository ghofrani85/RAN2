package com.ghofrani.htw.RAN2.controller.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Checks is the given String is a valid email address.
 * 
 * @author Daniel Wahlmann
 *
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

	private Pattern pattern;
	private Matcher matcher;
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*\\.(.[A-Za-z]{1,})$";

	@Override
	public void initialize(ValidEmail constraintAnnotation) {
		// No initialization needed.
	}

	/**
	 * Validates the given email.
	 */
	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {

		return (validateEmail(email));
	}

	/**
	 * Compare given email with provided pattern.
	 * 
	 * @param email
	 * @return true iff the email matches the pattern.
	 */
	private boolean validateEmail(String email) {
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}

}
