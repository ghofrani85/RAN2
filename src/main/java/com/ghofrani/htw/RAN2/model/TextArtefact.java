package com.ghofrani.htw.RAN2.model;

/**
 * This class provides all information needed for editing a text file.
 * 
 * @author Stefan Schmidt
 *
 */
public class TextArtefact extends Artefact {

	private String start;
	private String end;

	/**
	 * @param id The ID of the artefact in the database
	 * @param title The title of the artefact
	 * @param asset The asset needed for the artefact
	 * @param start The first character of the selection in the text
	 * @param end The last character of the selection in the text
	 */
	public TextArtefact(Integer id, String title, Asset asset, String start, String end) {
		super(id, title, asset);
		this.start = start;
		this.end = end;
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
		if (!(obj instanceof TextArtefact)) {
			return false;
		}
		return super.equals(obj);
	}
}
