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
import com.ghofrani.htw.RAN2.controller.service.IFolderService;
import com.ghofrani.htw.RAN2.controller.service.IProductService;
import com.ghofrani.htw.RAN2.controller.transmission.CheckFolder;
import com.ghofrani.htw.RAN2.model.Folder;
import com.ghofrani.htw.RAN2.model.Product;

/**
 * REST controller for initializing the Product Folder matrix and reacting to
 * changes.
 * 
 * @author Daniel Wahlmann
 * @author Tim Gugel
 *
 */
@RestController
public class CheckFolderController {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private IProductService productService;

	@Autowired
	private IFolderService folderService;

	private final AtomicLong counter = new AtomicLong();

	/**
	 * Checks if a Folder is associated with a Product.
	 * 
	 * @param productId
	 *            The id of the Product.
	 * @param folderId
	 *            The id of the Folder.
	 * @return A JSON with true, if the Folder is associated with the Product,
	 *         false otherwise.
	 */
	@PostMapping("/checkfolder")
	@Secured("ROLE_USER")
	public CheckFolder checkFolder(@RequestParam(value = "p") Integer productId,
			@RequestParam(value = "f") Integer folderId) {

		log.debug("Called checkFolder with productId={} folderId={}", productId, folderId);

		Product product = productService.loadProduct(productId);
		Folder folder = folderService.loadFolder(folderId);

		String s = "false";

		if (product.getFolderList().contains(folder)) {
			s = "true";
		}

		return new CheckFolder(counter.incrementAndGet(), s);
	}

	/**
	 * Adds a Folder to a Product.
	 * 
	 * @param productId
	 *            The id of the Product.
	 * @param folderId
	 *            The id of the Folder.
	 * @return a CheckFolder Object
	 * 
	 */
	@PostMapping("/addfolder")
	@Secured("ROLE_USER")
	public CheckFolder addFolder(@RequestParam(value = "p") Integer productId,
			@RequestParam(value = "f") Integer folderId) {

		log.debug("Called addFolder with productId={} folderId={}", productId, folderId);

		Product product = productService.loadProduct(productId);
		Folder folder = folderService.loadFolder(folderId);

		product.addFolder(folder);
		productService.saveProduct(product);

		return new CheckFolder(counter.incrementAndGet(), "true");
	}

	/**
	 * Removes a Folder from a Product.
	 * 
	 * @param productId
	 *            The id of the Product.
	 * @param folderId
	 *            The id of the Folder.
	 * @return a CheckFolder Object
	 */
	@PostMapping("/removefolder")
	@Secured("ROLE_USER")
	public CheckFolder removeFolder(@RequestParam(value = "p") Integer productId,
			@RequestParam(value = "f") Integer folderId) {

		log.debug("Called removeFolder with productId={} folderId={}", productId, folderId);

		Product product = productService.loadProduct(productId);
		Folder folder = folderService.loadFolder(folderId);

		product.removeFolder(folder);
		productService.saveProduct(product);

		return new CheckFolder(counter.incrementAndGet(), "false");
	}
}
