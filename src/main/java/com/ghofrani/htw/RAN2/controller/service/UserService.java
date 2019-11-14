package com.ghofrani.htw.RAN2.controller.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghofrani.htw.RAN2.controller.error.DatabaseException;
import com.ghofrani.htw.RAN2.controller.error.EmailExistsException;
import com.ghofrani.htw.RAN2.controller.error.UsernameExistsException;
import com.ghofrani.htw.RAN2.controller.token.AbstractToken;
import com.ghofrani.htw.RAN2.controller.token.PasswordResetToken;
import com.ghofrani.htw.RAN2.controller.token.VerificationToken;
import com.ghofrani.htw.RAN2.controller.transmission.UserData;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Implementation of IUserService. Provides methods for interacting with the
 * userAccess database implementation.
 * 
 * @author Daniel Wahlmann
 *
 */

@Service
public class UserService implements IUserService {

	@Autowired
	private UserAccess userAccess;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Loads the user with the given email. Throws an exception if no user with the
	 * email exists.
	 * 
	 * @param email Email of User
	 * @return User that was loaded
	 * @throws UsernameNotFoundException When user does not exist
	 */
	@Override
	public User loadUser(String email) throws UsernameNotFoundException {
		List<User> users = userAccess.selectUsersByEmail(email);

		if (users == null || users.isEmpty()) {
			throw new UsernameNotFoundException("No user found with email:" + email);
		}

		return users.get(0);
	}

	/**
	 * Registers a new account with the given UserData.
	 * 
	 * @param userData Data of new User
	 * @return Created new User
	 * @throws EmailExistsException When email already exists
	 * @throws UsernameExistsException Username already exists
	 */
	@Transactional
	@Override
	public User registerNewUserAccount(UserData userData) throws EmailExistsException, UsernameExistsException {
		if (emailExists(userData.getEmail())) {
			throw new EmailExistsException(
					"An account with the email adress " + userData.getEmail() + " alredy exists.");
		}
		if (usernameExists(userData.getUsername())) {
			throw new UsernameExistsException(
					"An account with the username" + userData.getUsername() + "already exists.");
		}

		User user = new User();
		user.setUsername(userData.getUsername());
		user.setEmail(userData.getEmail());
		user.setPassword(passwordEncoder.encode(userData.getPassword()));
		user.setRoles(Arrays.asList("ROLE_USER"));
		user.setEnabled(false);
		user.setLocked(false);
		user.setDailyUploadVolume((long) 0);
		user.setTotalDataVolume((long) 0);

		Calendar cal = Calendar.getInstance();
		user.setRegistrationDate(cal.getTime());

		return userAccess.saveUser(user);
	}

	/**
	 * Checks if an user with the given email already exists in the database.
	 * 
	 * @param email Email to be checked
	 * @return True, iff there is an user with the email in the database.
	 */
	private boolean emailExists(String email) {
		List<User> user = userAccess.selectUsersByEmail(email);

		return !user.isEmpty();
	}

	/**
	 * Checks if an user with the given username already exists in the database.
	 * 
	 * @param username
	 * @return True, iff there is an user with the username in the database.
	 */
	private boolean usernameExists(String username) {
		List<User> user = userAccess.selectUsersByUsername(username);

		return !user.isEmpty();
	}

	/**
	 * Creates a new verification token for the given user.
	 * 
	 * @param user User belongon to token
	 * @param token token String
	 */
	@Override
	public void createVerificationToken(User user, String token) {
		VerificationToken verificationToken = new VerificationToken(token, user);
		userAccess.insertVerificationToken(verificationToken);

	}

	/**
	 * Gets the verification token from the database.
	 * 
	 * @param token tokenString of token
	 * @return The loaded Token
	 */
	@Override
	public AbstractToken getVerificationToken(String token) {
		List<VerificationToken> verificationTokens = userAccess.selectVerificationTokenByToken(token);
		if (verificationTokens.isEmpty()) {
			return null;
		}
		return verificationTokens.get(0);
	}

	/**
	 * Saves the user to the database.
	 * 
	 * @param user User to be saved
	 * @return Saved User Object
	 */
	@Override
	public User saveRegisteredUser(User user) throws DatabaseException {
		User saved = userAccess.saveUser(user);

		if (saved == null) {
			throw new DatabaseException();
		}

		return saved;
	}

	/**
	 * Gets the user associated with the given verification token.
	 * 
	 * @param verificationToken token for user
	 * @return User loaded
	 */
	@Override
	public User getVerificationUser(String verificationToken) {
		List<User> users = userAccess.selectUsersByVerificationToken(verificationToken);
		if (users.isEmpty()) {
			return null;
		}
		return users.get(0);
	}

	/**
	 * Creates a new PasswordResetToken and saves it in the database.
	 * 
	 * @param user User Object
	 * @param token Token String
	 */
	@Override
	public void createPasswordResetToken(User user, String token) {
		PasswordResetToken prt = new PasswordResetToken(token, user);
		userAccess.insertPasswordResetToken(prt);
	}

	/**
	 * Gets the PasswordResetToken from the database.
	 * 
	 * @param token Token String
	 * @return Loaded Token
	 */
	@Override
	public AbstractToken getPasswordResetToken(String token) {
		List<PasswordResetToken> resetTokens = userAccess.selectPasswordResetTokenByToken(token);
		if (resetTokens.isEmpty()) {
			return null;
		}
		return resetTokens.get(0);
	}

	/**
	 * Deletes the given verification token.
	 * 
	 * @param verificationToken token to be deleted
	 */
	@Override
	public void deleteVerificationToken(AbstractToken verificationToken) {
		if (verificationToken instanceof VerificationToken) {
			userAccess.deleteVerificationToken((VerificationToken) verificationToken);
		}
	}

	/**
	 * Changes the password of the given user to the given one.
	 * 
	 * @param user User to be changed
	 * @param password new Password string
	 */
	@Override
	public void changePassword(User user, String password) {
		user.setPassword(passwordEncoder.encode(password));
		userAccess.saveUser(user);

	}

	/**
	 * Deletes the given user.
	 * 
	 * @param user User to be deleted
	 */
	@Override
	public void deleteUser(User user) {
		List<VerificationToken> tokens = getVerificationTokens();
		for (VerificationToken token : tokens) {
			if (token.getUser().getId().equals(user.getId())) {
				deleteVerificationToken(token);
			}
		}
		userAccess.deleteUser(user);

	}

	/**
	 * Deletes the given password reset token.
	 * 
	 * @param resetToken token to be deleted
	 */
	@Override
	public void deletePasswordResetToken(AbstractToken resetToken) {
		if (resetToken instanceof PasswordResetToken) {
			userAccess.deleteVerificationToken((PasswordResetToken) resetToken);
		}
	}

	/**
	 * Gets a List of all verification tokens.
	 * 
	 * @return List of tokens
	 */
	@Override
	public List<VerificationToken> getVerificationTokens() {

		return userAccess.selectVerificationTokens();
	}

	/**
	 * Gets a List of all password reset tokens.
	 * 
	 * @return List of tokens
	 */
	@Override
	public List<PasswordResetToken> getPasswordResetTokens() {
		return userAccess.selectPasswordResetTokens();

	}

	/**
	 * Resets the daily upload limit for all users.
	 */
	@Override
	public void resetDailyUploadLimit() {
		List<User> users = userAccess.selectUsers();

		for (User user : users) {
			user.setDailyUploadVolume((long) 0);
			userAccess.saveUser(user);
		}

	}

}
