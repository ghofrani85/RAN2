package com.ghofrani.htw.RAN2.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.database.helper.TrackingType;
import com.ghofrani.htw.RAN2.database.rowmapper.FolderRowMapper;
import com.ghofrani.htw.RAN2.database.rowmapper.ProductRowMapper;
import com.ghofrani.htw.RAN2.model.Product;

/**
 * Class to handle product interaction with the database.
 * 
 * @author Tobias Powelske
 *
 */
@Component
public class ProductAccess {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private static final String SELECT_SQL = "SELECT id, title, description FROM products";
	private static final String INSERT_SQL = "INSERT INTO products(title, description) VALUES(?, ?)";
	private static final String UPDATE_SQL = "UPDATE products SET title = ?, description = ? WHERE id = ?";
	private static final String DELETE_SQL = "DELETE FROM products WHERE id = ?";
	private static final String INSERT_PRODUCTSXFOLDERS_SQL = "INSERT INTO productsxfolders(productid, folderid) VALUES(?, ?)";
	private static final String DELETE_PRODUCTSXFOLDERS_SQL = "DELETE FROM productsxfolders WHERE productid = ?";
	private static final String DELETE_PROJECTSXPRODUCTS_SQL = "DELETE FROM projectsxproducts WHERE productid = ?";
	private static final String SELECT_PRODUCTSXFOLDERS_SQL = "SELECT folders.id, title, description, parentid, updatedparent"
			+ " FROM productsxfolders JOIN folders ON folderid = folders.id";

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private TrackingAccess trackacc;

	/**
	 * Prevent instances of the class.
	 */
	private ProductAccess() {
	}

	/**
	 * Returns a list of all products.
	 * 
	 * @return list of all products
	 */
	public List<Product> selectProducts() {
		LinkedList<Product> result = new LinkedList<Product>();

		log.debug("Getting products...");

		jdbc.query(SELECT_SQL, new ProductRowMapper()).forEach(Product -> {
			log.debug(Product.toString());
			result.add(Product);
		});

		// load tracking...
		result.forEach(pro -> pro.setTrackingList(trackacc.selectTrackingByItemID(pro.getId(), TrackingType.PRODUCT)));

		return result;
	}

	/**
	 * Returns the product with the given id.
	 * 
	 * @param id
	 *            id of the product to be retrieved
	 * @return the retrieved product
	 */
	public Product selectProductsByID(int id) {
		Product result = null;

		log.debug(String.format("Getting product with ID %d", id));

		List<Product> resultlist = jdbc.query(SELECT_SQL + " WHERE id = ?", new Object[] { id },
				new ProductRowMapper());

		if (!resultlist.isEmpty()) {
			result = resultlist.get(0);

			selectProductsXFolders(result);
		}

		if (result != null) {
			// load tracking...
			result.setTrackingList(trackacc.selectTrackingByItemID(result.getId(), TrackingType.PRODUCT));
		}

		return result;
	}

	/**
	 * Fills the folderList of the given product model.
	 * 
	 * @param product
	 *            the product to get the folderList for
	 */
	private void selectProductsXFolders(Product product) {
		log.debug(String.format("Getting folders to product %d", product.getId()));

		jdbc.query(SELECT_PRODUCTSXFOLDERS_SQL + " WHERE productid = ?", new Object[] { product.getId() },
				new FolderRowMapper()).forEach(folder -> {
					product.addFolder(folder);
				});
	}

	/**
	 * Inserts a new product, if id is null or updates an existing one.
	 * 
	 * @param product
	 *            the product to be inserted or updated
	 * @return the updated product model
	 */
	public Product saveProduct(Product product) {
		Product result = product;

		if (product.getId() == null) {
			result = insertProduct(product);

			final int id = result.getId();
			result.getTrackingList().forEach(tra -> tra.setItemid(id));
		} else {
			result = updateProduct(product);
		}

		// save the folders for the product...
		saveProductsXFolders(result);

		// save tracking...
		trackacc.saveTracking(result.getTrackingList());

		return result;
	}

	/**
	 * Inserts a new product.
	 * 
	 * @param product
	 *            the product to be inserted
	 * @return the updated product model
	 */
	public Product insertProduct(Product product) {
		KeyHolder holder = new GeneratedKeyHolder();

		jdbc.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, product.getTitle());
				ps.setString(2, product.getDescription());
				return ps;
			}
		}, holder);

		int newId;
		if (holder.getKeys().size() > 1) {
			newId = (int) holder.getKeys().get("id");
		} else {
			newId = holder.getKey().intValue();
		}
		product.setId(newId);

		return product;
	}

	/**
	 * Updates the given product.
	 * 
	 * @param product
	 *            the product to be updated
	 * @return the updated product model
	 */
	public Product updateProduct(Product product) {
		jdbc.update(UPDATE_SQL, product.getTitle(), product.getDescription(), product.getId());

		return product;
	}

	/**
	 * Inserts the folders in the given products folderList into the database.
	 * 
	 * @param product
	 *            the product whose folderList entries should be saved
	 */
	private void saveProductsXFolders(Product product) {
		// delete every productxfolder connection beforehand...
		jdbc.update(DELETE_PRODUCTSXFOLDERS_SQL, product.getId());

		// redo connections...
		product.getFolderList()
				.forEach(feat -> jdbc.update(INSERT_PRODUCTSXFOLDERS_SQL, product.getId(), feat.getId()));
	}

	/**
	 * Deletes the given product and the connection to a project.
	 * 
	 * @param product
	 *            the product to be deleted
	 * @return true, if successful, false, if not
	 */
	public boolean deleteProduct(Product product) {
		// delete every projectxproduct connection...
		jdbc.update(DELETE_PROJECTSXPRODUCTS_SQL, product.getId());

		// delete every productxfolder connection...
		jdbc.update(DELETE_PRODUCTSXFOLDERS_SQL, product.getId());

		// delete tracking...
		trackacc.deleteTracking(product.getId(), TrackingType.PRODUCT);

		// delete the product...
		int resultcount = jdbc.update(DELETE_SQL, product.getId());

		if (resultcount >= 1) {
			return true;
		}

		return false;
	}
}
