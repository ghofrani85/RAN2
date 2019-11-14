package com.ghofrani.htw.RAN2.controller.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ghofrani.htw.RAN2.controller.error.DatabaseException;
import com.ghofrani.htw.RAN2.controller.error.EmailExistsException;
import com.ghofrani.htw.RAN2.controller.error.UsernameExistsException;
import com.ghofrani.htw.RAN2.controller.token.AbstractToken;
import com.ghofrani.htw.RAN2.controller.token.PasswordResetToken;
import com.ghofrani.htw.RAN2.controller.token.VerificationToken;
import com.ghofrani.htw.RAN2.controller.transmission.UserData;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Provides an interface for handling the interaction with the user database.
 * 
 * @author Daniel Wahlmann
 *
 */
public interface IUserService {

	/**
	 * Loads the user with the given email. Throws an exception if no user with the
	 * email exists.
	 * 
	 * @param email Email of User
	 * @return User that was loaded
	 * @throws UsernameNotFoundException When user does not exist
	 */
	User loadUser(String email) throws UsernameNotFoundException;

	/**
	 * Registers a new account with the given UserData.
	 * 
	 * @param userData Data of new User
	 * @return Created new User
	 * @throws EmailExistsException When email already exists
	 * @throws UsernameExistsException Username already exists
	 */
	User registerNewUserAccount(UserData userData) throws EmailExistsException;

	/**
	 * Creates a new verification token for the given user.
	 * 
	 * @param user User belongon to token
	 * @param token token String
	 */
	void createVerificationToken(User user, String token);

	/**
	 * Gets the verification token from the database.
	 * 
	 * @param token tokenString of token
	 * @return The loaded Token
	 */
	AbstractToken getVerificationToken(String token);

	/**
	 * Saves the user to the database.
	 * 
	 * @param user User to be saved
	 * @return Saved User Object
	 */
	User saveRegisteredUser(User user) throws DatabaseException;

	/**
	 * Gets the user associated with the given verification token.
	 * 
	 * @param verificationToken token for user
	 * @return User loaded
	 */
	User getVerificationUser(String verificationToken);

	/**
	 * Creates a new PasswordResetToken and saves it in the database.
	 * 
	 * @param user User Object
	 * @param token Token String
	 */
	void createPasswordResetToken(User user, String token);

	/**
	 * Gets the PasswordResetToken from the database.
	 * 
	 * @param token Token String
	 * @return Loaded Token
	 */
	AbstractToken getPasswordResetToken(String token);

	/**
	 * Deletes the given verification token.
	 * 
	 * @param verificationToken token to be deleted
	 */
	void deleteVerificationToken(AbstractToken verificationToken);

	/**
	 * Changes the password of the given user to the given one.
	 * 
	 * @param user User to be changed
	 * @param password new Password string
	 */
	void changePassword(User user, String password);

	/**
	 * Deletes the given user.
	 * 
	 * @param user User to be deleted
	 */
	void deleteUser(User user);

	/**
	 * Deletes the given password reset token.
	 * 
	 * @param resetToken token to be deleted
	 */
	void deletePasswordResetToken(AbstractToken resetToken);

	/**
	 * Gets a List of all verification tokens.
	 * 
	 * @return List of tokens
	 */
	List<VerificationToken> getVerificationTokens();

	/**
	 * Gets a List of all password reset tokens.
	 * 
	 * @return List of tokens
	 */
	List<PasswordResetToken> getPasswordResetTokens();

	/**
	 * Resets the daily upload limit for all users.
	 */
	void resetDailyUploadLimit();
}
