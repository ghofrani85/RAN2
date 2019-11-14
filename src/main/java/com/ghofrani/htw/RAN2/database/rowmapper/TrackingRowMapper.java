package com.ghofrani.htw.RAN2.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ghofrani.htw.RAN2.database.helper.TrackingType;
import com.ghofrani.htw.RAN2.model.Tracking;

/**
 * RowMapper for the tracking model.
 * 
 * @author Tobias Powelske
 *
 */
public class TrackingRowMapper implements RowMapper<Tracking> {

	@Override
	public Tracking mapRow(ResultSet rs, int rowNum) throws SQLException {
		Tracking tra = new Tracking();

		tra.setItemid(rs.getInt("itemid"));
		tra.setType(TrackingType.values()[rs.getInt("trtype")]);
		tra.setChangemade(rs.getDate("changemade"));
		tra.setText(rs.getString("text"));

		return tra;
	}

}
