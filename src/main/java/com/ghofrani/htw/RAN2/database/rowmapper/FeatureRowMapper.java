package com.ghofrani.htw.RAN2.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ghofrani.htw.RAN2.model.Feature;

/**
 * RowMapper for the feature model.
 * 
 * @author Tobias Powelske
 *
 */
public class FeatureRowMapper implements RowMapper<Feature> {

	@Override
	public Feature mapRow(ResultSet rs, int rowNum) throws SQLException {
		Feature feature = new Feature();
		int pid = rs.getInt("parentid");

		feature.setId(rs.getInt("id"));
		feature.setTitle(rs.getString("title"));
		feature.setDescription(rs.getString("description"));
		feature.setUpdatedparent(rs.getBoolean("updatedparent"));

		if (pid != 0) {
			feature.setParent(new Feature(pid, null, null, null));
		}

		return feature;
	}

}
