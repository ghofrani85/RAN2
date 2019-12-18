package com.ghofrani.htw.RAN2.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ghofrani.htw.RAN2.database.helper.AssetType;
import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.AudioFile;
import com.ghofrani.htw.RAN2.model.AudioAsset;
import com.ghofrani.htw.RAN2.model.Folder;
import com.ghofrani.htw.RAN2.model.FolderFile;
import com.ghofrani.htw.RAN2.model.OtherFile;
import com.ghofrani.htw.RAN2.model.OtherAsset;
import com.ghofrani.htw.RAN2.model.PictureFile;
import com.ghofrani.htw.RAN2.model.PictureAsset;
import com.ghofrani.htw.RAN2.model.TextFile;
import com.ghofrani.htw.RAN2.model.TextAsset;
import com.ghofrani.htw.RAN2.model.VideoFile;
import com.ghofrani.htw.RAN2.model.VideoAsset;
import com.ghofrani.htw.RAN2.model.XMLFile;
import com.ghofrani.htw.RAN2.model.XMLAsset;

/**
 * RowMapper for the file model.
 * 
 * @author Tobias Powelske
 * @author Jannik Gröger
 *
 */
public class FileRowMapper implements RowMapper<File> {

	@Override
	public File mapRow(ResultSet rs, int rowNum) throws SQLException {
		AssetType type = AssetType.values()[rs.getInt("astype")];
		File art = null;

		switch (type) {
		case AUDIO:
			art = new AudioFile(rs.getInt("artid"), rs.getString("title"),
					new AudioAsset(rs.getInt("assetid"), null, null, null), rs.getString("start"),
					rs.getString("endmark"));
			break;
		case PICTURE:
			art = new PictureFile(rs.getInt("artid"), rs.getString("title"),
					new PictureAsset(rs.getInt("assetid"), null, null, null), rs.getString("start"),
					rs.getString("endmark"), rs.getString("picturewidth"), rs.getString("pictureheight"));
			break;
		case TEXT:
			art = new TextFile(rs.getInt("artid"), rs.getString("title"),
					new TextAsset(rs.getInt("assetid"), null, null, null), rs.getString("start"),
					rs.getString("endmark"));
			break;
		case VIDEO:
			art = new VideoFile(rs.getInt("artid"), rs.getString("title"),
					new VideoAsset(rs.getInt("assetid"), null, null, null), rs.getString("start"),
					rs.getString("endmark"));
			break;
		case XML:
			art = new XMLFile(rs.getInt("artid"), rs.getString("title"),
					new XMLAsset(rs.getInt("assetid"), null, null, null), rs.getString("start"));
			break;
		case OTHER:
			art = new OtherFile(rs.getInt("artid"), rs.getString("title"),
					new OtherAsset(rs.getInt("assetid"), null, null, null), rs.getString("start"),
					rs.getString("endmark"));
			break;
		default:
			return null;
		}

		art.setFolder(new Folder(rs.getInt("folderid"), null, null, null));

		return art;
	}

}
