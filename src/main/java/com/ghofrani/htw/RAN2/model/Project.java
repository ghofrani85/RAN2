package com.ghofrani.htw.RAN2.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Robert Völkner
 *
 */
public class Project {

	private Integer id;
	private String title;
	private String description;
	private Date lastChange;
	private List<Product> productList;
	private List<Folder> projectFolderList;
	private User user;
	private Project parent;
	private boolean updatedparent;
	private List<Tracking> trackingList;
	private Integer upVote; // Added 
	private Integer downVote; // Added
	private String rate; //Added
	private List<User> ratedUserList; //Added

	
	/**
	 * Constructor
	 * 
	 * @param id
	 *            id of the project
	 * @param title
	 *            title of the project
	 * @param description
	 *            description of the project
	 * @param lastChange
	 *            date of the last change made
	 * @param productList
	 *            list of all products in the project
	 * @param projectfolderList
	 *            list of all folders in the project
	 * @param user
	 *            the owner of the project
	 * @param parent
	 *            the original to this project
	 * @param updatedparent
	 *            flag for notification about changes in the parent
	 */
	public Project(Integer id, String title, String description, Date lastChange, List<Product> productList,
			List<Folder> projectfolderList, User user, Project parent, boolean updatedparent) {
		this.id = id;
		this.title = title;
		this.description = description;
		if (lastChange != null) {
			this.lastChange = new Date(lastChange.getTime());
		} else {
			this.lastChange = new Date();
		}

		this.productList = productList;
		this.projectFolderList = projectfolderList;
		this.user = user;
		this.parent = parent;
		this.updatedparent = updatedparent;
		this.trackingList = new LinkedList<>();
		this.upVote = 0;
		this.downVote = 0;
		this.ratedUserList = new LinkedList<>();
	}

	/**
	 * Default Constructor.
	 */
	public Project() {
		this.id = null;
		this.title = "";
		this.description = "";
		this.lastChange = new Date();
		this.productList = new LinkedList<>();
		this.projectFolderList = new LinkedList<>();
		this.user = null;
		this.parent = null;
		this.updatedparent = false;
		this.trackingList = new LinkedList<>();
		this.upVote = 0;
		this.downVote = 0;
		this.ratedUserList = new LinkedList<>();
	}

	/**
	 * Constructor for creating a new Project.
	 * 
	 * @param title
	 *            title of the project
	 * @param description
	 *            description of the project
	 * @param owner
	 *            the owner of the project
	 */
	public Project(String title, String description, User owner) {
		this.id = null;
		this.title = title;
		this.description = description;
		this.lastChange = new Date();
		this.productList = new LinkedList<>();
		this.projectFolderList = new LinkedList<>();
		this.user = owner;
		this.trackingList = new LinkedList<>();
		this.upVote = 0;
		this.downVote = 0;
		this.ratedUserList = new LinkedList<>();
	}

	/**
	 * Adds a product to a project
	 * 
	 * @param prod
	 *            the product to be added
	 */
	public void addProduct(Product prod) {
		if (this.productList == null) {
			this.productList = new LinkedList<>();
		}
		this.productList.add(prod);
	}

	/**
	 * Removes a product to a project
	 * 
	 * @param prod
	 *            the product to be removed
	 */
	public void removeProduct(Product prod) {
		this.productList.remove(prod);
	}

	/**
	 * Adds a folder to a project
	 * 
	 * @param feat
	 *            the folder to be added
	 */
	public void addFolder(Folder feat) {
		if (this.projectFolderList == null) {
			this.projectFolderList = new LinkedList<>();
		}
		this.projectFolderList.add(feat);
	}

	/**
	 * Removes a folder to a project
	 * 
	 * @param feat
	 *            the folder to be removed
	 */
	public void removeFolder(Folder feat) {
		this.projectFolderList.remove(feat);
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
	 * @return the the date and time of the last change
	 */
	public Date getLastChange() {
		return new Date(lastChange.getTime());
	}

	/**
	 * @param lastChange
	 *            the date and time to set in the format: "DD.MM.YYYY - HH:MM"
	 */
	public void setLastChange(Date lastChange) {
		this.lastChange = new Date(lastChange.getTime());
	}

	/**
	 * @return the list of all products in the project
	 */
	public List<Product> getProductList() {
		return productList;
	}

	/**
	 * @param productList
	 *            the list of all products in the project
	 */
	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	/**
	 * @return the list of all folders in the project
	 */
	public List<Folder> getFolderList() {
		return projectFolderList;
	}

	/**
	 * @param folderList
	 *            the list of all folders in the project
	 */
	public void setFolderList(List<Folder> folderList) {
		this.projectFolderList = folderList;
	}

	/**
	 * Converts the project information to a string
	 */
	@Override
	public String toString() {
		return String.format("%d: %s, %s", this.id, this.title, this.description);
	}

	/**
	 * @return the parent
	 */
	public Project getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(Project parent) {
		this.parent = parent;
	}

	/**
	 * @return the updatedparent
	 */
	public boolean isUpdatedparent() {
		return updatedparent;
	}

	/**
	 * @param updatedparent
	 *            the updatedparent to set
	 */
	public void setUpdatedparent(boolean updatedparent) {
		this.updatedparent = updatedparent;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
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
	 * @return the upVote
	 */
	public Integer getUpVote() {
		return upVote;
	}

	/**
	 * @param upVote
	 *            the upVote to set
	 */
	public void setUpVote(Integer upVote) {
		this.upVote = upVote;
	}
	
	/**
	 * Increment upVote by one
	 */
	public void addOneToUpVote() {
		this.upVote++;
	}

	/**
	 * @return the downVote
	 */
	public Integer getDownVote() {
		return downVote;
	}

	/**
	 * @param downVote
	 *            the downVote to set
	 */
	public void setDownVote(Integer downVote) {
		this.downVote = downVote;
	}
	
	/**
	 * Increment downVote by one
	 */
	public void addOneToDownVote() {
		this.downVote++;
	}
	
	/**
	 * @return up voting ratio
	 */
	public String getRate() {
		double rate = 0;
		if ((getDownVote() + getUpVote()) != 0) {
		 rate = getUpVote() * 100 / (getDownVote() + getUpVote());
		}
		return Double.toString(rate);
	}
	
	/**
	 * @param rate
	 * 			the rate to set
	 */
	public void setRate(String rate) {
		this.rate = rate;
	}
	
	/**
	 * @return the ratedUserList
	 */
	public List<User> getRatedUserList() {
		return ratedUserList;
	}
	
	/**
	 * @param ratedUserList
	 * 			the ratedUserList to set
	 */
	public void setRatedUserList(List<User> ratedUserList) {
		this.ratedUserList = ratedUserList;
	}
	

	/**
	 * Adds a rated user to the ratedUserList
	 * 
	 * @param ratedUser
	 */
	public void addRatedUser(User ratedUser) {
		if (ratedUserList == null) {
			this.ratedUserList = new LinkedList<>();
		}
		this.ratedUserList.add(ratedUser);
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
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastChange == null) ? 0 : lastChange.hashCode());
		result = prime * result + ((productList == null) ? 0 : productList.hashCode());
		result = prime * result + ((projectFolderList == null) ? 0 : projectFolderList.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((trackingList == null) ? 0 : trackingList.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (!(obj instanceof Project)) {
			return false;
		}
		Project other = (Project) obj;
		if (this.id == null || other.getId() == null) {
			return false;
		}

		return this.id.equals(other.id);
	}
}
