package com.ghofrani.htw.RAN2.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ghofrani.htw.RAN2.controller.error.DatabaseException;
import com.ghofrani.htw.RAN2.database.ProductAccess;
import com.ghofrani.htw.RAN2.model.Product;

/**
 * Implementation of IProductService for use with the ProductAccess database
 * interface.
 * 
 * @author Daniel Wahlmann
 *
 */
@Service
public class ProductService implements IProductService {

	@Autowired
	private ProductAccess productAccess;

	/**
	 * Loads the product with the given id.
	 * 
	 * @param id ID of the product to be loaded
	 * @return the loaded Product
	 */
	@Override
	public Product loadProduct(Integer id) {
		return productAccess.selectProductsByID(id);
	}

	/**
	 * Saves the given product to the database.
	 * 
	 * @param product Product to be saved
	 * @throws DatabaseException When the database encountered an error
	 * @return the saved Product
	 */
	@Override
	public Product saveProduct(Product product) throws DatabaseException {
		Product saved = productAccess.saveProduct(product);

		if (saved == null) {
			throw new DatabaseException();
		}

		return saved;
	}

	/**
	 * Deletes the given product from the database.
	 * 
	 * @param product Product to be deleted
	 */
	@Override
	public void deleteProduct(Product product) {
		productAccess.deleteProduct(product);

	}

}
