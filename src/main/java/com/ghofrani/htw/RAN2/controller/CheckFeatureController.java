package com.ghofrani.htw.RAN2.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.service.IFeatureService;
import com.ghofrani.htw.RAN2.controller.service.IProductService;
import com.ghofrani.htw.RAN2.controller.transmission.CheckFeature;
import com.ghofrani.htw.RAN2.model.Feature;
import com.ghofrani.htw.RAN2.model.Product;

/**
 * REST controller for initializing the Product Feature matrix and reacting to
 * changes.
 * 
 * @author Daniel Wahlmann
 * @author Tim Gugel
 *
 */
@RestController
public class CheckFeatureController {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private IProductService productService;

	@Autowired
	private IFeatureService featureService;

	private final AtomicLong counter = new AtomicLong();

	/**
	 * Checks if a Feature is associated with a Product.
	 * 
	 * @param productId
	 *            The id of the Product.
	 * @param featureId
	 *            The id of the Feature.
	 * @return A JSON with true, if the Feature is associated with the Product,
	 *         false otherwise.
	 */
	@PostMapping("/checkfeature")
	@Secured("ROLE_USER")
	public CheckFeature checkFeature(@RequestParam(value = "p") Integer productId,
			@RequestParam(value = "f") Integer featureId) {

		log.debug("Called checkFeature with productId={} featureId={}", productId, featureId);

		Product product = productService.loadProduct(productId);
		Feature feature = featureService.loadFeature(featureId);

		String s = "false";

		if (product.getFeatureList().contains(feature)) {
			s = "true";
		}

		return new CheckFeature(counter.incrementAndGet(), s);
	}

	/**
	 * Adds a Feature to a Product.
	 * 
	 * @param productId
	 *            The id of the Product.
	 * @param featureId
	 *            The id of the Feature.
	 * @return a CheckFeature Object
	 * 
	 */
	@PostMapping("/addfeature")
	@Secured("ROLE_USER")
	public CheckFeature addFeature(@RequestParam(value = "p") Integer productId,
			@RequestParam(value = "f") Integer featureId) {

		log.debug("Called addFeature with productId={} featureId={}", productId, featureId);

		Product product = productService.loadProduct(productId);
		Feature feature = featureService.loadFeature(featureId);

		product.addFeature(feature);
		productService.saveProduct(product);

		return new CheckFeature(counter.incrementAndGet(), "true");
	}

	/**
	 * Removes a Feature from a Product.
	 * 
	 * @param productId
	 *            The id of the Product.
	 * @param featureId
	 *            The id of the Feature.
	 * @return a CheckFeature Object
	 */
	@PostMapping("/removefeature")
	@Secured("ROLE_USER")
	public CheckFeature removeFeature(@RequestParam(value = "p") Integer productId,
			@RequestParam(value = "f") Integer featureId) {

		log.debug("Called removeFeature with productId={} featureId={}", productId, featureId);

		Product product = productService.loadProduct(productId);
		Feature feature = featureService.loadFeature(featureId);

		product.removeFeature(feature);
		productService.saveProduct(product);

		return new CheckFeature(counter.incrementAndGet(), "false");
	}
}
