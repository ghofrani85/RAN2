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
import com.ghofrani.htw.RAN2.database.rowmapper.ArtefactRowMapper;
import com.ghofrani.htw.RAN2.model.Artefact;
import com.ghofrani.htw.RAN2.model.AudioArtefact;
import com.ghofrani.htw.RAN2.model.PictureArtefact;
import com.ghofrani.htw.RAN2.model.TextArtefact;
import com.ghofrani.htw.RAN2.model.VideoArtefact;
import com.ghofrani.htw.RAN2.model.XMLArtefact;

/**
 * Class to handle artefact interaction with the database.
 * 
 * @author Tobias Powelske
 *
 */
@Component
public class ArtefactAccess {
	private static final String SELECT_SQL = "SELECT artefacts.id artid, artefacts.title, start, endmark, picturewidth, pictureheight, assetid, astype, featureid"
			+ " FROM artefacts JOIN assets on assetid = assets.id";
	private static final String INSERT_SQL = "INSERT INTO artefacts(title, start, endmark, assetid, featureid) VALUES(?, ?, ?, ?, ?)";
	private static final String INSERT_PICTUREARTEFACT_SQL = "INSERT INTO artefacts(title, start, endmark, picturewidth, pictureheight, assetid, featureid) VALUES(?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE artefacts SET title = ?, start = ?, endmark = ?, assetid = ?, featureid = ? WHERE id = ?";
	private static final String UPDATE_PICTUREARTEFACT_SQL = "UPDATE artefacts SET title = ?, start = ?, endmark = ?, picturewidth = ?, pictureheight = ?, assetid = ?, featureid = ? WHERE id = ?";
	private static final String DELETE_SQL = "DELETE FROM artefacts WHERE id = ?";
	private static final String DELETE_BYFEATUREID_SQL = "DELETE FROM artefacts WHERE featureid = ?";

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private AssetAccess assacc;

	@Autowired
	private FeatureAccess featacc;

	@Autowired
	private TrackingAccess trackacc;

	/**
	 * Prevent instances of this class.
	 */
	private ArtefactAccess() {
	}

	/**
	 * Gets a list of artefacts from the database.
	 * 
	 * @return a list of artefacts
	 */
	public List<Artefact> selectArtefacts() {
		List<Artefact> result = null;

		result = jdbc.query(SELECT_SQL, new ArtefactRowMapper());

		// load assets for each artefact...
		result.forEach(art -> art.setAsset(assacc.selectAssetsByID(art.getAsset().getId())));

		// load features for each artefact...
		result.forEach(feat -> feat.setFeature(featacc.selectFeaturesByID(feat.getFeature().getId())));

		// load tracking...
		result.forEach(art -> art.setTrackingList(trackacc.selectTrackingByItemID(art.getId(), TrackingType.ARTEFACT)));

		return result;
	}

	/**
	 * Gets an artefact from the database by its id.
	 * 
	 * @param id
	 *            the id to look for
	 * @return the artefact with the given id
	 */
	public Artefact selectArtefactsByID(int id) {
		List<Artefact> reslist = null;
		Artefact result = null;

		reslist = jdbc.query(SELECT_SQL + " WHERE artefacts.id = ?", new Object[] { id }, new ArtefactRowMapper());

		if (!reslist.isEmpty()) {
			result = reslist.get(0);
		}
		if (result != null) {
			// load asset...
			result.setAsset(assacc.selectAssetsByID(result.getAsset().getId()));

			// load feature...
			result.setFeature(featacc.selectFeaturesByID(result.getFeature().getId()));

			// load tracking...
			result.setTrackingList(trackacc.selectTrackingByItemID(result.getId(), TrackingType.ARTEFACT));
		}
		return result;
	}

