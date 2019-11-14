package com.ghofrani.htw.RAN2.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.database.helper.TrackingType;
import com.ghofrani.htw.RAN2.database.rowmapper.AssetRowMapper;
import com.ghofrani.htw.RAN2.model.Asset;

/**
 * Class to handle asset interaction with the database.
 * 
 * 
 * @author Javad Ghofrani
 *
 */
@Component
public class AssetAccess {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private static final String INSERT_SQL = "INSERT INTO assets(title, description, url, metadata, astype) VALUES(?, ?, ?, ?, ?)";
	private static final String UPDATE_SQL = "UPDATE assets SET title = ?, description = ?, url = ?, metadata = ?, astype = ? WHERE id = ?";
	private static final String DELETE_SQL = "DELETE FROM assets WHERE id = ?";
	private static final String SELECT_SQL = "SELECT id, title, description, url, metadata, astype FROM assets";

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private TrackingAccess trackacc;

	/**
	 * Prevent instances of the class.
	 */
	private AssetAccess() {
	}

	/**
	 * Returns a list of all assets.
	 * 
	 * @return list of all assets
	 */
	public List<Asset> selectAssets() {
		LinkedList<Asset> result = new LinkedList<Asset>();

		log.debug("Getting assets...");

		jdbc.query(SELECT_SQL, new AssetRowMapper()).forEach(asset -> {
			result.add(asset);
		});

		// load tracking...
		result.forEach(ass -> ass.setTrackingList(trackacc.selectTrackingByItemID(ass.getId(), TrackingType.ASSET)));

		return result;
	}

	/**
	 * Returns the asset with the given id.
	 * 
	 * @param id
	 *            id of the asset to be retrieved
	 * @return the retrieved asset
	 */
	public Asset selectAssetsByID(int id) {
		Asset result = null;

		log.debug(String.format("Getting asset with ID %d", id));

		List<Asset> resultlist = jdbc.query(SELECT_SQL + " WHERE id = ?", new Object[] { id }, new AssetRowMapper());

		if (!resultlist.isEmpty()) {
			result = resultlist.get(0);
		}
		if (result != null) {
			// load tracking...
			result.setTrackingList(trackacc.selectTrackingByItemID(result.getId(), TrackingType.ASSET));
		}

		return result;
	}

	/**
	 * Returns a list of assets for the given feature id.
	 * 
	 * @param id
	 *            the id of the feature
	 * @return a list of found assets for the given feature id
	 */
	public List<Asset> selectAssetsByFeatureID(int id) {
		LinkedList<Asset> result = new LinkedList<Asset>();

		log.debug("Getting assets...");

		jdbc.query(SELECT_SQL + " JOIN featuresxassets ON assetid = id WHERE featureid = ?", new Object[] { id },
				new AssetRowMapper()).forEach(asset -> {
					result.add(asset);
				});

		// load tracking...
		result.forEach(ass -> ass.setTrackingList(trackacc.selectTrackingByItemID(ass.getId(), TrackingType.ASSET)));

		return result;
	}

	/**
	 * Inserts a new asset, if id is null or updates an existing one.
	 * 
	 * @param asset
	 *            the asset to be inserted or updated
	 * @return the updated asset model
	 */
	public Asset saveAsset(Asset asset) {
		Asset ass = asset;

		if (asset.getId() == null) {
			ass = insertAsset(asset);

			final int id = ass.getId();
			ass.getTrackingList().forEach(tra -> tra.setItemid(id));
		} else {
			ass = updateAsset(asset);
		}

		// save tracking...
		trackacc.saveTracking(ass.getTrackingList());

		return ass;
	}

	/**
	 * Inserts a new asset.
	 * 
	 * @param asset
	 *            the asset to be inserted
	 * @return the updated asset model
	 * @author Jannik Gröger(added only url parameter)
	 */
	private Asset insertAsset(Asset asset) {
		KeyHolder holder = new GeneratedKeyHolder();

		jdbc.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, asset.getTitle());
				ps.setString(2, asset.getDescription());
				ps.setString(3, asset.getURL());
				ps.setString(4, asset.getMetadataAsJSONString());
				ps.setInt(5, asset.getType().getValue());
				return ps;
			}
		}, holder);

		int newId;
		if (holder.getKeys().size() > 1) {
			newId = (int) holder.getKeys().get("id");
		} else {
			newId = holder.getKey().intValue();
		}
		asset.setId(newId);

		return asset;
	}

	/**
	 * Updates the given asset.
	 * 
	 * @param asset
	 *            the asset to be updated
	 * @return the updated asset model
	 * @author Jannik Gröger
	 */
	private Asset updateAsset(Asset asset) {

		jdbc.update(UPDATE_SQL, asset.getTitle(), asset.getDescription(), asset.getURL(),
				asset.getMetadataAsJSONString(), asset.getType().getValue(), asset.getId());

		return asset;

	}

	/**
	 * Deletes the given asset.
	 * 
	 * @param asset
	 *            the asset to be deleted
	 * @return true, if successful, false, if not
	 */
	public boolean deleteAsset(Asset asset) {
		int resultcount = 0;

		resultcount = jdbc.update(DELETE_SQL, asset.getId());

		// delete tracking...
		trackacc.deleteTracking(asset.getId(), TrackingType.ASSET);

		if (resultcount >= 1) {
			return true;
		}

		return false;
	}
}
