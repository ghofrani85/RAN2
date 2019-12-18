package com.ghofrani.htw.RAN2.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.database.helper.TrackingType;
import com.ghofrani.htw.RAN2.database.rowmapper.FileRowMapper;
import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.AudioFile;
import com.ghofrani.htw.RAN2.model.PictureFile;
import com.ghofrani.htw.RAN2.model.TextFile;
import com.ghofrani.htw.RAN2.model.VideoFile;
import com.ghofrani.htw.RAN2.model.XMLFile;

/**
 * Class to handle file interaction with the database.
 * 
 * @author Tobias Powelske
 *
 */
@Component
public class FileAccess {
	private static final String SELECT_SQL = "SELECT files.id artid, files.title, start, endmark, picturewidth, pictureheight, assetid, astype, folderid"
			+ " FROM files JOIN assets on assetid = assets.id";
	private static final String INSERT_SQL = "INSERT INTO files(title, start, endmark, assetid, folderid) VALUES(?, ?, ?, ?, ?)";
	private static final String INSERT_PICTUREFILE_SQL = "INSERT INTO files(title, start, endmark, picturewidth, pictureheight, assetid, folderid) VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE files SET title = ?, start = ?, endmark = ?, assetid = ?, folderid = ? WHERE id = ?";
	private static final String UPDATE_PICTUREFILE_SQL = "UPDATE files SET title = ?, start = ?, endmark = ?, picturewidth = ?, pictureheight = ?, assetid = ?, folderid = ? WHERE id = ?";
	private static final String DELETE_SQL = "DELETE FROM files WHERE id = ?";
	private static final String DELETE_BYFOLDERID_SQL = "DELETE FROM files WHERE folderid = ?";

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private AssetAccess assacc;

	@Autowired
	private FolderAccess featacc;

	@Autowired
	private TrackingAccess trackacc;

	/**
	 * Prevent instances of this class.
	 */
	private FileAccess() {
	}

	/**
	 * Gets a list of files from the database.
	 * 
	 * @return a list of files
	 */
	public List<File> selectFiles() {
		List<File> result = null;

		result = jdbc.query(SELECT_SQL, new FileRowMapper());

		// load assets for each file...
		result.forEach(art -> art.setAsset(assacc.selectAssetsByID(art.getAsset().getId())));

		// load folders for each file...
		result.forEach(feat -> feat.setFolder(featacc.selectFoldersByID(feat.getFolder().getId())));

		// load tracking...
		result.forEach(art -> art.setTrackingList(trackacc.selectTrackingByItemID(art.getId(), TrackingType.FILE)));

		return result;
	}

	/**
	 * Gets an file from the database by its id.
	 * 
	 * @param id
	 *            the id to look for
	 * @return the file with the given id
	 */
	public File selectFilesByID(int id) {
		List<File> reslist = null;
		File result = null;

		reslist = jdbc.query(SELECT_SQL + " WHERE files.id = ?", new Object[] { id }, new FileRowMapper());

		if (!reslist.isEmpty()) {
			result = reslist.get(0);
		}
		if (result != null) {
			// load asset...
			result.setAsset(assacc.selectAssetsByID(result.getAsset().getId()));

			// load folder...
			result.setFolder(featacc.selectFoldersByID(result.getFolder().getId()));

			// load tracking...
			result.setTrackingList(trackacc.selectTrackingByItemID(result.getId(), TrackingType.FILE));
		}
		return result;
	}

	/**
	 * Returns a list of files by the associated folder id.
	 * 
	 * @param id
	 *            the folder id to be looked for
	 * @return a list of found files
	 */
	public List<File> selectFilesByFolderID(int id) {
		List<File> reslist = null;

		reslist = jdbc.query(SELECT_SQL + " WHERE folderid = ?", new Object[] { id }, new FileRowMapper());

		reslist.forEach(art -> {
			// load asset...
			art.setAsset(assacc.selectAssetsByID(art.getAsset().getId()));

			// load folder...
			art.setFolder(featacc.selectFoldersByID(art.getFolder().getId()));

			// load tracking...
			art.setTrackingList(trackacc.selectTrackingByItemID(art.getId(), TrackingType.FILE));
		});

		return reslist;
	}

	/**
	 * Returns a list of files by the associated folder id.
	 * 
	 * @param id
	 *            the folder id to be looked for
	 * @param loadfolder
	 *            if folder should be loaded with the file
	 * @return a list of found files
	 */
	public List<File> selectFilesByFolderID(int id, boolean loadfolder) {
		List<File> reslist = null;

		reslist = jdbc.query(SELECT_SQL + " WHERE folderid = ?", new Object[] { id }, new FileRowMapper());

		reslist.forEach(art -> {
			// load asset...
			art.setAsset(assacc.selectAssetsByID(art.getAsset().getId()));

			// load folder...
			if (loadfolder) {
				art.setFolder(featacc.selectFoldersByID(art.getFolder().getId()));
			}

			// load tracking...
			art.setTrackingList(trackacc.selectTrackingByItemID(art.getId(), TrackingType.FILE));
		});

		return reslist;
	}

	/**
	 * Inserts a new file, if id is null or updates an existing one.
	 * 
	 * @param file
	 *            the file to be inserted or updated
	 * @return the updated file model
	 */
	public File saveFile(File file) {
		File art;

		if (file.getId() == null) {
			art = insertFile(file);

			final int id = art.getId();
			art.getTrackingList().forEach(tra -> tra.setItemid(id));
		} else {
			art = updateFile(file);
		}

		// save tracking...
		trackacc.saveTracking(art.getTrackingList());

		return art;
	}

