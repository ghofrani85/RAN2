package com.ghofrani.htw.RAN2.controller;

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
import com.ghofrani.htw.RAN2.database.FileAccess;
import com.ghofrani.htw.RAN2.database.ProductAccess;
import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.Folder;
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
	private FileAccess fileAccess;

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
	 * Generates Zip Archive with file files for product
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

		// product has no folders
		if (product.getFolderList().isEmpty()) {
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

		

		// iterate over folder list of a product
		List<Folder> folderList = product.getFolderList();
		for (int i = 0; i < folderList.size(); i++) {
			Folder folder = (Folder) folderList.get(i);
			Integer folderID = folder.getId();

			String folderFileName = editAssetController.createFileName(folder.getTitle());
			String folderPath = path + "/" + folderFileName; // folder for a folders files
			java.io.File folderDirectory = new java.io.File(folderPath);

			//create a directory for folder
			boolean createFolderFolderSuccessful = createDirectory(folderPath, folderDirectory, true);
			if (!createFolderFolderSuccessful) {
				log.info("Folder Folder couldn't be created.");
				String message = messages.getMessage("message.filesystemError", null, resolver.resolveLocale(request));
				return errorHandler(message, true, "");
			}

			
			//folder has files
			if (!fileAccess.selectFilesByFolderID(folderID).isEmpty()) {
				List<File> fileList = fileAccess.selectFilesByFolderID(folder.getId());

				//create the files
				try {
					createFiles(folderPath, fileList);
				} catch (Exception ex) {
					log.info("Create Files fails. Exception={}", ex.getMessage());
					String message = messages.getMessage("message.filesystemError", null, resolver.resolveLocale(request));
					return errorHandler(message, true, "");
				}
				
				//add the folder folder to the zip file
				try {
					addFileToZip(folderDirectory, folderFileName, zos); // adds folder folder to zip archive
				} catch (Exception ex) {
					log.info("File couldn't be added to zip. Exception={}", ex.getMessage());
					String message = messages.getMessage("message.filesystemError", null, resolver.resolveLocale(request));
					return errorHandler(message, true, "");
				}

			} else {
				String message = messages.getMessage("message.emptyFolder", null, resolver.resolveLocale(request));
				return errorHandler(message + folder.getTitle(), true, "");
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
	 * @param folderDirectory directory to be created
	 * @param isFolderDirectory if directory is supposed to be for a folder
	 * @return true if success, else false
	 */
	public boolean createDirectory(String path, java.io.File folderDirectory, boolean isFolderDirectory) {
		log.info("called createDirectory with path={}, folderDirectory={}, isFolderDirectory={}",path, folderDirectory, isFolderDirectory );
		Path pa = Paths.get(path);
		if (!pa.toFile().exists()) {
			return (new java.io.File(path).mkdir()); // create directory
		} else {
			if (isFolderDirectory) {
				editAssetController.deleteFiles(folderDirectory);
				folderDirectory = new java.io.File(path);
				return (folderDirectory.mkdir());
			} else {
				return true;
			}

		}

	}

	/**
	 * Method calls the correct file editor depending on the asset type
	 * @param folderPath Path to the folder where file lies
	 * @param fileList List of files to be edited
	 * @throws IOException When file could not be read
	 * @throws SQLException When database encountered an error
	 */
	public void createFiles(String folderPath, List<File> fileList) throws IOException, SQLException {
		// iterate over file list of a folder
		log.info("called createDirectory with folderPath={}, fileList={}",folderPath, fileList);
		
		for (int j = 0; j < fileList.size(); j++) {
			File file = (File) fileList.get(j);
			Asset asset = file.getAsset();

			switch (asset.getType()) {
			case TEXT:
				try {
					editAssetController.createTextFileData(file, folderPath);
				}catch(Exception ex) {
					log.info("CreateTextFile Exception={}", ex.getMessage());
				}
				break;
			case XML:
				try {
					editAssetController.createXMLFileData(file, folderPath);
				}catch(Exception ex) {
					log.info("CreateXMLFile Exception={}", ex.getMessage());
				}
				break;
			case PICTURE:
				try {
					editAssetController.createPictureFileData(file, folderPath);
				}catch(Exception ex) {
					log.info("CreatePictureFile Exception={}", ex.getMessage());
				}
				break;
			case AUDIO:
				try {
					editAssetController.createAudioFileData(file, folderPath);
				}catch(Exception ex) {
					log.info("CreateAudioFile Exception={}", ex.getMessage());
				}
				break;
			case VIDEO:
				try {
					editAssetController.createVideoFileData(file, folderPath);
				}catch(Exception ex) {
					log.info("CreateVideoFile Exception={}", ex.getMessage());
				}
				break;
			case OTHER:
				try {
					editAssetController.createOtherFileData(file, folderPath);
				}catch(Exception ex) {
					log.info("CreateOtherFile Exception={}", ex.getMessage());
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

	public static void addFileToZip(java.io.File file, String path, ZipOutputStream zos) throws Exception {

		log.info("Called addToZipFile with file={}, fileName={}, zipOutputStream={} ", file, path, zos);

		if (file.isDirectory()) {
			java.io.File[] listOfFiles = file.listFiles();
			if (listOfFiles != null) {
				for (java.io.File f : listOfFiles) {
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
