package com.ghofrani.htw.RAN2.model;

import com.ghofrani.htw.RAN2.database.helper.AssetType;

/**
 * When an instance of this class is created it creates a XML type asset.
 * 
 * @author Stefan Schmidt
 *
 */
public class XMLAsset extends Asset {

	/**
	 * Default Constructor
	 */
	public XMLAsset() {
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
	public XMLAsset(Integer id, String title, String description, String URL) {
		super(id, title, description, URL, AssetType.XML);
	}
}
