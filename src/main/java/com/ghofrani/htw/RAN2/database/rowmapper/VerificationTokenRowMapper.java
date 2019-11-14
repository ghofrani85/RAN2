package com.ghofrani.htw.RAN2.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ghofrani.htw.RAN2.controller.token.VerificationToken;
import com.ghofrani.htw.RAN2.model.User;

/**
 * RowMapper for the verification tokens.
 * 
 * @author Tobias Powelske
 *
 */
public class VerificationTokenRowMapper implements RowMapper<VerificationToken> {

	@Override
	public VerificationToken mapRow(ResultSet rs, int rowNum) throws SQLException {
		VerificationToken vtoken = new VerificationToken();

		vtoken.setId(rs.getInt("id"));
		vtoken.setToken(rs.getString("token"));
		vtoken.setExpirationDate(rs.getDate("expirationdate"));
		vtoken.setUser(new User(rs.getInt("userid"), null, null, null, false, false, null, null, null, null));

		return vtoken;
	}

}
