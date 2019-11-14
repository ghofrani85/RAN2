package com.ghofrani.htw.RAN2.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ghofrani.htw.RAN2.model.User;

/**
 * RowMapper for the user model.
 * 
 * @author Tobias Powelske
 *
 */
public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();

		user.setId(rs.getInt("id"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		user.setEnabled(rs.getBoolean("enabled"));
		user.setEmail(rs.getString("email"));
		user.setLocked(rs.getBoolean("locked"));
		user.setRegistrationDate(rs.getDate("registrationdate"));
		user.setTotalDataVolume(rs.getLong("totaldatavolume"));
		user.setDailyUploadVolume(rs.getLong("dailyuploadvolume"));

		return user;
	}

}
