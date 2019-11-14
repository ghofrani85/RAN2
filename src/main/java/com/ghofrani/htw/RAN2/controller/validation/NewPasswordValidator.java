package com.ghofrani.htw.RAN2.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator to check for password length greater or equal 8 or equals 0.
 * 
 * @author Robert Völkner
 *
 */
public class NewPasswordValidator implements ConstraintValidator<PasswordFits, Object> {

	/**
	 * Initialization (not needed)
	 */
	@Override
	public void initialize(PasswordFits constraintAnnotation) {
		// No initialization needed.
	}

	/**
	 * @param obj
	 *            the password that needs to be checked
	 * @param context
	 *            the validation context
	 * @return the result if the password is valid or not
	 */
	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		String password = (String) obj;
		return (password.length() >= 8) || (password.length() == 0);
	}

}
