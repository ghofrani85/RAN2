package com.ghofrani.htw.RAN2.database;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.database.rowmapper.AssetRowMapper;
import com.ghofrani.htw.RAN2.database.rowmapper.FeatureRowMapper;
import com.ghofrani.htw.RAN2.database.rowmapper.ProductRowMapper;
import com.ghofrani.htw.RAN2.database.rowmapper.ProjectRowMapper;
import com.ghofrani.htw.RAN2.database.rowmapper.UserRowMapper;

@Component
public class DBConnector {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private JdbcTemplate jdbc;

	public DBConnector() {
	}

	/**
	 * Setup method for the dummy database.
	 */
	@PostConstruct
	private void setup() {
		// log.info("Creating dummy tables");

		// create dummy tables...
		// createDummyTableProjects();
		// createDummyTableAssets();
		// createDummyTableFeatures();
		// createDummyTableProduct();
		// createDummyTableUsers();
	}

	/**
	 * Creates a dummy table for users.
	 */
	private void createDummyTableUsers() {
		// jdbc.execute("DROP TABLE users IF EXISTS");
		// jdbc.execute("CREATE TABLE users("
		// + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255), login
		// VARCHAR(255))");

		LinkedList<String> userlist = new LinkedList<String>();
		for (int i = 0; i <= 5; i = i + 1) {
			userlist.add(String.format("Username%d Email%d", i + 1, i));
		}

		// Split up the array of whole names into an array of first/last names
		List<Object[]> splitUpNames = userlist.stream().map(name -> name.split(" ")).collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> log.info(String.format("Inserting user record for %s %s", name[0], name[1])));

		// Uses jdbc's batchUpdate operation to bulk load data
		jdbc.batchUpdate("INSERT INTO users(username, email, password, enabled, locked) VALUES (?,?, 'asd', 0, 0)",
				splitUpNames);

		log.info("Querying for user records where username = 'Vorname1':");
		jdbc.query("SELECT id, username, password, enabled, locked, email FROM users WHERE username = ?",
				new Object[] { "Username1" }, new UserRowMapper()).forEach(user -> log.info(user.toString()));
	}

	/**
	 * Creates a dummy table for projects.
	 */
	private void createDummyTableProjects() {
		// jdbc.execute("DROP TABLE projects IF EXISTS");
		// jdbc.execute("CREATE TABLE projects(" + "id SERIAL, title VARCHAR(255),
		// description VARCHAR(255))");

		LinkedList<String> projectlist = new LinkedList<String>();
		for (int i = 0; i <= 5; i = i + 1) {
			projectlist.add(String.format("Projekt%d Beschreibung%d", i + 1, i));
		}

		// Split up the array of whole names into an array of first/last names
		List<Object[]> splitUpNames = projectlist.stream().map(name -> name.split(" ")).collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> log.info(String.format("Inserting project record for %s %s", name[0], name[1])));

		// Uses jdbc's batchUpdate operation to bulk load data
		jdbc.batchUpdate("INSERT INTO projects(title, description, userid) VALUES (?,?,100)", splitUpNames);

		log.info("Querying for project records where title = 'Josh':");
		jdbc.query("SELECT id, title, description FROM projects WHERE title = ?", new Object[] { "Josh" },
				new ProjectRowMapper()).forEach(project -> log.info(project.toString()));
	}

	/**
	 * Creates a dummy table for assets.
	 */
	private void createDummyTableAssets() {
		// jdbc.execute("DROP TABLE assets IF EXISTS");
		// jdbc.execute("CREATE TABLE assets(" + "id SERIAL, title VARCHAR(255),
		// description VARCHAR(255))");

		LinkedList<String> assetlist = new LinkedList<String>();
		for (int i = 0; i <= 50; i = i + 1) {
			assetlist.add(String.format("Asset%d This_is_the_description_of_the_asset_number_%d", i, i));
		}

		// Split up the array of whole names into an array of first/last names
		List<Object[]> splitUpNames = assetlist.stream().map(name -> name.split(" ")).collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> log.info(String.format("Inserting asset record for %s %s", name[0], name[1])));

		// Uses jdbc's batchUpdate operation to bulk load data
		jdbc.batchUpdate("INSERT INTO assets(title, description) VALUES (?,?)", splitUpNames);

		log.info("Querying for asset records where title = 'Josh':");
		jdbc.query("SELECT id, title, description FROM assets WHERE title = ?", new Object[] { "Josh" },
				new AssetRowMapper()).forEach(asset -> log.info(asset.toString()));
	}

	/**
	 * Creates a dummy table for features.
	 */
	private void createDummyTableFeatures() {
		// jdbc.execute("DROP TABLE features IF EXISTS");
		// jdbc.execute("CREATE TABLE features(" + "id SERIAL, title VARCHAR(255),
		// description VARCHAR(255))");

		LinkedList<String> featurelist = new LinkedList<String>();
		for (int i = 0; i <= 50; i = i + 1) {
			featurelist.add(String.format("Feature%d Beschreibung%d", i, i));
		}

		// Split up the array of whole names into an array of first/last names
		List<Object[]> splitUpNames = featurelist.stream().map(name -> name.split(" ")).collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> log.info(String.format("Inserting feature record for %s %s", name[0], name[1])));

		// Uses jdbc's batchUpdate operation to bulk load data
		jdbc.batchUpdate("INSERT INTO features(title, description) VALUES (?,?)", splitUpNames);

		log.info("Querying for feature records where title = 'Josh':");
		jdbc.query("SELECT id, title, description FROM features WHERE title = ?", new Object[] { "Josh" },
				new FeatureRowMapper()).forEach(feature -> log.info(feature.toString()));
	}

	/**
	 * Creates a dummy table for products.
	 */
	private void createDummyTableProduct() {
		// jdbc.execute("DROP TABLE products IF EXISTS");
		// jdbc.execute("CREATE TABLE products(" + "id SERIAL, title VARCHAR(255),
		// description VARCHAR(255))");

		LinkedList<String> productlist = new LinkedList<String>();
		for (int i = 0; i <= 50; i = i + 1) {
			productlist.add(String.format("Produkt%d Beschreibung%d", i, i));
		}

		// Split up the array of whole names into an array of first/last names
		List<Object[]> splitUpNames = productlist.stream().map(name -> name.split(" ")).collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> log.info(String.format("Inserting products record for %s %s", name[0], name[1])));

		// Uses jdbc's batchUpdate operation to bulk load data
		jdbc.batchUpdate("INSERT INTO products(title, description) VALUES (?,?)", splitUpNames);

		log.info("Querying for product records where title = 'Josh':");
		jdbc.query("SELECT id, title, description FROM products WHERE title = ?", new Object[] { "Josh" },
				new ProductRowMapper()).forEach(asset -> log.info(asset.toString()));
	}
}
