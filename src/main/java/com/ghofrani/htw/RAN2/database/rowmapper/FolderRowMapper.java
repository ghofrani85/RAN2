package com.ghofrani.htw.RAN2.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ghofrani.htw.RAN2.model.Folder;

/**
 * RowMapper for the folder model.
 * 
 * @author Tobias Powelske
 *
 */
public class FolderRowMapper implements RowMapper<Folder> {

	@Override
	public Folder mapRow(ResultSet rs, int rowNum) throws SQLException {
		Folder folder = new Folder();
		int pid = rs.getInt("parentid");

		folder.setId(rs.getInt("id"));
		folder.setTitle(rs.getString("title"));
		folder.setDescription(rs.getString("description"));
		folder.setUpdatedparent(rs.getBoolean("updatedparent"));

		if (pid != 0) {
			folder.setParent(new Folder(pid, null, null, null));
		}

		return folder;
	}

}
