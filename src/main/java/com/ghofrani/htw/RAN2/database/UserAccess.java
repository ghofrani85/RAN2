package com.ghofrani.htw.RAN2.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.token.PasswordResetToken;
import com.ghofrani.htw.RAN2.controller.token.VerificationToken;
import com.ghofrani.htw.RAN2.database.rowmapper.PasswordResetTokenRowMapper;
import com.ghofrani.htw.RAN2.database.rowmapper.UserRowMapper;
import com.ghofrani.htw.RAN2.database.rowmapper.VerificationTokenRowMapper;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Class to handle user interaction with the database.
 * 
 * @author Tobias Powelske
 *
 */
@Component
public class UserAccess {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private static final String INSERT_SQL = "INSERT INTO users(username, password, enabled, locked, email, "
			+ "registrationdate, totaldatavolume, dailyuploadvolume) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE users SET username = ?, password = ?, enabled = ?, "
			+ "locked = ?, email = ?, registrationdate = ?, totaldatavolume = ?, dailyuploadvolume = ? WHERE id = ?";
	private static final String DELETE_SQL = "DELETE FROM users WHERE id = ?";
	private static final String SELECT_SQL = "SELECT id, username, password, enabled, locked, email, "
			+ "registrationdate, totaldatavolume, dailyuploadvolume FROM users";
	private static final String INSERT_VERIFICATIONTOKEN_SQL = "INSERT INTO verificationtokens(token, userid, expirationdate) VALUES(?, ?, ?)";
	private static final String DELETE_VERIFICATIONTOKEN_SQL = "DELETE FROM verificationtokens WHERE id = ?";
	private static final String INSERT_PASSWORDRESETTOKEN_SQL = "INSERT INTO passwordresettokens(token, userid, expirationdate) VALUES(?, ?, ?)";
	private static final String DELETE_PASSWORDRESETTOKEN_SQL = "DELETE FROM passwordresettokens WHERE id = ?";
	private static final String INSERT_AUTHORITIES_SQL = "INSERT INTO authorities(userid, authority) VALUES(?, ?)";
	private static final String DELETE_AUTHORITIES_SQL = "DELETE FROM authorities WHERE userid = ?";
	private static final String SELECT_VERIFICATIONTOKEN_SQL = "SELECT id, token, userid, expirationdate FROM verificationtokens";
	private static final String SELECT_PASSWORDRESETTOKEN_SQL = "SELECT id, token, userid, expirationdate FROM passwordresettokens";

	@Autowired
	private JdbcTemplate jdbc;

	/**
	 * Prevent instances of the class.
	 */
	private UserAccess() {
	}

	/**
	 * Returns a list of all users.
	 * 
	 * @return list of all users
	 */
	public List<User> selectUsers() {
		LinkedList<User> result = new LinkedList<>();

		log.debug("Getting users...");

		jdbc.query(SELECT_SQL, new UserRowMapper()).forEach(user -> result.add(user));

		result.forEach(u -> selectAuthorities(u));

		return result;
	}

	/**
	 * Returns the user with the given id.
	 * 
	 * @param id
	 *            id of the user to be retrieved
	 * @return the retrieved user
	 */
	public User selectUsersByID(int id) {
		User result = null;

		log.debug("Getting user with ID {}", id);

		List<User> resultlist = jdbc.query(SELECT_SQL + " WHERE id = ?", new Object[] { id }, new UserRowMapper());

		resultlist.forEach(u -> selectAuthorities(u));

		if (!resultlist.isEmpty()) {
			result = resultlist.get(0);
		}

		return result;
	}

	/**
	 * Returns a list of users with the given username.
	 * 
	 * @param uname
	 *            username of the users to be retrieved
	 * @return a list of found users
	 */
	public List<User> selectUsersByUsername(String uname) {
		List<User> result = null;

		log.debug("Getting users with username {}", uname);

		result = jdbc.query(SELECT_SQL + " WHERE username = ?", new Object[] { uname }, new UserRowMapper());

		result.forEach(u -> selectAuthorities(u));

		return result;
	}

	/**
	 * Returns a list of users with the given email.
	 * 
	 * @param email
	 *            email of the users to be retrieved
	 * @return a list of found users
	 */
	public List<User> selectUsersByEmail(String email) {
		List<User> result = null;

		log.debug("Getting users with email {}", email);

		result = jdbc.query(SELECT_SQL + " WHERE email = ?", new Object[] { email }, new UserRowMapper());

		result.forEach(u -> selectAuthorities(u));

		return result;
	}