	/**
	 * Returns a list of artefacts by the associated feature id.
	 * 
	 * @param id
	 *            the feature id to be looked for
	 * @return a list of found artefacts
	 */
	public List<Artefact> selectArtefactsByFeatureID(int id) {
		List<Artefact> reslist = null;

		reslist = jdbc.query(SELECT_SQL + " WHERE featureid = ?", new Object[] { id }, new ArtefactRowMapper());

		reslist.forEach(art -> {
			// load asset...
			art.setAsset(assacc.selectAssetsByID(art.getAsset().getId()));

			// load feature...
			art.setFeature(featacc.selectFeaturesByID(art.getFeature().getId()));

			// load tracking...
			art.setTrackingList(trackacc.selectTrackingByItemID(art.getId(), TrackingType.ARTEFACT));
		});

		return reslist;
	}

	/**
	 * Returns a list of artefacts by the associated feature id.
	 * 
	 * @param id
	 *            the feature id to be looked for
	 * @param loadfeature
	 *            if feature should be loaded with the artefact
	 * @return a list of found artefacts
	 */
	public List<Artefact> selectArtefactsByFeatureID(int id, boolean loadfeature) {
		List<Artefact> reslist = null;

		reslist = jdbc.query(SELECT_SQL + " WHERE featureid = ?", new Object[] { id }, new ArtefactRowMapper());

		reslist.forEach(art -> {
			// load asset...
			art.setAsset(assacc.selectAssetsByID(art.getAsset().getId()));

			// load feature...
			if (loadfeature) {
				art.setFeature(featacc.selectFeaturesByID(art.getFeature().getId()));
			}

			// load tracking...
			art.setTrackingList(trackacc.selectTrackingByItemID(art.getId(), TrackingType.ARTEFACT));
		});

		return reslist;
	}

	/**
	 * Inserts a new artefact, if id is null or updates an existing one.
	 * 
	 * @param artefact
	 *            the artefact to be inserted or updated
	 * @return the updated artefact model
	 */
	public Artefact saveArtefact(Artefact artefact) {
		Artefact art;

		if (artefact.getId() == null) {
			art = insertArtefact(artefact);

			final int id = art.getId();
			art.getTrackingList().forEach(tra -> tra.setItemid(id));
		} else {
			art = updateArtefact(artefact);
		}

		// save tracking...
		trackacc.saveTracking(art.getTrackingList());

		return art;
	}

