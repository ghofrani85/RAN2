package com.ghofrani.htw.RAN2.model;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.UrlResource;

import com.ghofrani.htw.RAN2.database.helper.AssetType;

/**
 * The representation of a file of any type.
 * 
 * Assets are needed for artefacts.
 * 
 * @author Stefan Schmidt
 *
 */
public abstract class Asset {

	// Path to save assets to
	public static String assetPath = null;

	private Integer id;
	private String title;
	private String description;
	private Map<String, String> metadata;
	private AssetType type;
	private List<Tracking> trackingList;
	private String URL;

	/**
	 * Default Constructor
	 */
	public Asset() {
		this.id = null;
		this.title = "";
		this.description = "";
		this.metadata = new HashMap<>();
		this.type = null;
		this.trackingList = new LinkedList<>();
		this.URL = null;
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
	 * @param type
	 *            the type of data the file contains
	 */
	public Asset(Integer id, String title, String description, String URL, AssetType type) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.URL = URL;
		this.metadata = new HashMap<>();
		this.type = type;
		this.trackingList = new LinkedList<>();
	}

	/**
	 * 
	 * Creates a JSON Object for the instance of asset
	 * 
	 * @return JSONObject
	 */
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();

		try {
			json.put("id", id);
			json.put("title", title);
			json.put("description", description);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the type
	 */
	public AssetType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(AssetType type) {
		this.type = type;
	}

	/**
	 * @return the trackingList
	 */
	public List<Tracking> getTrackingList() {
		return trackingList;
	}

	/**
	 * @param trackingList
	 *            the trackingList to set
	 */
	public void setTrackingList(List<Tracking> trackingList) {
		this.trackingList = trackingList;
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
		result = prime * result + ((URL == null) ? 0 : URL.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((trackingList == null) ? 0 : trackingList.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Asset)) {
			return false;
		}
		Asset other = (Asset) obj;
		if (this.id == null || other.getId() == null) {
			return false;
		}
		return this.id.equals(other.getId());
	}

	/**
	 * This method returns the metadata as String in json format
	 * 
	 * @return String containing metadata in json format
	 * @throws JSONException When JSON could not be parsed
	 * @author Jannik Gröger
	 */
	public String getMetadataAsJSONString() {
		JSONObject json = new JSONObject();

		Iterator<String> iter = metadata.keySet().iterator();

		while (iter.hasNext()) {
			String key = iter.next();
			String value = metadata.get(key);
			try {
				json.put(key, value);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return json.toString();
	}

	/**
	 * Sets the metadata with a given json String
	 * 
	 * @param jsonString
	 *            A string with json format containing (key,value)-pairs of the
	 *            metadata, if string is null or empty, default metadata will be
	 *            used
	 * @throws JSONException
	 *             Will be thrown if wrong string has invalid json format
	 * @author Jannik Gröger
	 */
	public void setMetadataWithJSONString(String jsonString) {

		if (jsonString == null || jsonString.length() == 0) {
			metadata = new HashMap<String, String>();
			metadata.put("Filename", "none");

			metadata.put("FileFormat", "none");

			metadata.put("Size", "0B");

			metadata.put("Uploaded by", "none");

			metadata.put("Uploaded on", "none");

			return;
		}

		JSONObject json;
		try {
			json = new JSONObject(jsonString);

			Iterator<String> iter = json.keys();

			metadata = new HashMap<>();

			while (iter.hasNext()) {
				String key = iter.next();
				String value = json.getString(key);
				metadata.put(key, value);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the metadata
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata
	 *            the metadata to set
	 */
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Adds a tracking object to the tracking list.
	 * 
	 * @param tracking
	 *            the object to be added
	 */
	public void addTrackingEntry(Tracking tracking) {
		if (trackingList == null) {
			trackingList = new LinkedList<>();
		}
		trackingList.add(tracking);
	}

	/**
	 * Removes a tracking object from the tracking list.
	 * 
	 * @param tracking
	 *            the object to be removed
	 */
	public void removeTrackingEntry(Tracking tracking) {
		trackingList.remove(tracking);
	}

	/**
	 * @return the uRL
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * @param uRL
	 *            the uRL to set
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}

	/**
	 * Returns an URLResource object, which is a handle for the file this assets
	 * represents
	 * 
	 * @return URLResource pointing to file this asset represents
	 * @throws MalformedURLException When URL has invalid format
	 */
	public UrlResource getURLResource() throws MalformedURLException {
		// Check if path is relative path to local file
		if (this.URL.startsWith("files/sub")) {
			Path path = Paths.get(this.URL.replaceAll("files/", assetPath + "/"));

			return new UrlResource(path.toUri());
		} else {
			return new UrlResource(this.URL);
		}

	}

}
