package com.ghofrani.htw.RAN2.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.setup.ConfigData;
import com.ghofrani.htw.RAN2.controller.transmission.DownloadData;
import com.ghofrani.htw.RAN2.database.ArtefactAccess;
import com.ghofrani.htw.RAN2.database.ProductAccess;
import com.ghofrani.htw.RAN2.model.Artefact;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Feature;
import com.ghofrani.htw.RAN2.model.Product;

/**
 * This controller has all necessary methods to create a .zip file on the
 * server. It generates a .zip file when "/generateZip" is called from the
 * Frontend.
 * 
 * @author Stefan Schmidt
 */
@RestController
public class GenerateZipController {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ProductAccess productAccess;

	@Autowired
	private ArtefactAccess artefactAccess;

	@Autowired
	private EditAssetController editAssetController;

	@Autowired
	private ConfigData configData;
	
	@Autowired
	private MessageSource messages;
	
	@Autowired
	private LocaleResolver resolver;

	private static FileOutputStream fos;
	private static ZipOutputStream zos;

	private final AtomicLong counter = new AtomicLong();

	
	/**
	 * Generates Zip Archive with artefact files for product
	 * @param productId ID of the product for which the zip will be generated
	 * @param request Request Object
	 * @return ErrorHandler for errors or download when successfull
	 * @throws Exception When error are encountered
	 */
	@PostMapping("/generateZip")
	@Secured("ROLE_USER")
	public DownloadData generateZip(@RequestParam(value = "productId") Integer productId, HttpServletRequest request) throws Exception {
		log.info("Called generatZip with productId={}", productId);

		Product product = productAccess.selectProductsByID(productId);
		String path = configData.getTempPath();

		// product has no features
		if (product.getFeatureList().isEmpty()) {
			String message = messages.getMessage("message.emptyProduct", null, resolver.resolveLocale(request));
			return errorHandler(message + product.getTitle(), true, "");
		}
		
		//create directory for the download data
		boolean createFolderSuccessful = createDirectory(path, null, false);
		if (!createFolderSuccessful) {
			log.info("Download Folder couldn't be created.");
			String message = messages.getMessage("message.filesystemError", null, resolver.resolveLocale(request));
			return errorHandler(message, true, "");
		}
		
		
		String productName = editAssetController.createFileName(product.getTitle());
		//create the zip download file
		try {
			createZipFile(productName, path); // create zip file
		} catch (Exception ex) {
			log.info("Zip file couldn't be created. Exception={}", ex.getMessage());
			String message = messages.getMessage("message.filesystemError", null, resolver.resolveLocale(request));
			return errorHandler(message, true, "");
		}

		

		// iterate over feature list of a product
		List<Feature> featureList = product.getFeatureList();
		for (int i = 0; i < featureList.size(); i++) {
			Feature feature = (Feature) featureList.get(i);
			Integer featureID = feature.getId();

			String featureFileName = editAssetController.createFileName(feature.getTitle());
			String featurePath = path + "/" + featureFileName; // folder for a features artefacts
			File featureDirectory = new File(featurePath);

			//create a directory for feature
			boolean createFeatureFolderSuccessful = createDirectory(featurePath, featureDirectory, true);
			if (!createFeatureFolderSuccessful) {
				log.info("Feature Folder couldn't be created.");
				String message = messages.getMessage("message.filesystemError", null, resolver.resolveLocale(request));
				return errorHandler(message, true, "");
			}

			
			//feature has artefacts
			if (!artefactAccess.selectArtefactsByFeatureID(featureID).isEmpty()) {
				List<Artefact> artefactList = artefactAccess.selectArtefactsByFeatureID(feature.getId());

				//create the artefacts
				try {
					createArtefacts(featurePath, artefactList);
				} catch (Exception ex) {
					log.info("Create Artefacts fails. Exception={}", ex.getMessage());
					String message = messages.getMessage("message.filesystemError", null, resolver.resolveLocale(request));
					return errorHandler(message, true, "");
				}
				
				//add the feature folder to the zip file
				try {
					addFileToZip(featureDirectory, featureFileName, zos); // adds feature folder to zip archive
				} catch (Exception ex) {
					log.info("File couldn't be added to zip. Exception={}", ex.getMessage());
					String message = messages.getMessage("message.filesystemError", null, resolver.resolveLocale(request));
					return errorHandler(message, true, "");
				}

			} else {
				String message = messages.getMessage("message.emptyFeature", null, resolver.resolveLocale(request));
				return errorHandler(message + feature.getTitle(), true, "");
			}
		}

		String URL = path + "/"; // path

		zos.close(); // closes the zipOutputStream
		fos.close(); // closes the FileOutputStream after all files have been added to zip archive
		
		String message = messages.getMessage("message.successfulDownload", null, request.getLocale());
		return errorHandler(message, false, URL);
	}

