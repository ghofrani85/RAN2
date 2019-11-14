package com.ghofrani.htw.RAN2.model;

import java.util.Date;

import com.ghofrani.htw.RAN2.database.helper.TrackingType;

/**
 * Class for providing tracking data.
 * 
 * @author Tobias Powelske
 *
 */
public class Tracking {

	private Integer itemid;
	private TrackingType type;
	private Date changemade;
	private String text;

	/**
	 * Default constructor
	 */
	public Tracking() {
		this.itemid = null;
		this.changemade = null;
		this.text = null;
	}

	/**
	 * Constructor
	 * 
	 * @param itemid ID of the item
	 * @param type Type of trackinginfo
	 * @param changemade the date of the change
	 * @param text What was changed
	 */
	public Tracking(Integer itemid, TrackingType type, Date changemade, String text) {
		this.itemid = itemid;
		this.type = type;
		this.changemade = new Date(changemade.getTime());
		this.text = text;
	}

	/**
	 * @return the itemid
	 */
	public Integer getItemid() {
		return itemid;
	}

	/**
	 * @param itemid
	 *            the itemid to set
	 */
	public void setItemid(Integer itemid) {
		this.itemid = itemid;
	}

	/**
	 * @return the type
	 */
	public TrackingType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(TrackingType type) {
		this.type = type;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the changemade
	 */
	public Date getChangemade() {
		if (changemade == null) {
			return changemade;
		}
		return new Date(changemade.getTime());
	}

	/**
	 * @param changemade
	 *            the changemade to set
	 */
	public void setChangemade(Date changemade) {
		this.changemade = new Date(changemade.getTime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changemade == null) ? 0 : changemade.hashCode());
		result = prime * result + ((itemid == null) ? 0 : itemid.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Tracking)) {
			return false;
		}
		Tracking other = (Tracking) obj;
		if (changemade == null) {
			if (other.changemade != null) {
				return false;
			}
		} else if (!changemade.equals(other.changemade)) {
			return false;
		}
		if (itemid == null) {
			if (other.itemid != null) {
				return false;
			}
		} else if (!itemid.equals(other.itemid)) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}
}
