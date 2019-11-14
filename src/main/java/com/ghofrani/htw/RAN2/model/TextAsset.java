package com.ghofrani.htw.RAN2.model;

import com.ghofrani.htw.RAN2.database.helper.AssetType;

/**
 * When an instance of this class is created it creates a text type asset.
 * 
 * @author Stefan Schmidt
 *
 */
public class TextAsset extends Asset {

	/**
	 * Default Constructor
	 */
	public TextAsset() {
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
	public TextAsset(Integer id, String title, String description, String URL) {
		super(id, title, description, URL, AssetType.TEXT);
	}

}