	/**
	 * Returns a list of users with the given role.
	 * 
	 * @param role
	 *            role of the users to be retrieved
	 * @return a list of found users
	 */
	public List<User> selectUsersByRole(String role) {
		List<User> result = null;

		log.debug("Getting users with role {}", role);

		result = jdbc.query(
				"SELECT id, username, password, enabled, locked, email, registrationdate, totaldatavolume, dailyuploadvolume FROM users JOIN authorities ON userid = id WHERE authority = ?",
				new Object[] { role }, new UserRowMapper());

		result.forEach(u -> selectAuthorities(u));

		return result;
	}

	/**
	 * Returns a list of users associated with the given verification token.
	 * 
	 * @param token
	 *            the token to be looked for
	 * @return a list of found users
	 */
	public List<User> selectUsersByVerificationToken(String token) {
		List<User> result = null;

		result = jdbc.query(
				"SELECT id, username, password, enabled, locked, email FROM users JOIN verificationtokens ON userid = id WHERE token = ?",
				new Object[] { token }, new UserRowMapper());

		return result;
	}

	/**
	 * Returns a list of users associated with the given password reset token.
	 * 
	 * @param token
	 *            the token to be looked for
	 * @return a list of found users
	 */
	public List<User> selectUsersByPasswordResetToken(String token) {
		List<User> result = null;

		result = jdbc.query(
				"SELECT id, username, password, enabled, locked, email FROM users JOIN passwordresettokens ON userid = id WHERE token = ?",
				new Object[] { token }, new UserRowMapper());

		return result;
	}

	/**
	 * Fills the authority list of the given user.
	 * 
	 * @param user
	 *            the user to fill the authority list for
	 */
	private void selectAuthorities(User user) {
		log.debug("Getting authorities for user {}", user.getId());

		jdbc.query("SELECT authority FROM authorities WHERE userid = ?", new Object[] { user.getId() },
				(rs, rowNum) -> rs.getString("authority")).forEach(aut -> user.addRole(aut));
	}

	/**
	 * Inserts a new user, if id is null or updates an existing one.
	 * 
	 * @param user
	 *            the user to be inserted or updated
	 * @return the updated user model
	 */
	public User saveUser(User user) {
		if (user.getId() == null) {
			return insertUser(user);
		} else {
			return updateUser(user);
		}
	}