	/**
	 * Creates a new directory
	 * @param path Path where directory should be created
	 * @param featureDirectory directory to be created
	 * @param isFeatureDirectory if directory is supposed to be for a feature
	 * @return true if success, else false
	 */
	public boolean createDirectory(String path, File featureDirectory, boolean isFeatureDirectory) {
		log.info("called createDirectory with path={}, featureDirectory={}, isFeatureDirectory={}",path, featureDirectory, isFeatureDirectory );
		Path pa = Paths.get(path);
		if (!pa.toFile().exists()) {
			return (new File(path).mkdir()); // create directory
		} else {
			if (isFeatureDirectory) {
				editAssetController.deleteFiles(featureDirectory);
				featureDirectory = new File(path);
				return (featureDirectory.mkdir());
			} else {
				return true;
			}

		}

	}

	/**
	 * Method calls the correct artefact editor depending on the asset type
	 * @param featurePath Path to the folder where file lies
	 * @param artefactList List of artefacts to be edited
	 * @throws IOException When file could not be read
	 * @throws SQLException When database encountered an error
	 */
	public void createArtefacts(String featurePath, List<Artefact> artefactList) throws IOException, SQLException {
		// iterate over artefact list of a feature
		log.info("called createDirectory with featurePath={}, artefactList={}",featurePath, artefactList);
		
		for (int j = 0; j < artefactList.size(); j++) {
			Artefact artefact = (Artefact) artefactList.get(j);
			Asset asset = artefact.getAsset();

			switch (asset.getType()) {
			case TEXT:
				try {
					editAssetController.createTextArtefactData(artefact, featurePath);
				}catch(Exception ex) {
					log.info("CreateTextArtefact Exception={}", ex.getMessage());
				}
				break;
			case XML:
				try {
					editAssetController.createXMLArtefactData(artefact, featurePath);
				}catch(Exception ex) {
					log.info("CreateXMLArtefact Exception={}", ex.getMessage());
				}
				break;
			case PICTURE:
				try {
					editAssetController.createPictureArtefactData(artefact, featurePath);
				}catch(Exception ex) {
					log.info("CreatePictureArtefact Exception={}", ex.getMessage());
				}
				break;
			case AUDIO:
				try {
					editAssetController.createAudioArtefactData(artefact, featurePath);
				}catch(Exception ex) {
					log.info("CreateAudioArtefact Exception={}", ex.getMessage());
				}
				break;
			case VIDEO:
				try {
					editAssetController.createVideoArtefactData(artefact, featurePath);
				}catch(Exception ex) {
					log.info("CreateVideoArtefact Exception={}", ex.getMessage());
				}
				break;
			case OTHER:
				try {
					editAssetController.createOtherArtefactData(artefact, featurePath);
				}catch(Exception ex) {
					log.info("CreateOtherArtefact Exception={}", ex.getMessage());
				}
				break;
			default:
				errorHandler("Unknown filetype", true, "");
			}

		}

	}

	/**
	 * The method adds a file to the zip archive
	 * 
	 * @param file
	 *            the file to be added to the zip archive
	 * @param path
	 *            path of the file to be added to zip archive
	 * @param zos
	 *            ZipOutputStream
	 * @author Vivian Holzapfel
	 * @throws Exception When en error occured during process
	 */

	public static void addFileToZip(File file, String path, ZipOutputStream zos) throws Exception {

		log.info("Called addToZipFile with file={}, fileName={}, zipOutputStream={} ", file, path, zos);

		if (file.isDirectory()) {
			File[] listOfFiles = file.listFiles();
			if (listOfFiles != null) {
				for (File f : listOfFiles) {
					addFileToZip(f, path, zos);
				}
			}

		} else {
			FileInputStream fis = new FileInputStream(file);
			try {
				//String fileName = editAssetController.createFileName(file.getName()); CHANGE
				String fileName = file.getName();
				ZipEntry zipEntry = new ZipEntry(path + "/" + fileName);
				zos.putNextEntry(zipEntry);
				byte[] bytes = new byte[1024];
				int length;
				while ((length = fis.read(bytes)) >= 0) {
					zos.write(bytes, 0, length);
				}

				zos.closeEntry();
				fis.close();
			} catch (Exception ex) {
				throw new Exception("FileInputStreamError");
			} finally {
				zos.closeEntry();
				fis.close();
			}
		}

	}

	/**
	 * The method creates the zip archive
	 * 
	 * @param productName Name of product that the zip file represented
	 * @param path Path where zipfile will be created
	 * @throws FileNotFoundException When Zip could not be generated
	 * @author Vivian Holzapfel
	 */

	public static void createZipFile(String productName, String path) throws FileNotFoundException {
		log.info("Called createZipFile with productName={}", productName);

		
		fos = new FileOutputStream(path + "/" + productName + ".zip");
		zos = new ZipOutputStream(fos);
	}

	
	/**
	 * sends a new error message to the frontend
	 * @param errorMessage Message for the User
	 * @param error true for actual error, false for warning
	 * @param URL the URL of the download
	 * @return DownloadData Object handling the error
	 */
	public DownloadData errorHandler(String errorMessage, boolean error, String URL) {
		return new DownloadData(counter.incrementAndGet(), error, errorMessage, URL);

	}
}
