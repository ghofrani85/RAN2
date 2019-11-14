package com.ghofrani.htw.RAN2.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.database.helper.TrackingType;
import com.ghofrani.htw.RAN2.database.rowmapper.TrackingRowMapper;
import com.ghofrani.htw.RAN2.model.Tracking;

/**
 * Class to handle asset interaction with the database.
 * 
 * @author Tobias Powelske
 *
 */
@Component
public class TrackingAccess {
	private static final String SELECT_SQL = "SELECT itemid, trtype, changemade, text FROM tracking";
	private static final String INSERT_SQL = "INSERT INTO tracking(itemid, trtype, changemade, text) VALUES(?, ?, ?, ?)";
	private static final String DELETE_SQL = "DELETE FROM tracking WHERE itemid = ? AND trtype = ?";

	@Autowired
	private JdbcTemplate jdbc;

	/**
	 * Prevent instances of this class.
	 */
	private TrackingAccess() {
	}

	/**
	 * Gets an tracking from the database by its itemid.
	 * 
	 * @param id
	 *            the id to look for
	 * @param type
	 *            the type of the tracked model
	 * @return the tracking with the given id
	 */
	public List<Tracking> selectTrackingByItemID(int id, TrackingType type) {
		List<Tracking> reslist = null;

		reslist = jdbc.query(SELECT_SQL + " WHERE itemid = ? AND trtype = ?", new Object[] { id, type.getValue() },
				new TrackingRowMapper());

		return reslist;
	}

	/**
	 * Inserts a new tracking, if id is null or updates an existing one.
	 * 
	 * @param tracking
	 *            the tracking to be inserted
	 */
	public void saveTracking(List<Tracking> tracking) {
		if (!tracking.isEmpty()) {
			deleteTracking(tracking.get(0).getItemid(), tracking.get(0).getType());
			tracking.forEach(tra -> insertTracking(tra));
		}
	}

	/**
	 * Inserts a new tracking.
	 * 
	 * @param tracking
	 *            the tracking to be inserted
	 */
	private void insertTracking(Tracking tracking) {
		jdbc.update(INSERT_SQL, tracking.getItemid(), tracking.getType().getValue(), tracking.getChangemade(),
				tracking.getText());
	}

	/**
	 * Deletes the tracking entries for the given itemid.
	 * 
	 * @param itemid
	 *            the itemid to be deleted
	 * @param type
	 *            the type of the tracked model
	 * @return true, if successful, false, if not
	 */
	public boolean deleteTracking(int itemid, TrackingType type) {
		int resultcount = jdbc.update(DELETE_SQL, itemid, type.getValue());

		return resultcount >= 1;
	}
}