	/**
	 * Inserts a new user.
	 * 
	 * @param user
	 *            the user to be inserted
	 * @return the updated user model
	 */
	public User insertUser(User user) {
		KeyHolder holder = new GeneratedKeyHolder();

		jdbc.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, user.getUsername());
				ps.setString(2, user.getPassword());
				ps.setBoolean(3, user.isEnabled());
				ps.setBoolean(4, user.isLocked());
				ps.setString(5, user.getEmail());
				java.sql.Date sqlDate = new java.sql.Date(user.getRegistrationDate().getTime());
				ps.setDate(6, sqlDate);
				ps.setLong(7, user.getTotalDataVolume());
				ps.setLong(8, user.getDailyUploadVolume());
				return ps;
			}
		}, holder);

		int newId;
		if (holder.getKeys().size() > 1) {
			newId = (int) holder.getKeys().get("id");
		} else {
			newId = holder.getKey().intValue();
		}

		user.setId(newId);

		// save user roles...
		saveAuthorities(user);

		return user;
	}

	/**
	 * Updates the given user.
	 * 
	 * @param user
	 *            the user to be updated
	 * @return the updated user model
	 */
	public User updateUser(User user) {
		jdbc.update(UPDATE_SQL, user.getUsername(), user.getPassword(), user.isEnabled(), user.isLocked(),
				user.getEmail(), user.getRegistrationDate(), user.getTotalDataVolume(), user.getDailyUploadVolume(),
				user.getId());

		// save user roles...
		saveAuthorities(user);

		return user;
	}

	/**
	 * Inserts new roles of the given user
	 * 
	 * @param user
	 *            user whose roles should be inserted
	 */
	private void saveAuthorities(User user) {
		// delete the authorities for the given user...
		jdbc.update(DELETE_AUTHORITIES_SQL, user.getId());

		// re-insert the authorities...
		for (String auth : user.getRoles()) {
			jdbc.update(INSERT_AUTHORITIES_SQL, user.getId(), auth);
		}
	}

	/**
	 * Deletes the given user.
	 * 
	 * @param user
	 *            the user to be deleted
	 * @return true, if successful, false, if not
	 */
	public boolean deleteUser(User user) {
		int resultcount = 0;

		// delete authorities for the user first...
		jdbc.update(DELETE_AUTHORITIES_SQL, user.getId());

		// delete user...
		resultcount = jdbc.update(DELETE_SQL, user.getId());

		if (resultcount >= 1) {
			return true;
		}

		return false;
	}

	// ********************************************************************************************

	/**
	 * Returns a list of all verification tokens.
	 * 
	 * @return a list of verification tokens
	 */
	public List<VerificationToken> selectVerificationTokens() {
		List<VerificationToken> reslist = jdbc.query(SELECT_VERIFICATIONTOKEN_SQL, new VerificationTokenRowMapper());

		// load user for each token...
		reslist.forEach(tok -> tok.setUser(selectUsersByID(tok.getUser().getId())));

		return reslist;
	}

	/**
	 * Selects the verification token by the token string.
	 * 
	 * @param token
	 *            the token string to be looked for
	 * @return a list of found token
	 */
	public List<VerificationToken> selectVerificationTokenByToken(String token) {
		List<VerificationToken> reslist = jdbc.query(SELECT_VERIFICATIONTOKEN_SQL + " WHERE token = ?",
				new Object[] { token }, new VerificationTokenRowMapper());

		// load user for each token...
		reslist.forEach(tok -> tok.setUser(selectUsersByID(tok.getUser().getId())));

		return reslist;
	}

	/**
	 * Inserts a new verification token.
	 * 
	 * @param token
	 *            the token to be inserted
	 * @return the updated token model
	 */
	public VerificationToken insertVerificationToken(VerificationToken token) {
		KeyHolder holder = new GeneratedKeyHolder();

		jdbc.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_VERIFICATIONTOKEN_SQL,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, token.getToken());
				ps.setInt(2, token.getUser().getId());
				ps.setDate(3, token.getExpirationDate());
				return ps;
			}
		}, holder);

		int newId;
		if (holder.getKeys().size() > 1) {
			newId = (int) holder.getKeys().get("id");
		} else {
			newId = holder.getKey().intValue();
		}
		token.setId(newId);

		return token;
	}

	/**
	 * Deletes the given verification token.
	 * 
	 * @param token
	 *            the token to be deleted
	 * @return true, if successful, false, if not
	 */
	public boolean deleteVerificationToken(VerificationToken token) {
		int resultcount = jdbc.update(DELETE_VERIFICATIONTOKEN_SQL, token.getId());

		if (resultcount >= 1) {
			return true;
		}

		return false;
	}

	// ********************************************************************************************

	/**
	 * Returns a list of all password reset tokens.
	 * 
	 * @return a list of password reset tokens
	 */
	public List<PasswordResetToken> selectPasswordResetTokens() {
		List<PasswordResetToken> reslist = jdbc.query(SELECT_PASSWORDRESETTOKEN_SQL, new PasswordResetTokenRowMapper());

		// load user for each token...
		reslist.forEach(tok -> tok.setUser(selectUsersByID(tok.getUser().getId())));

		return reslist;
	}

	/**
	 * Selects the password reset token by the token string.
	 * 
	 * @param token
	 *            the token string to be looked for
	 * @return a list of found token
	 */
	public List<PasswordResetToken> selectPasswordResetTokenByToken(String token) {
		List<PasswordResetToken> reslist = jdbc.query(SELECT_PASSWORDRESETTOKEN_SQL + " WHERE token = ?",
				new Object[] { token }, new PasswordResetTokenRowMapper());

		// load user for each token...
		reslist.forEach(tok -> tok.setUser(selectUsersByID(tok.getUser().getId())));

		return reslist;
	}

	/**
	 * Inserts a new password reset token.
	 * 
	 * @param token
	 *            the token to be inserted
	 * @return the updated token model
	 */
	public PasswordResetToken insertPasswordResetToken(PasswordResetToken token) {
		KeyHolder holder = new GeneratedKeyHolder();

		jdbc.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_PASSWORDRESETTOKEN_SQL,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, token.getToken());
				ps.setInt(2, token.getUser().getId());
				ps.setDate(3, token.getExpirationDate());
				return ps;
			}
		}, holder);

		int newId;
		if (holder.getKeys().size() > 1) {
			newId = (int) holder.getKeys().get("id");
		} else {
			newId = holder.getKey().intValue();
		}
		token.setId(newId);

		return token;
	}

	/**
	 * Deletes the given password reset token.
	 * 
	 * @param token
	 *            the token to be deleted
	 * @return true, if successful, false, if not
	 */
	public boolean deleteVerificationToken(PasswordResetToken token) {
		int resultcount = jdbc.update(DELETE_PASSWORDRESETTOKEN_SQL, token.getId());

		if (resultcount >= 1) {
			return true;
		}

		return false;
	}

	// ********************************************************************************************
}