	/**
	 * Inserts a new artefact.
	 * 
	 * @param artefact
	 *            the artefact to be inserted
	 * @return the updated artefact model
	 */
	private Artefact insertArtefact(Artefact artefact) {
		KeyHolder holder = new GeneratedKeyHolder();

		switch (artefact.getAsset().getType()) {
		case AUDIO:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, artefact.getTitle());
					ps.setString(2, ((AudioArtefact) artefact).getStart());
					ps.setString(3, ((AudioArtefact) artefact).getEnd());
					ps.setInt(4, artefact.getAsset().getId());
					ps.setInt(5, artefact.getFeature().getId());
					return ps;
				}
			}, holder);
			break;

		case TEXT:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, artefact.getTitle());
					ps.setString(2, ((TextArtefact) artefact).getStart());
					ps.setString(3, ((TextArtefact) artefact).getEnd());
					ps.setInt(4, artefact.getAsset().getId());
					ps.setInt(5, artefact.getFeature().getId());
					return ps;
				}
			}, holder);
			break;

		case VIDEO:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, artefact.getTitle());
					ps.setString(2, ((VideoArtefact) artefact).getStart());
					ps.setString(3, ((VideoArtefact) artefact).getEnd());
					ps.setInt(4, artefact.getAsset().getId());
					ps.setInt(5, artefact.getFeature().getId());
					return ps;
				}
			}, holder);
			break;

		case XML:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, artefact.getTitle());
					ps.setString(2, ((XMLArtefact) artefact).getNodes());
					ps.setString(3, null);
					ps.setInt(4, artefact.getAsset().getId());
					ps.setInt(5, artefact.getFeature().getId());
					return ps;
				}
			}, holder);
			break;

		case PICTURE:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_PICTUREARTEFACT_SQL,
							Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, artefact.getTitle());
					ps.setString(2, ((PictureArtefact) artefact).getX());
					ps.setString(3, ((PictureArtefact) artefact).getY());
					ps.setString(4, ((PictureArtefact) artefact).getWidth());
					ps.setString(5, ((PictureArtefact) artefact).getHeight());
					ps.setInt(6, artefact.getAsset().getId());
					ps.setInt(7, artefact.getFeature().getId());
					return ps;
				}
			}, holder);
			break;

		case OTHER:
			jdbc.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, artefact.getTitle());
					ps.setNull(2, 0);
					ps.setNull(3, 0);
					ps.setInt(4, artefact.getAsset().getId());
					ps.setInt(5, artefact.getFeature().getId());
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
		artefact.setId(newId);

		return artefact;
	}

	/**
	 * Updates the given artefact.
	 * 
	 * @param feature
	 *            the artefact to be updated
	 * @return the updated artefact model
	 */
	private Artefact updateArtefact(Artefact artefact) {
		switch (artefact.getAsset().getType()) {
		case AUDIO:
			jdbc.update(UPDATE_SQL, artefact.getTitle(), ((AudioArtefact) artefact).getStart(),
					((AudioArtefact) artefact).getEnd(), artefact.getAsset().getId(), artefact.getFeature().getId(),
					artefact.getId());
			break;
		case PICTURE:
			jdbc.update(UPDATE_PICTUREARTEFACT_SQL, artefact.getTitle(), ((PictureArtefact) artefact).getX(),
					((PictureArtefact) artefact).getY(), ((PictureArtefact) artefact).getWidth(),
					((PictureArtefact) artefact).getHeight(), artefact.getAsset().getId(),
					artefact.getFeature().getId(), artefact.getId());
			break;
		case TEXT:
			jdbc.update(UPDATE_SQL, artefact.getTitle(), ((TextArtefact) artefact).getStart(),
					((TextArtefact) artefact).getEnd(), artefact.getAsset().getId(), artefact.getFeature().getId(),
					artefact.getId());
			break;
		case VIDEO:
			jdbc.update(UPDATE_SQL, artefact.getTitle(), ((VideoArtefact) artefact).getStart(),
					((VideoArtefact) artefact).getEnd(), artefact.getAsset().getId(), artefact.getFeature().getId(),
					artefact.getId());
			break;
		case XML:
			jdbc.update(UPDATE_SQL, artefact.getTitle(), ((XMLArtefact) artefact).getNodes(), null,
					artefact.getAsset().getId(), artefact.getFeature().getId(), artefact.getId());
			break;
		case OTHER:
			jdbc.update(UPDATE_SQL, artefact.getTitle(), null, null, artefact.getAsset().getId(),
					artefact.getFeature().getId(), artefact.getId());
			break;
		default:
			break;
		}

		return artefact;
	}

	/**
	 * Deletes the given artefact.
	 * 
	 * @param artefact
	 *            the artefact to be deleted
	 * @return true, if successful, false, if not
	 */
	public boolean deleteArtefact(Artefact artefact) {
		int resultcount = 0;

		// delete tracking...
		trackacc.deleteTracking(artefact.getId(), TrackingType.ARTEFACT);

		resultcount = jdbc.update(DELETE_SQL, artefact.getId());

		return resultcount >= 1;
	}

	/**
	 * Deletes all artefacts associated with the given feature.
	 * 
	 * @param id
	 *            the feature id to delete the artefacts by
	 * @return true, if successful, false, if not
	 */
	public boolean deleteArtefactsByFeatureID(int id) {
		int resultcount = 0;
		List<Artefact> templist = null;

		templist = jdbc.query(SELECT_SQL + " WHERE featureid = ?", new Object[] { id }, new ArtefactRowMapper());

		templist.forEach(art ->
		// delete tracking...
		trackacc.deleteTracking(art.getId(), TrackingType.ARTEFACT));

		resultcount = jdbc.update(DELETE_BYFEATUREID_SQL, id);

		return resultcount >= 1;
	}
}
