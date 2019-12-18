package com.ghofrani.htw.RAN2.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.setup.ConfigData;
import com.ghofrani.htw.RAN2.model.File;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.AudioFile;
import com.ghofrani.htw.RAN2.model.OtherFile;
import com.ghofrani.htw.RAN2.model.PictureFile;
import com.ghofrani.htw.RAN2.model.TextFile;
import com.ghofrani.htw.RAN2.model.VideoFile;
import com.ghofrani.htw.RAN2.model.XMLFile;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

/**
 * Controller for editing assets to become files
 * 
 * @author Vivian Holzapfel
 *
 */

@Component
public class EditAssetController {

	@Autowired
	private ConfigData configData;

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	/**
	 * 
	 * Creates a file for the other File type, file formats, which aren't
	 * supported by the program
	 * 
	 * @param file
	 *            the file to be edited
	 * @param folderPath
	 *            the path to the folder folder, where the file will be saved
	 * @throws IOException
	 *             is thrown when there was an error when reading a file
	 * 
	 * @author Vivian Holzapfel
	 */
	public void createOtherFileData(File file, String folderPath) throws IOException {
		log.info("Called createOtherFileData with file={}, folderPath={} ", file, folderPath);

		OtherFile otherFile = (OtherFile) file;

		Asset asset = otherFile.getAsset();
		InputStream input = asset.getURLResource().getInputStream();
		String format = asset.getMetadata().get("FileFormat");
		String fileName = otherFile.getTitle() + "." + format;
		fileName = createFileName(fileName);

		java.io.File targetFile = new java.io.File(folderPath + "/" + fileName);
		FileUtils.copyInputStreamToFile(input, targetFile);

	}

	/**
	 * The Method edits a TextFile and writes the new String into a file
	 * 
	 * @param file
	 *            File to be edited
	 * @param folderPath
	 *            Path to the folderfolder
	 * @throws SQLException
	 *             When there was an database error
	 * @throws IOException
	 *             When files couldn't be created
	 * @author Vivian Holzapfel
	 */
	public void createTextFileData(File file, String folderPath) throws SQLException, IOException {
		log.info("Called createTextFileData with file={}, path={} ", file, folderPath);

		TextFile textFile = (TextFile) file;

		int start;
		int end;

		Asset asset = textFile.getAsset();
		InputStream input = asset.getURLResource().getInputStream();

		String content = new BufferedReader(new InputStreamReader(input)).lines().parallel()
				.collect(Collectors.joining("\n"));

		String editedContent = content;
		if (!textFile.getStart().equals("") || !textFile.getEnd().equals("")) {
			start = Integer.parseInt(textFile.getStart());
			end = Integer.parseInt(textFile.getEnd());
			editedContent = content.substring(start, end); // edit the text
		}

		String format = asset.getMetadata().get("FileFormat");
		String fileName = textFile.getTitle() + "." + format;
		fileName = createFileName(fileName);

		Files.write(Paths.get(folderPath + "/" + fileName), editedContent.getBytes()); // write into file
	}

