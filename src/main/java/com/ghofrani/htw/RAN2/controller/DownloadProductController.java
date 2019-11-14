package com.ghofrani.htw.RAN2.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.database.ProductAccess;
import com.ghofrani.htw.RAN2.model.Product;

/**
 * @author Stefan Schmidt
 * @author Vivian Holzapfel
 *
 */
@Controller
public class DownloadProductController {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ProductAccess productAccess;
	
	@Autowired
	EditAssetController editAssetController;

	@GetMapping("/downloadProduct")
	@Secured("ROLE_USER")
	public void downloadProduct(@RequestParam(value = "productId") Integer productId,
			@RequestParam(value = "path") String path, HttpServletResponse response) throws IOException {

		log.info("Called downloadProduct with path={}, productId={}", path, productId);
		Product product = productAccess.selectProductsByID(productId);
		String fileName = editAssetController.createFileName(product.getTitle()) + ".zip";
		File file = new File(path + fileName);

		String mimeType = URLConnection.guessContentTypeFromName(fileName);
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}

		response.setContentType(mimeType);

		/*
		 * "Content-Disposition : inline" will show viewable types [like
		 * images/text/pdf/anything viewable by browser] right on browser while
		 * others(zip e.g) will be directly downloaded [may provide save as popup, based
		 * on your browser setting.]
		 */
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

		/*
		 * "Content-Disposition : attachment" will be directly download, may provide
		 * save as popup, based on your browser setting
		 */
		// response.setHeader("Content-Disposition", String.format("attachment;
		// filename=\"%s\"", file.getName()));

		response.setContentLength((int) file.length());

		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

		// Copy bytes from source to destination(outputstream in this example), closes
		// both streams.
		FileCopyUtils.copy(inputStream, response.getOutputStream());
	}

}
