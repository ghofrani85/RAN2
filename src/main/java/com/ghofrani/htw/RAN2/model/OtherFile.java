package com.ghofrani.htw.RAN2.model;

/**
 * File class for type other.
 * 
 * @author Tobias Powelske
 *
 */
public class OtherFile extends File {
	private String start;
	private String end;

	/**
	 * Constructor
	 * 
	 * @param id ID of file
	 * @param title Title of this file
	 * @param asset Asset associated with this file
	 * @param start Start String
	 * @param end End String
	 */
	public OtherFile(Integer id, String title, Asset asset, String start, String end) {
		super(id, title, asset);
		this.start = start;
		this.end = end;
	}

	/**
	 * Default constructor
	 */
	public OtherFile() {
		super(null, null, null);
		this.start = null;
		this.end = null;
	}

	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	/**
	 * Equals method, only compares the ids because the content is saved in the
	 * database.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof OtherFile)) {
			return false;
		}
		return super.equals(obj);
	}
}
