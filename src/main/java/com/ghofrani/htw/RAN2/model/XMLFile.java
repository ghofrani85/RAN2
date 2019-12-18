package com.ghofrani.htw.RAN2.model;

/**
 * This class provides all information needed for editing a XML file.
 * 
 * @author Stefan Schmidt
 *
 */
public class XMLFile extends File {

	private String nodes;

	/**
	 * @param id
	 *            The ID of this File
	 * @param title
	 *            Title of the File
	 * @param asset
	 *            Asset which the file belongs to
	 * @param nodes
	 *            The selected Nodes in the XML, in the following notation: "1,3,7"
	 *            for nodes 1, 3 und 7
	 */
	public XMLFile(Integer id, String title, Asset asset, String nodes) {
		super(id, title, asset);
		this.nodes = nodes;
	}

	/**
	 * @return the nodes
	 */
	public String getNodes() {
		return nodes;
	}

	/**
	 * @param nodes
	 *            the nodes to set
	 */
	public void setNodes(String nodes) {
		this.nodes = nodes;
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
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
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
		if (!(obj instanceof XMLFile)) {
			return false;
		}
		return super.equals(obj);
	}
}
