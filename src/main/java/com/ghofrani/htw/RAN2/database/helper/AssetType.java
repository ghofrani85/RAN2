package com.ghofrani.htw.RAN2.database.helper;

/**
 * Enum for different types of assets.
 * 
 * @author Tobias Powelske
 *
 */
public enum AssetType {
	PICTURE(0), AUDIO(1), VIDEO(2), TEXT(3), XML(4), OTHER(5);

	private final int value;

	/**
	 * Constructor
	 * 
	 * @param value
	 *            value of the enum
	 */
	private AssetType(int value) {
		this.value = value;
	}

	/**
	 * Gets the value of the enum.
	 * 
	 * @return the value of the enum
	 */
	public int getValue() {
		return value;
	}
}
