package com.ghofrani.htw.RAN2.controller.service;

import java.util.List;

import com.ghofrani.htw.RAN2.controller.error.DatabaseException;
import com.ghofrani.htw.RAN2.controller.error.SameUserException;
import com.ghofrani.htw.RAN2.model.Project;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Provides an interface for handling interactions with the project database.
 * 
 * @author Daniel Wahlmann
 *
 */
public interface IProjectService {

	/**
	 * Loads the Project with the given id.
	 * 
	 * @param id the id of the project to load
	 * @return The Project loaded
	 */
	Project loadProject(Integer id);

	/**
	 * Saves the Project to the database.
	 * 
	 * @param project The project to save
	 * @throws DatabaseException When the database encountered an error
	 * @return saved Project Object
	 */
	Project saveProject(Project project) throws DatabaseException;

	/**
	 * Copies the given Project to a different user.
	 * 
	 * @param oldId ID of parentProject
	 * @param newTitle Title of new Project
	 * @param newDescription New description of copy
	 * @param newUser User that copied
	 * @return the new project
	 * @throws SameUserException When user tried to copy from himself
	 */
	Project copyProject(Integer oldId, String newTitle, String newDescription, User newUser) throws SameUserException;

	/**
	 * Wrapper for selectProjectsByUserID, returns a List with all Projects by a
	 * given User
	 * 
	 * @author Jannik Gröger
	 * @param user
	 *            The user of which the projects are returned
	 * @return List of projects of User user
	 */
	List<Project> loadAllProjectsOfUser(User user);
	
	List<Project> loadAllRatedProjectsOfUser(User user);

	/**
	 * Notifies all children of the project by setting the updatedParent variable
	 * 
	 * @param project The project which was updated
	 * @author Robert Völkner
	 */
	void notifyChildren(Project project);

}
