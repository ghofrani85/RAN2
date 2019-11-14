package com.ghofrani.htw.RAN2.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ghofrani.htw.RAN2.database.helper.AssetType;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.AudioAsset;
import com.ghofrani.htw.RAN2.model.OtherAsset;
import com.ghofrani.htw.RAN2.model.PictureAsset;
import com.ghofrani.htw.RAN2.model.TextAsset;
import com.ghofrani.htw.RAN2.model.VideoAsset;
import com.ghofrani.htw.RAN2.model.XMLAsset;

/**
 * RowMapper for the asset model.
 * 
 * @author Tobias Powelske
 *
 */
public class AssetRowMapper implements RowMapper<Asset> {

	@Override
	public Asset mapRow(ResultSet rs, int rowNum) throws SQLException {
		Asset asset = null;
		AssetType type = AssetType.values()[rs.getInt("astype")];

		switch (type) {
		case AUDIO:
			asset = new AudioAsset(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
					rs.getString("url"));
			break;
		case PICTURE:
			asset = new PictureAsset(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
					rs.getString("url"));
			break;
		case TEXT:
			asset = new TextAsset(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
					rs.getString("url"));
			break;
		case VIDEO:
			asset = new VideoAsset(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
					rs.getString("url"));
			break;
		case XML:
			asset = new XMLAsset(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
					rs.getString("url"));
			break;
		case OTHER:
			asset = new OtherAsset(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
					rs.getString("url"));
			break;
		
		default:
			return null;
		}

		asset.setMetadataWithJSONString(rs.getString("metadata"));

		return asset;
	}

}
