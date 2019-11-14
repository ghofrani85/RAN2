package com.ghofrani.htw.RAN2.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ghofrani.htw.RAN2.controller.transmission.PasswordReset;
import com.ghofrani.htw.RAN2.controller.transmission.UserData;
import com.ghofrani.htw.RAN2.controller.transmission.UserSettings;

/**
 * Validator to check for matching passwords.
 * 
 * @author Daniel Wahlmann
 *
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

	@Override
	public void initialize(PasswordMatches constraintAnnotation) {
		// No initialization needed.
	}

	/**
	 * Validates the given Object. Returns true iff the Object contains a password
	 * and a second matching password and they are equal.
	 */
	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		if (obj instanceof UserData) {
			UserData userData = (UserData) obj;
			return userData.getPassword().equals(userData.getMatchingPassword());
		}
		if (obj instanceof PasswordReset) {
			PasswordReset passwordReset = (PasswordReset) obj;
			return passwordReset.getPassword().equals(passwordReset.getMatchingPassword());
		}
		if (obj instanceof UserSettings) {
			UserSettings userSettings = (UserSettings) obj;
			return userSettings.getNewPassword().equals(userSettings.getRepeatNewPassword());
		}
		return false;
	}

}
