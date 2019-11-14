package com.ghofrani.htw.RAN2.model;

/**
 * This class provides all information needed for editing an audio file.
 * 
 * @author Stefan Schmidt
 *
 */
public class AudioArtefact extends Artefact {

	private String start;
	private String end;

	/**
	 * @param id The ID of the audio artefact in the database
	 * @param title Title of the artefact
	 * @param asset The audio asset which is needed for the artefact
	 * @param start The start time of the artefact
	 * @param end The end time of the artefact
	 */
	public AudioArtefact(Integer id, String title, Asset asset, String start, String end) {
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
		if (!(obj instanceof AudioArtefact)) {
			return false;
		}
		return super.equals(obj);
	}
}
