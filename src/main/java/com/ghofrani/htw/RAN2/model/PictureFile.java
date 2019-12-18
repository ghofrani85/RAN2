package com.ghofrani.htw.RAN2.model;

/**
 * This class provides all information needed for editing a picture file.
 * 
 * @author Stefan Schmidt
 *
 */
public class PictureFile extends File {

	private String x;
	private String y;
	private String width;
	private String height;

	/**
	 * @param id The ID of the picture file in the databse
	 * @param title The title of the file
	 * @param asset The asset needed for the file
	 * @param x The offset in x direction to get a subimage
	 * @param y The offset in y direction to get a subimage
	 * @param width The width of the subimage
	 * @param height The height of the subimage
	 */
	public PictureFile(Integer id, String title, Asset asset, String x, String y, String width, String height) {
		super(id, title, asset);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * @return the x offset
	 */
	public String getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x offset to set
	 */
	public void setX(String x) {
		this.x = x;
	}

	/**
	 * @return the y offset
	 */
	public String getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y offset to set
	 */
	public void setY(String y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(String height) {
		this.height = height;
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
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
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
		if (!(obj instanceof PictureFile)) {
			return false;
		}
		return super.equals(obj);
	}
}
