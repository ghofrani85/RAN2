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
import com.ghofrani.htw.RAN2.database.helper.TrackingType;
import com.ghofrani.htw.RAN2.database.rowmapper.FolderRowMapper;
import com.ghofrani.htw.RAN2.model.Folder;

/**
 * Class to handle folder interaction with the database.
 * 
 * @author Tobias Powelske
 *
 */
@Component
public class FolderAccess {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private static final String INSERT_SQL = "INSERT INTO folders(title, description, parentid, updatedparent) VALUES(?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE folders SET title = ?, description = ?, parentid = ?, "
			+ "updatedparent = ? WHERE id = ?";
	private static final String UPDATE_PARENT_SQL = "UPDATE folders SET parentid = ?, updatedparent = ? WHERE parentid = ?";
	private static final String DELETE_SQL = "DELETE FROM folders WHERE id = ?";
	private static final String SELECT_SQL = "SELECT folders.id, folders.title, folders.description, folders.parentid, "
			+ "folders.updatedparent FROM folders";
	private static final String SELECT_DISTINCT_SQL = "SELECT DISTINCT folders.id, folders.title, "
			+ "folders.description, folders.parentid, folders.updatedparent FROM folders";
	private static final String DELETE_PROJECTSXFOLDERS_SQL = "DELETE FROM projectsxfolders WHERE folderid = ?";
	private static final String DELETE_PRODUCTSXFOLDERS_SQL = "DELETE FROM productsxfolders WHERE folderid = ?";
	private static final String INSERT_FOLDERSXFOLDERFILES_SQL = "INSERT INTO foldersxfolderfiles(folderid, folderfileid) VALUES(?, ?)";
	private static final String DELETE_FOLDERSXFOLDERFILES_FOLDER_SQL = "DELETE FROM foldersxfolderfiles WHERE folderid = ?";
	private static final String DELETE_FOLDERSXFOLDERFILES_FOLDERARTEFACT_SQL = "DELETE FROM foldersxfolderfiles WHERE folderfileid = ?";

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private TrackingAccess trackacc;

	@Autowired
	private FileAccess artacc;

	/**
	 * Prevent instances of the class.
	 */
	private FolderAccess() {
	}

	/**
	 * Returns a list of all folders.
	 * 
	 * @return list of all folders
	 */
	public List<Folder> selectFolders() {
		LinkedList<Folder> result = new LinkedList<>();

		log.debug("Getting folders...");

		jdbc.query(SELECT_SQL, new FolderRowMapper()).forEach(folder -> result.add(folder));

		// load changed parent...
		result.forEach(feat -> {
			if (feat.getParent() != null) {
				feat.setParent(selectFoldersByID(feat.getParent().getId()));
			}
		});

		// load files...
		result.forEach(feat -> feat.setFileList(artacc.selectFilesByFolderID(feat.getId())));

		// load tracking...
		result.forEach(
				feat -> feat.setTrackingList(trackacc.selectTrackingByItemID(feat.getId(), TrackingType.FOLDER)));

		return result;
	}

	/**
	 * Returns the folder with the given id.
	 * 
	 * @param id
	 *            id of the folder to be retrieved
	 * @return the retrieved folder
	 */
	public Folder selectFoldersByID(int id) {
		Folder result = null;

		log.debug("Getting folder with ID {}", id);

		List<Folder> resultlist = jdbc.query(SELECT_SQL + " WHERE id = ?", new Object[] { id },
				new FolderRowMapper());

		if (!resultlist.isEmpty()) {
			result = resultlist.get(0);

			// load changed parent...
			if (result.getParent() != null) {
				result.setParent(selectFoldersByID(result.getParent().getId()));
			}
		}

		if (result != null) {
			// load files...
			result.setFileList(artacc.selectFilesByFolderID(result.getId(), false));

			// load tracking...
			result.setTrackingList(trackacc.selectTrackingByItemID(result.getId(), TrackingType.FOLDER));
			
			// load folderFiles...
			result.setFolderFileList(selectFolderFilesByFolderID(result.getId()));

		}

		return result;
	}