	/**
	 * The Method edits a XMLFile and writes the new xml into a file
	 * 
	 * @param file
	 *            the file that needs to be edited
	 * @param folderPath
	 *            the path where the file will be saved
	 * @throws SQLException
	 *             When there was an database error
	 * @throws IOException
	 *             When files couldn't be created
	 * @author Robert Völkner
	 */
	public void createXMLFileData(File file, String folderPath) throws IOException, SQLException {

		log.info("Called createXMLFileData with file={}, path={} ", file, folderPath);

		int[] nodes;
		XMLFile xmlFile = (XMLFile) file;
		if (xmlFile.getNodes().length() == 0) {
			nodes = new int[] { 1 };
		} else {
			String[] stringNodes = xmlFile.getNodes().split(",");
			nodes = new int[stringNodes.length];
			for (int i = 0; i < stringNodes.length; i++) { // fill with by user selected node positions
				nodes[i] = Integer.parseInt(stringNodes[i]);
			}
		}

		Asset asset = xmlFile.getAsset();
		InputStream input = asset.getURLResource().getInputStream();

		String content = new BufferedReader(new InputStreamReader(input)).lines().parallel() // load the file
				.collect(Collectors.joining("\n"));

		List<Integer> posOpenList = new ArrayList<>();
		int lastIndex = 0;
		while (lastIndex != -1) { // add all positions of opening tags of the xml file to the posOpenList list
			int index = content.indexOf('<', lastIndex);

			if (index != -1) {
				if (!content.substring(index + 1, index + 2).equals("/")) {
					posOpenList.add(index);
				}
				lastIndex = index + 1;
			} else {
				lastIndex = index;
			}
		}

		List<String> subNodes = new ArrayList<>();

		for (int i = 0; i < nodes.length; i++) { // add all by user selected nodes with subnodes to the subNodes List
			int openIndex = posOpenList.get(nodes[i]);
			int closeIndex = content.indexOf('>', openIndex);
			String tag = content.substring(openIndex + 1, closeIndex);
			String[] tagParts = tag.split(" ");
			int closeTagIndex = -1;

			lastIndex = openIndex;
			List<Integer> posCloseList = new ArrayList<>();

			while (lastIndex != -1) { // add all positions of ending tags equivalent to the tag to posCloseList
				int index = content.indexOf("</" + tagParts[0] + ">", lastIndex);

				if (index != -1) {
					posCloseList.add(index);
					lastIndex = index + 1;
				} else {
					lastIndex = index;
				}
			}

			for (int j = 0; j < posCloseList.size(); j++) { // filter for the correct closing tag in case of subnodes
															// with the same tag name
				int countTags = 0;
				for (int k = nodes[i] + 1; k < posOpenList.size(); k++) {
					int openIndexTemp = posOpenList.get(k);
					int closeIndexTemp = content.indexOf('>', openIndexTemp);
					String tagTemp = content.substring(openIndexTemp + 1, closeIndexTemp);
					String[] tagPartsTemp = tagTemp.split(" ");

					if (tagPartsTemp[0].equals(tagParts[0]) && openIndexTemp < posCloseList.get(j)) { // check for same
																										// name and
																										// position
																										// before
																										// closing tag
						countTags++;
					}
				}

				if (countTags == j) { // check if the correct tag is found
					closeTagIndex = posCloseList.get(j) + tagParts[0].length() + 2;
					break;
				}

			}

			if (closeTagIndex == -1) { // Syntax error check
				subNodes.add("Unable to add node " + nodes[i] + " because of syntax error in original xml!");
			} else {
				subNodes.add(content.substring(openIndex, closeTagIndex + 1));
			}

		}

		String editedContent = content.substring(posOpenList.get(0), content.indexOf('>', posOpenList.get(0)) + 1); // get
																													// the
																													// xml
																													// header
		editedContent += "\n" + "<content>";

		for (int i = 0; i < subNodes.size(); i++) { // construct new xml
			editedContent += "\n" + subNodes.get(i);
		}

		editedContent += "\n" + "</content>";
		String fileName = xmlFile.getTitle() + ".xml";
		fileName = createFileName(fileName);

		Files.write(Paths.get(folderPath + "/" + fileName), editedContent.getBytes()); // write into file
	}

	/**
	 * The Method edits a PictureFile and writes the new Image into a file
	 * 
	 * @param file
	 *            the pictureFile to be edited
	 * @param folderPath
	 *            the path to the file
	 * @throws SQLException
	 *             When there was an database error
	 * @throws IOException
	 *             When files couldn't be created
	 * @author Vivian Holzapfel
	 */
	public void createPictureFileData(File file_a, String folderPath) throws SQLException, IOException {
		log.info("Called createPictureFileData with file={}, path={} ", file_a, folderPath);

		PictureFile pictureFile = (PictureFile) file_a;

		int x = Integer.parseInt(pictureFile.getX());
		int y = Integer.parseInt(pictureFile.getY());
		int width = Integer.parseInt(pictureFile.getWidth());
		int height = Integer.parseInt(pictureFile.getHeight());

		Asset asset = pictureFile.getAsset();
		InputStream input = asset.getURLResource().getInputStream();
		String format = asset.getMetadata().get("FileFormat");
		String fileName = pictureFile.getTitle() + "." + format;
		fileName = createFileName(fileName);

		BufferedImage content = ImageIO.read(input);
		BufferedImage editedPicture = content.getSubimage(x, y, width, height); // edit picture to use only chosen part

		java.io.File file = createFile(folderPath, fileName);
		ImageIO.write(editedPicture, format, file); // writes picture into file
	}