	/**
	 * Inserts a new file.
	 * 
	 * @param file
	 *            the file to be inserted
	 * @return the updated file model
	 */
	private File insertFile(File file) {
		KeyHolder holder = new GeneratedKeyHolder();

		switch (file.getAsset().getType()) {
		case AUDIO:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, file.getTitle());
					ps.setString(2, ((AudioFile) file).getStart());
					ps.setString(3, ((AudioFile) file).getEnd());
					ps.setInt(4, file.getAsset().getId());
					ps.setInt(5, file.getFolder().getId());
					return ps;
				}
			}, holder);
			break;

		case TEXT:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, file.getTitle());
					ps.setString(2, ((TextFile) file).getStart());
					ps.setString(3, ((TextFile) file).getEnd());
					ps.setInt(4, file.getAsset().getId());
					ps.setInt(5, file.getFolder().getId());
					return ps;
				}
			}, holder);
			break;

		case VIDEO:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, file.getTitle());
					ps.setString(2, ((VideoFile) file).getStart());
					ps.setString(3, ((VideoFile) file).getEnd());
					ps.setInt(4, file.getAsset().getId());
					ps.setInt(5, file.getFolder().getId());
					return ps;
				}
			}, holder);
			break;

		case XML:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, file.getTitle());
					ps.setString(2, ((XMLFile) file).getNodes());
					ps.setString(3, null);
					ps.setInt(4, file.getAsset().getId());
					ps.setInt(5, file.getFolder().getId());
					return ps;
				}
			}, holder);
			break;

		case PICTURE:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_PICTUREFILE_SQL,
							Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, file.getTitle());
					ps.setString(2, ((PictureFile) file).getX());
					ps.setString(3, ((PictureFile) file).getY());
					ps.setString(4, ((PictureFile) file).getWidth());
					ps.setString(5, ((PictureFile) file).getHeight());
					ps.setInt(6, file.getAsset().getId());
					ps.setInt(7, file.getFolder().getId());
					return ps;
				}
			}, holder);
			break;

		case OTHER:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, file.getTitle());
					ps.setNull(2, 0);
					ps.setNull(3, 0);
					ps.setInt(4, file.getAsset().getId());
					ps.setInt(5, file.getFolder().getId());
					return ps;
				}
			}, holder);
			break;

		default:
			break;
		}

		int newId;
		if (holder.getKeys().size() > 1) {
			newId = (int) holder.getKeys().get("id");
		} else {
			newId = holder.getKey().intValue();
		}
		file.setId(newId);

		return file;
	}

	/**
	 * Updates the given file.
	 * 
	 * @param folder
	 *            the file to be updated
	 * @return the updated file model
	 */
	private File updateFile(File file) {
		switch (file.getAsset().getType()) {
		case AUDIO:
			jdbc.update(UPDATE_SQL, file.getTitle(), ((AudioFile) file).getStart(),
					((AudioFile) file).getEnd(), file.getAsset().getId(), file.getFolder().getId(),
					file.getId());
			break;
		case PICTURE:
			jdbc.update(UPDATE_PICTUREFILE_SQL, file.getTitle(), ((PictureFile) file).getX(),
					((PictureFile) file).getY(), ((PictureFile) file).getWidth(),
					((PictureFile) file).getHeight(), file.getAsset().getId(),
					file.getFolder().getId(), file.getId());
			break;
		case TEXT:
			jdbc.update(UPDATE_SQL, file.getTitle(), ((TextFile) file).getStart(),
					((TextFile) file).getEnd(), file.getAsset().getId(), file.getFolder().getId(),
					file.getId());
			break;
		case VIDEO:
			jdbc.update(UPDATE_SQL, file.getTitle(), ((VideoFile) file).getStart(),
					((VideoFile) file).getEnd(), file.getAsset().getId(), file.getFolder().getId(),
					file.getId());
			break;
		case XML:
			jdbc.update(UPDATE_SQL, file.getTitle(), ((XMLFile) file).getNodes(), null,
					file.getAsset().getId(), file.getFolder().getId(), file.getId());
			break;
		case OTHER:
			jdbc.update(UPDATE_SQL, file.getTitle(), null, null, file.getAsset().getId(),
					file.getFolder().getId(), file.getId());
			break;
		default:
			break;
		}

		return file;
	}

	/**
	 * Deletes the given file.
	 * 
	 * @param file
	 *            the file to be deleted
	 * @return true, if successful, false, if not
	 */
	public boolean deleteFile(File file) {
		int resultcount = 0;

		// delete tracking...
		trackacc.deleteTracking(file.getId(), TrackingType.FILE);

		resultcount = jdbc.update(DELETE_SQL, file.getId());

		return resultcount >= 1;
	}

	/**
	 * Deletes all files associated with the given folder.
	 * 
	 * @param id
	 *            the folder id to delete the files by
	 * @return true, if successful, false, if not
	 */
	public boolean deleteFilesByFolderID(int id) {
		int resultcount = 0;
		List<File> templist = null;

		templist = jdbc.query(SELECT_SQL + " WHERE folderid = ?", new Object[] { id }, new FileRowMapper());

		templist.forEach(art ->
		// delete tracking...
		trackacc.deleteTracking(art.getId(), TrackingType.FILE));

		resultcount = jdbc.update(DELETE_BYFOLDERID_SQL, id);

		return resultcount >= 1;
	}
}
