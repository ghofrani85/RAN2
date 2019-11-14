package com.ghofrani.htw.RAN2.controller.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation for checking for fitting new password.
 * 
 * @author Robert Völkner
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Constraint(validatedBy = NewPasswordValidator.class)
public @interface PasswordFits {
	String message() default "The password length must be greater than 7 or 0";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