	/**
	 * Returns a list of folders for the given project id.
	 * 
	 * @param id
	 *            the id of the project to return the folders for
	 * @return a list of found folders
	 */
	public List<Folder> selectFoldersByProjectID(int id) {
		LinkedList<Folder> result = new LinkedList<>();

		log.debug("Getting folders for project {}...", id);

		jdbc.query(SELECT_SQL + " JOIN projectsxfolders on folderid = folders.id WHERE projectid = ?",
				new Object[] { id }, new FolderRowMapper()).forEach(folder -> result.add(folder));

		// load changed parent...
		result.forEach(feat -> {
			if (feat.getParent() != null) {
				feat.setParent(selectFoldersByID(feat.getParent().getId()));
			}
		});

		// load files...
		result.forEach(feat -> feat.setFileList(artacc.selectFilesByFolderID(feat.getId())));

		// load tracking...
		result.forEach(
				feat -> feat.setTrackingList(trackacc.selectTrackingByItemID(feat.getId(), TrackingType.FOLDER)));

		// load folderFiles...
		result.forEach(
				feat -> feat.setFolderFileList(selectFolderFilesByFolderID(feat.getId())));

		
		return result;
	}

	/**
	 * Returns a list of folders for the given asset id.
	 * 
	 * @param id
	 *            the id of the asset to return the folders for
	 * @return a list of found folders
	 */
	public List<Folder> selectFoldersByAssetID(int id) {
		List<Folder> result;

		log.debug("Getting folders for asset {}...", id);

		result = jdbc.query(SELECT_DISTINCT_SQL + " JOIN files on folderid = folders.id WHERE assetid = ?",
				new Object[] { id }, new FolderRowMapper());

		// load changed parent...
		result.forEach(feat -> {
			if (feat.getParent() != null) {
				feat.setParent(selectFoldersByID(feat.getParent().getId()));
			}
		});

		// load files...
		result.forEach(feat -> feat.setFileList(artacc.selectFilesByFolderID(feat.getId())));

		// load tracking...
		result.forEach(
				feat -> feat.setTrackingList(trackacc.selectTrackingByItemID(feat.getId(), TrackingType.FOLDER)));

		// load folderFiles...
		result.forEach(
				feat -> feat.setFolderFileList(selectFolderFilesByFolderID(feat.getId())));
		
		return result;
	}
	
	public List<Folder> selectFolderFilesByFolderID(int id) {
		List<Folder> result = new LinkedList<>();
		
		log.debug("Getting folderFiles list fot folder {}...", id);
		
		jdbc.query(SELECT_SQL + " JOIN foldersxfolderfiles on folderfileid = folders.id WHERE folderid = ?",
		new Object[] { id }, new FolderRowMapper()).forEach(folderfile -> result.add(folderfile));

//		// load changed parent...
//		result.forEach(feat -> {
//			if (feat.getParent() != null) {
//				feat.setParent(selectFoldersByID(feat.getParent().getId()));
//			}
//		});
		
		// load files...
		result.forEach(feat -> feat.setFileList(artacc.selectFilesByFolderID(feat.getId())));
		
		// load tracking...
		result.forEach(
				feat -> feat.setTrackingList(trackacc.selectTrackingByItemID(feat.getId(), TrackingType.FOLDER)));
		
		// load folderFiles...
		result.forEach(
				feat -> {
					if (feat.getFolderFileList() != null && feat.getFolderFileList().isEmpty() == false) {
						feat.setFolderFileList(selectFolderFilesByFolderID(feat.getId()));
					}
				});
		
		return result;
	}
	
	public List<Folder> selectFolderByFolderFileID(int id) {
		List<Folder> result = new LinkedList<>();
		
		log.debug("Getting folder for the folderFile {}", id);
		
		jdbc.query(SELECT_DISTINCT_SQL + " JOIN foldersxfolderfiles on folderid = folders.id WHERE folderfileid = ?",
		new Object[] { id }, new FolderRowMapper()).forEach(folder -> result.add(folder));
		
		// load files...
		result.forEach(
				feat -> feat.setFileList(artacc.selectFilesByFolderID(feat.getId())));
		
		// load tracking...
		result.forEach(
				feat -> feat.setTrackingList(trackacc.selectTrackingByItemID(feat.getId(), TrackingType.FOLDER)));
		
		// load folderFiles...
		result.forEach(
				feat -> {
					if (feat.getFolderFileList() != null && feat.getFolderFileList().isEmpty() == false) {
						feat.setFolderFileList(selectFolderFilesByFolderID(feat.getId()));
					}
				});
		
		return result;
	}
	
