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
import com.ghofrani.htw.RAN2.database.rowmapper.FeatureRowMapper;
import com.ghofrani.htw.RAN2.model.Feature;

/**
 * Class to handle feature interaction with the database.
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
	private static final String SELECT_DISTINCT_SQL = "SELECT DISTINCT features.id, folders.title, "
			+ "folders.description, folders.parentid, folders.updatedparent FROM folders";
	private static final String DELETE_PROJECTSXFEATURES_SQL = "DELETE FROM projectsxfolders WHERE featureid = ?";
	private static final String DELETE_PRODUCTSXFEATURES_SQL = "DELETE FROM productsxfolders WHERE featureid = ?";
	private static final String INSERT_FEATURESXFEATUREARTEFACTS_SQL = "INSERT INTO foldersxfeatureartefacts(featureid, featureartefactid) VALUES(?, ?)";
	private static final String DELETE_FEATURESXFEATUREARTEFACTS_FEATURE_SQL = "DELETE FROM foldersxfeatureartefacts WHERE featureid = ?";
	private static final String DELETE_FEATURESXFEATUREARTEFACTS_FEATUREARTEFACT_SQL = "DELETE FROM foldersxfeatureartefacts WHERE featureartefactid = ?";

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private TrackingAccess trackacc;

	@Autowired
	private ArtefactAccess artacc;

	/**
	 * Prevent instances of the class.
	 */
	private FolderAccess() {
	}

	/**
	 * Returns a list of all features.
	 * 
	 * @return list of all features
	 */
	public List<Feature> selectFeatures() {
		LinkedList<Feature> result = new LinkedList<>();

		log.debug("Getting features...");

		jdbc.query(SELECT_SQL, new FeatureRowMapper()).forEach(feature -> result.add(feature));

		// load changed parent...
		result.forEach(feat -> {
			if (feat.getParent() != null) {
				feat.setParent(selectFeaturesByID(feat.getParent().getId()));
			}
		});

		// load artefacts...
		result.forEach(feat -> feat.setArtefactList(artacc.selectArtefactsByFeatureID(feat.getId())));

		// load tracking...
		result.forEach(
				feat -> feat.setTrackingList(trackacc.selectTrackingByItemID(feat.getId(), TrackingType.FEATURE)));

		return result;
	}

	/**
	 * Returns the feature with the given id.
	 * 
	 * @param id
	 *            id of the feature to be retrieved
	 * @return the retrieved feature
	 */
	public Feature selectFeaturesByID(int id) {
		Feature result = null;

		log.debug("Getting feature with ID {}", id);

		List<Feature> resultlist = jdbc.query(SELECT_SQL + " WHERE id = ?", new Object[] { id },
				new FeatureRowMapper());

		if (!resultlist.isEmpty()) {
			result = resultlist.get(0);

			// load changed parent...
			if (result.getParent() != null) {
				result.setParent(selectFeaturesByID(result.getParent().getId()));
			}
		}

		if (result != null) {
			// load artefacts...
			result.setArtefactList(artacc.selectArtefactsByFeatureID(result.getId(), false));

			// load tracking...
			result.setTrackingList(trackacc.selectTrackingByItemID(result.getId(), TrackingType.FEATURE));
			
			// load featureArtefacts...
			result.setFeatureArtefactList(selectFeatureArtefactsByFeatureID(result.getId()));

		}

		return result;
	}

	/**
	 * Returns a list of features for the given project id.
	 * 
	 * @param id
	 *            the id of the project to return the features for
	 * @return a list of found features
	 */
	public List<Feature> selectFeaturesByProjectID(int id) {
		LinkedList<Feature> result = new LinkedList<>();

		log.debug("Getting features for project {}...", id);

		jdbc.query(SELECT_SQL + " JOIN projectsxfeatures on featureid = features.id WHERE projectid = ?",
				new Object[] { id }, new FeatureRowMapper()).forEach(feature -> result.add(feature));

		// load changed parent...
		result.forEach(feat -> {
			if (feat.getParent() != null) {
				feat.setParent(selectFeaturesByID(feat.getParent().getId()));
			}
		});

		// load artefacts...
		result.forEach(feat -> feat.setArtefactList(artacc.selectArtefactsByFeatureID(feat.getId())));

		// load tracking...
		result.forEach(
				feat -> feat.setTrackingList(trackacc.selectTrackingByItemID(feat.getId(), TrackingType.FEATURE)));

		// load featureArtefacts...
		result.forEach(
				feat -> feat.setFeatureArtefactList(selectFeatureArtefactsByFeatureID(feat.getId())));

		
		return result;
	}

	/**
	 * Returns a list of features for the given asset id.
	 * 
	 * @param id
	 *            the id of the asset to return the features for
	 * @return a list of found features
	 */
	public List<Feature> selectFeaturesByAssetID(int id) {
		List<Feature> result;

		log.debug("Getting features for asset {}...", id);

		result = jdbc.query(SELECT_DISTINCT_SQL + " JOIN artefacts on featureid = features.id WHERE assetid = ?",
				new Object[] { id }, new FeatureRowMapper());

		// load changed parent...
		result.forEach(feat -> {
			if (feat.getParent() != null) {
				feat.setParent(selectFeaturesByID(feat.getParent().getId()));
			}
		});

		// load artefacts...
		result.forEach(feat -> feat.setArtefactList(artacc.selectArtefactsByFeatureID(feat.getId())));

		// load tracking...
		result.forEach(
				feat -> feat.setTrackingList(trackacc.selectTrackingByItemID(feat.getId(), TrackingType.FEATURE)));

		// load featureArtefacts...
		result.forEach(
				feat -> feat.setFeatureArtefactList(selectFeatureArtefactsByFeatureID(feat.getId())));
		
		return result;
	}
	
	public List<Feature> selectFeatureArtefactsByFeatureID(int id) {
		List<Feature> result = new LinkedList<>();
		
		log.debug("Getting featureArtefacts list fot feature {}...", id);
		
		jdbc.query(SELECT_SQL + " JOIN featuresxfeatureartefacts on featureartefactid = features.id WHERE featureid = ?",
		new Object[] { id }, new FeatureRowMapper()).forEach(featureartefact -> result.add(featureartefact));

//		// load changed parent...
//		result.forEach(feat -> {
//			if (feat.getParent() != null) {
//				feat.setParent(selectFeaturesByID(feat.getParent().getId()));
//			}
//		});
		
		// load artefacts...
		result.forEach(feat -> feat.setArtefactList(artacc.selectArtefactsByFeatureID(feat.getId())));
		
		// load tracking...
		result.forEach(
				feat -> feat.setTrackingList(trackacc.selectTrackingByItemID(feat.getId(), TrackingType.FEATURE)));
		
		// load featureArtefacts...
		result.forEach(
				feat -> {
					if (feat.getFeatureArtefactList() != null && feat.getFeatureArtefactList().isEmpty() == false) {
						feat.setFeatureArtefactList(selectFeatureArtefactsByFeatureID(feat.getId()));
					}
				});
		
		return result;
	}
	
	public List<Feature> selectFeatureByFeatureArtefactID(int id) {
		List<Feature> result = new LinkedList<>();
		
		log.debug("Getting feature for the featureArtefact {}", id);
		
		jdbc.query(SELECT_DISTINCT_SQL + " JOIN featuresxfeatureartefacts on featureid = features.id WHERE featureartefactid = ?",
		new Object[] { id }, new FeatureRowMapper()).forEach(feature -> result.add(feature));
		
		// load artefacts...
		result.forEach(
				feat -> feat.setArtefactList(artacc.selectArtefactsByFeatureID(feat.getId())));
		
		// load tracking...
		result.forEach(
				feat -> feat.setTrackingList(trackacc.selectTrackingByItemID(feat.getId(), TrackingType.FEATURE)));
		
		// load featureArtefacts...
		result.forEach(
				feat -> {
					if (feat.getFeatureArtefactList() != null && feat.getFeatureArtefactList().isEmpty() == false) {
						feat.setFeatureArtefactList(selectFeatureArtefactsByFeatureID(feat.getId()));
					}
				});
		
		return result;
	}
	
	public void selectFeaturesXFeatureArtefacts (Feature feature) {
		log.debug("Getting featureArtefacts for the feature {}", feature.getId());
		
		jdbc.query(
				"SELECT features.id, title, description, parentid, updatedparent "
				+ "FROM featuresxfeatureartefacts JOIN features ON featureartefactid = features.id WHERE featureid = ?",
		new Object[] { feature.getId() }, new FeatureRowMapper())
		.forEach(featureArtefact -> feature.addFeatureArtefact(featureArtefact));

	}

	/**
	 * Inserts a new feature, if id is null or updates an existing one.
	 * 
	 * @param feature
	 *            the feature to be inserted or updated
	 * @return the updated feature model
	 */
	public Feature saveFeature(Feature feature) {
		Feature result;

		if (feature.getId() == null) {
			result = insertFeature(feature);

			final int id = result.getId();
			result.getTrackingList().forEach(tra -> tra.setItemid(id));
			result.getArtefactList().forEach(art -> art.getFeature().setId(id));
		} else {
			result = updateFeature(feature);
		}

		// save tracking...
		trackacc.saveTracking(result.getTrackingList());

		// save artefacts...
		result.getArtefactList().forEach(art -> artacc.saveArtefact(art));
		
		// save featureArtefacts for feature...
		saveFeaturesXFeatureArtefacts(result);
		
		// save featureArtefacts
		if (result.getFeatureArtefactList().isEmpty() == false) {
			result.getFeatureArtefactList().forEach(featart -> saveFeature(featart));
		}

		return result;
	}
	
	/**
	 * Inserts the features in the given feature featureArtefactList into the database.
	 * 
	 * @param feature
	 * 
	 * @author Hongfei Chen
	 */
	public void saveFeaturesXFeatureArtefacts(Feature feature) {
		jdbc.update(DELETE_FEATURESXFEATUREARTEFACTS_FEATURE_SQL, feature.getId());
		
		feature.getFeatureArtefactList()
				.forEach(featart -> jdbc.update(INSERT_FEATURESXFEATUREARTEFACTS_SQL, feature.getId(), featart.getId()));
	}

	/**
	 * Inserts a new feature.
	 * 
	 * @param feature
	 *            the feature to be inserted
	 * @return the updated feature model
	 */
	private Feature insertFeature(Feature feature) {
		KeyHolder holder = new GeneratedKeyHolder();

		jdbc.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, feature.getTitle());
				ps.setString(2, feature.getDescription());
				if (feature.getParent() != null) {
					ps.setInt(3, feature.getParent().getId());
				} else {
					ps.setNull(3, java.sql.Types.INTEGER);
				}
				ps.setBoolean(4, feature.isUpdatedparent());
				return ps;
			}
		}, holder);

		int newId;
		if (holder.getKeys().size() > 1) {
			newId = (int) holder.getKeys().get("id");
		} else {
			newId = holder.getKey().intValue();
		}
		feature.setId(newId);

		return feature;
	}

	/**
	 * Updates the given feature.
	 * 
	 * @param feature
	 *            the feature to be updated
	 * @return the updated feature model
	 */
	private Feature updateFeature(Feature feature) {
		Integer parentid = null;

		if (feature.getParent() != null) {
			parentid = feature.getParent().getId();
		}

		jdbc.update(UPDATE_SQL, feature.getTitle(), feature.getDescription(), parentid, feature.isUpdatedparent(),
				feature.getId());

		return feature;
	}
	
	

	/**
	 * Deletes the given feature.
	 * 
	 * @param feature
	 *            the feature to be deleted
	 * @return true, if successful, false, if not
	 */
	public boolean deleteFeature(Feature feature) {
		int resultcount = 0;

		// delete tracking...
		trackacc.deleteTracking(feature.getId(), TrackingType.FEATURE);

		// delete connections...
		jdbc.update(DELETE_PRODUCTSXFEATURES_SQL, feature.getId());
		jdbc.update(DELETE_PROJECTSXFEATURES_SQL, feature.getId());
		jdbc.update(DELETE_FEATURESXFEATUREARTEFACTS_FEATURE_SQL, feature.getId());
		jdbc.update(DELETE_FEATURESXFEATUREARTEFACTS_FEATUREARTEFACT_SQL, feature.getId());

		// delete artefacts...
		artacc.deleteArtefactsByFeatureID(feature.getId());
		
		// delete featureArtefacts...
		feature.getFeatureArtefactList().forEach(featart -> deleteFeature(featart));
		
		// set childrens id to null...
		jdbc.update(UPDATE_PARENT_SQL, null, false, feature.getId());

		resultcount = jdbc.update(DELETE_SQL, feature.getId());

		return resultcount >= 1;
	}
}
