package com.ghofrani.htw.RAN2.model;

import com.ghofrani.htw.RAN2.database.helper.AssetType;

/**
 * Asset class for type other.
 * 
 * @author Tobias Powelske
 *
 */
public class OtherAsset extends Asset {
	/**
	 * Default Constructor
	 */
	public OtherAsset() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            id of the asset
	 * @param title
	 *            title of the asset
	 * @param description
	 *            description of the asset
	 * @param URL
	 *            an URL-reference to the file this asset is representing
	 */
	public OtherAsset(Integer id, String title, String description, String URL) {
		super(id, title, description, URL, AssetType.OTHER);
	}
}
