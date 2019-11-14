package com.ghofrani.htw.RAN2.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ghofrani.htw.RAN2.model.Product;

/**
 * RowMapper for the product model.
 * 
 * @author Tobias Powelske
 *
 */
public class ProductRowMapper implements RowMapper<Product> {

	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		Product product = new Product();

		product.setId(rs.getInt("id"));
		product.setTitle(rs.getString("title"));
		product.setDescription(rs.getString("description"));

		return product;
	}

}
