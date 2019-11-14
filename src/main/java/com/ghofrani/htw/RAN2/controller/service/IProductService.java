package com.ghofrani.htw.RAN2.controller.service;

import com.ghofrani.htw.RAN2.controller.error.DatabaseException;
import com.ghofrani.htw.RAN2.model.Product;

/**
 * Provides an interface for handling interactions with the product database.
 * 
 * @author Daniel Wahlmann
 *
 */
public interface IProductService {

	/**
	 * Loads the product with the given id.
	 * 
	 * @param id ID of the product to be loaded
	 * @return the loaded Product
	 */
	Product loadProduct(Integer id);

	/**
	 * Saves the given product to the database.
	 * 
	 * @param product Product to be saved
	 * @throws DatabaseException When the database encountered an error
	 * @return the saved Product
	 */
	Product saveProduct(Product product) throws DatabaseException;

	/**
	 * Deletes the given product from the database.
	 * 
	 * @param product Product to be deleted
	 */
	void deleteProduct(Product product);

}