	public void selectFoldersXFolderFiles (Folder folder) {
		log.debug("Getting folderFiles for the folder {}", folder.getId());
		
		jdbc.query(
				"SELECT folders.id, title, description, parentid, updatedparent "
				+ "FROM foldersxfolderfiles JOIN folders ON folderfileid = folders.id WHERE folderid = ?",
		new Object[] { folder.getId() }, new FolderRowMapper())
		.forEach(folderFile -> folder.addFolderFile(folderFile));

	}

	/**
	 * Inserts a new folder, if id is null or updates an existing one.
	 * 
	 * @param folder
	 *            the folder to be inserted or updated
	 * @return the updated folder model
	 */
	public Folder saveFolder(Folder folder) {
		Folder result;

		if (folder.getId() == null) {
			result = insertFolder(folder);

			final int id = result.getId();
			result.getTrackingList().forEach(tra -> tra.setItemid(id));
			result.getFileList().forEach(art -> art.getFolder().setId(id));
		} else {
			result = updateFolder(folder);
		}

		// save tracking...
		trackacc.saveTracking(result.getTrackingList());

		// save files...
		result.getFileList().forEach(art -> artacc.saveFile(art));
		
		// save folderFiles for folder...
		saveFoldersXFolderFiles(result);
		
		// save folderFiles
		if (result.getFolderFileList().isEmpty() == false) {
			result.getFolderFileList().forEach(featart -> saveFolder(featart));
		}

		return result;
	}
	
	/**
	 * Inserts the folders in the given folder folderFileList into the database.
	 * 
	 * @param folder
	 * 
	 * @author Hongfei Chen
	 */
	public void saveFoldersXFolderFiles(Folder folder) {
		jdbc.update(DELETE_FOLDERSXFOLDERFILES_FOLDER_SQL, folder.getId());
		
		folder.getFolderFileList()
				.forEach(featart -> jdbc.update(INSERT_FOLDERSXFOLDERFILES_SQL, folder.getId(), featart.getId()));
	}

	/**
	 * Inserts a new folder.
	 * 
	 * @param folder
	 *            the folder to be inserted
	 * @return the updated folder model
	 */
	private Folder insertFolder(Folder folder) {
		KeyHolder holder = new GeneratedKeyHolder();

		jdbc.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, folder.getTitle());
				ps.setString(2, folder.getDescription());
				if (folder.getParent() != null) {
					ps.setInt(3, folder.getParent().getId());
				} else {
					ps.setNull(3, java.sql.Types.INTEGER);
				}
				ps.setBoolean(4, folder.isUpdatedparent());
				return ps;
			}
		}, holder);

		int newId;
		if (holder.getKeys().size() > 1) {
			newId = (int) holder.getKeys().get("id");
		} else {
			newId = holder.getKey().intValue();
		}
		folder.setId(newId);

		return folder;
	}

	/**
	 * Updates the given folder.
	 * 
	 * @param folder
	 *            the folder to be updated
	 * @return the updated folder model
	 */
	private Folder updateFolder(Folder folder) {
		Integer parentid = null;

		if (folder.getParent() != null) {
			parentid = folder.getParent().getId();
		}

		jdbc.update(UPDATE_SQL, folder.getTitle(), folder.getDescription(), parentid, folder.isUpdatedparent(),
				folder.getId());

		return folder;
	}
	
	

	/**
	 * Deletes the given folder.
	 * 
	 * @param folder
	 *            the folder to be deleted
	 * @return true, if successful, false, if not
	 */
	public boolean deleteFolder(Folder folder) {
		int resultcount = 0;

		// delete tracking...
		trackacc.deleteTracking(folder.getId(), TrackingType.FOLDER);

		// delete connections...
		jdbc.update(DELETE_PRODUCTSXFOLDERS_SQL, folder.getId());
		jdbc.update(DELETE_PROJECTSXFOLDERS_SQL, folder.getId());
		jdbc.update(DELETE_FOLDERSXFOLDERFILES_FOLDER_SQL, folder.getId());
		jdbc.update(DELETE_FOLDERSXFOLDERFILES_FOLDERARTEFACT_SQL, folder.getId());

		// delete files...
		artacc.deleteFilesByFolderID(folder.getId());
		
		// delete folderFiles...
		folder.getFolderFileList().forEach(featart -> deleteFolder(featart));
		
		// set childrens id to null...
		jdbc.update(UPDATE_PARENT_SQL, null, false, folder.getId());

		resultcount = jdbc.update(DELETE_SQL, folder.getId());

		return resultcount >= 1;
	}
}