	/**
	 * This method gets a audio file and creates a file in the folder path from
	 * the range saved in teh file and the asset
	 * 
	 * @param file
	 *            The audio file to edit (The file will be casted to video
	 *            type)
	 * @param folderPath
	 *            The path where the file file is saved
	 * @throws SQLException
	 *             When there was an database error
	 * @throws IOException
	 *             When files couldn't be created
	 * @author Stefan Schmidt
	 */
	public void createAudioFileData(File file, String folderPath) throws IOException, SQLException {
		log.info("Called createAudioFileData with file={}, path={} ", file, folderPath);

		AudioFile audioFile = (AudioFile) file;

		// Required information
		Asset asset = audioFile.getAsset();
		InputStream in = asset.getURLResource().getInputStream();
		String fileName = audioFile.getTitle();
		fileName = createFileName(fileName);
		java.io.File audio = new java.io.File(folderPath + "/" + "Input_" + audioFile.getId() + fileName + ".mp3");
		FileUtils.copyInputStreamToFile(in, audio);

		FFmpeg ffmpeg = new FFmpeg(configData.getFfmpegPath());
		FFprobe ffprobe = new FFprobe(configData.getFfprobePath());

		log.info("Cut audio: {}", audio.getName());

		// Set information to cut the audio
		FFmpegBuilder builder = new FFmpegBuilder()
				.setInput(folderPath + "/" + "Input_" + audioFile.getId() + fileName + ".mp3")
				.addOutput(folderPath + "/" + fileName + ".mp3").setAudioCodec("copy")
				.setStartOffset(Long.valueOf(audioFile.getStart()), TimeUnit.MILLISECONDS)
				.setDuration(Long.valueOf(audioFile.getEnd()) - Long.valueOf(audioFile.getStart()),
						TimeUnit.MILLISECONDS)
				.done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

		// Run a one-pass encode
		executor.createJob(builder).run();

		log.info("create file for audio");
		createFile(folderPath, fileName + ".mp3");
		// addFileToZip(file, folderPath + ".mp3", zos);

		log.info("delete file: {}", audio.getName());
		deleteFiles(audio);
	}

	/**
	 * This method gets a video file and creates a file in the folder path from
	 * the range saved in the file and the asset
	 * 
	 * @param file
	 *            The video file to edit (The file will be casted to video
	 *            type)
	 * @param folderPath
	 *            The path where the file file is saved
	 * @throws SQLException
	 *             When there was an database error
	 * @throws IOException
	 *             When files couldn't be created
	 * @author Stefan Schmidt
	 */
	public void createVideoFileData(File file, String folderPath) throws SQLException, IOException {
		log.info("Called createVideoFileData with file={}, path={} ", file, folderPath);

		VideoFile videoFile = (VideoFile) file;

		// Required information
		Asset asset = videoFile.getAsset();
		InputStream in = asset.getURLResource().getInputStream();
		String fileName = videoFile.getTitle();
		fileName = createFileName(fileName);
		java.io.File video = new java.io.File(folderPath + "/" + "Input_" + videoFile.getId() + fileName + ".mp4");
		FileUtils.copyInputStreamToFile(in, video);

		FFmpeg ffmpeg = new FFmpeg(configData.getFfmpegPath());
		FFprobe ffprobe = new FFprobe(configData.getFfprobePath());

		log.info("Run ffprobe!");

		log.info("Ready to cut video: {}", video.getName());
		// Set information to cut the video
		FFmpegBuilder builder = new FFmpegBuilder()
				.setInput(folderPath + "/" + "Input_" + videoFile.getId() + fileName + ".mp4")
				.addOutput(folderPath + "/" + fileName + ".mp4").setAudioCodec("copy").setVideoCodec("copy")
				.setStartOffset(Long.valueOf(videoFile.getStart()), TimeUnit.MILLISECONDS)
				.setDuration(Long.valueOf(videoFile.getEnd()) - Long.valueOf(videoFile.getStart()),
						TimeUnit.MILLISECONDS)
				.done();

		log.info("Cut video!");
		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

		// Run a one-pass encode
		executor.createJob(builder).run();

		log.info("create file for video");
		createFile(folderPath, fileName + ".mp4");

		log.info("delete file");
		deleteFiles(video);
	}

	/**
	 * Creates a new file at given path with given name
	 * 
	 * @param filePath
	 *            Path where Fle should be created
	 * @param fileName
	 *            Nae of file that should becreated
	 * @return file created File
	 * @throws IOException
	 *             When file couldn't be created
	 * @author Vivian Holzapfel
	 */
	public java.io.File createFile(String filePath, String fileName) throws IOException {
		log.info("Called createFile with filePath={}, fileName={} ", filePath, fileName);

		java.io.File file = new java.io.File(filePath, fileName);

		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * Method deletes files or folders, including all subfolders
	 * 
	 * @param file
	 *            The file or folder to be deleted
	 */
	public void deleteFiles(java.io.File file) {
		log.info("Called deleteFiles with file={}", file);

		if (file != null) {
			if (file.isDirectory()) { // directory can be empty
				java.io.File[] files = file.listFiles();
				if (files.length != 0) {
					for (java.io.File f : files) {
						deleteFiles(f);
					}
				}
				file.delete();
			} else {
				file.delete();
			}
		}
	}

	/**
	 * This method creates a filename without illegal characters, replacing them
	 * with "_"
	 * 
	 * @param fileName
	 *            the name to be checked
	 * @return fileName without illegal characters
	 */
	public String createFileName(String fileName) {
		log.info("Called createFileName with fileName={}", fileName);
		return fileName.replaceAll("[\\\\/:*?\"<>| ]", "_");

	}

}
