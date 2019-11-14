package com.ghofrani.htw.RAN2.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
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
import com.ghofrani.htw.RAN2.model.Artefact;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.AudioArtefact;
import com.ghofrani.htw.RAN2.model.OtherArtefact;
import com.ghofrani.htw.RAN2.model.PictureArtefact;
import com.ghofrani.htw.RAN2.model.TextArtefact;
import com.ghofrani.htw.RAN2.model.VideoArtefact;
import com.ghofrani.htw.RAN2.model.XMLArtefact;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

/**
 * Controller for editing assets to become artefacts
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
	 * Creates a file for the other Artefact type, file formats, which aren't
	 * supported by the program
	 * 
	 * @param artefact
	 *            the artefact to be edited
	 * @param featurePath
	 *            the path to the feature folder, where the file will be saved
	 * @throws IOException
	 *             is thrown when there was an error when reading a file
	 * 
	 * @author Vivian Holzapfel
	 */
	public void createOtherArtefactData(Artefact artefact, String featurePath) throws IOException {
		log.info("Called createOtherArtefactData with artefact={}, featurePath={} ", artefact, featurePath);

		OtherArtefact otherArtefact = (OtherArtefact) artefact;

		Asset asset = otherArtefact.getAsset();
		InputStream input = asset.getURLResource().getInputStream();
		String format = asset.getMetadata().get("FileFormat");
		String fileName = otherArtefact.getTitle() + "." + format;
		fileName = createFileName(fileName);

		File targetFile = new File(featurePath + "/" + fileName);
		FileUtils.copyInputStreamToFile(input, targetFile);

	}

	/**
	 * The Method edits a TextArtefact and writes the new String into a file
	 * 
	 * @param artefact
	 *            Artefact to be edited
	 * @param featurePath
	 *            Path to the featurefolder
	 * @throws SQLException
	 *             When there was an database error
	 * @throws IOException
	 *             When files couldn't be created
	 * @author Vivian Holzapfel
	 */
	public void createTextArtefactData(Artefact artefact, String featurePath) throws SQLException, IOException {
		log.info("Called createTextArtefactData with artefact={}, path={} ", artefact, featurePath);

		TextArtefact textArtefact = (TextArtefact) artefact;

		int start;
		int end;

		Asset asset = textArtefact.getAsset();
		InputStream input = asset.getURLResource().getInputStream();

		String content = new BufferedReader(new InputStreamReader(input)).lines().parallel()
				.collect(Collectors.joining("\n"));

		String editedContent = content;
		if (!textArtefact.getStart().equals("") || !textArtefact.getEnd().equals("")) {
			start = Integer.parseInt(textArtefact.getStart());
			end = Integer.parseInt(textArtefact.getEnd());
			editedContent = content.substring(start, end); // edit the text
		}

		String format = asset.getMetadata().get("FileFormat");
		String fileName = textArtefact.getTitle() + "." + format;
		fileName = createFileName(fileName);

		Files.write(Paths.get(featurePath + "/" + fileName), editedContent.getBytes()); // write into file
	}

	/**
	 * The Method edits a XMLArtefact and writes the new xml into a file
	 * 
	 * @param artefact
	 *            the artefact that needs to be edited
	 * @param featurePath
	 *            the path where the artefact will be saved
	 * @throws SQLException
	 *             When there was an database error
	 * @throws IOException
	 *             When files couldn't be created
	 * @author Robert Völkner
	 */
	public void createXMLArtefactData(Artefact artefact, String featurePath) throws IOException, SQLException {

		log.info("Called createXMLArtefactData with artefact={}, path={} ", artefact, featurePath);

		int[] nodes;
		XMLArtefact xmlArtefact = (XMLArtefact) artefact;
		if (xmlArtefact.getNodes().length() == 0) {
			nodes = new int[] { 1 };
		} else {
			String[] stringNodes = xmlArtefact.getNodes().split(",");
			nodes = new int[stringNodes.length];
			for (int i = 0; i < stringNodes.length; i++) { // fill with by user selected node positions
				nodes[i] = Integer.parseInt(stringNodes[i]);
			}
		}

		Asset asset = xmlArtefact.getAsset();
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
		String fileName = xmlArtefact.getTitle() + ".xml";
		fileName = createFileName(fileName);

		Files.write(Paths.get(featurePath + "/" + fileName), editedContent.getBytes()); // write into file
	}

	/**
	 * The Method edits a PictureArtefact and writes the new Image into a file
	 * 
	 * @param artefact
	 *            the pictureArtefact to be edited
	 * @param featurePath
	 *            the path to the file
	 * @throws SQLException
	 *             When there was an database error
	 * @throws IOException
	 *             When files couldn't be created
	 * @author Vivian Holzapfel
	 */
	public void createPictureArtefactData(Artefact artefact, String featurePath) throws SQLException, IOException {
		log.info("Called createPictureArtefactData with artefact={}, path={} ", artefact, featurePath);

		PictureArtefact pictureArtefact = (PictureArtefact) artefact;

		int x = Integer.parseInt(pictureArtefact.getX());
		int y = Integer.parseInt(pictureArtefact.getY());
		int width = Integer.parseInt(pictureArtefact.getWidth());
		int height = Integer.parseInt(pictureArtefact.getHeight());

		Asset asset = pictureArtefact.getAsset();
		InputStream input = asset.getURLResource().getInputStream();
		String format = asset.getMetadata().get("FileFormat");
		String fileName = pictureArtefact.getTitle() + "." + format;
		fileName = createFileName(fileName);

		BufferedImage content = ImageIO.read(input);
		BufferedImage editedPicture = content.getSubimage(x, y, width, height); // edit picture to use only chosen part

		File file = createFile(featurePath, fileName);
		ImageIO.write(editedPicture, format, file); // writes picture into file
	}

	/**
	 * This method gets a audio artefact and creates a file in the feature path from
	 * the range saved in teh artefact and the asset
	 * 
	 * @param artefact
	 *            The audio artefact to edit (The artefact will be casted to video
	 *            type)
	 * @param featurePath
	 *            The path where the artefact file is saved
	 * @throws SQLException
	 *             When there was an database error
	 * @throws IOException
	 *             When files couldn't be created
	 * @author Stefan Schmidt
	 */
	public void createAudioArtefactData(Artefact artefact, String featurePath) throws IOException, SQLException {
		log.info("Called createAudioArtefactData with artefact={}, path={} ", artefact, featurePath);

		AudioArtefact audioArtefact = (AudioArtefact) artefact;

		// Required information
		Asset asset = audioArtefact.getAsset();
		InputStream in = asset.getURLResource().getInputStream();
		String fileName = audioArtefact.getTitle();
		fileName = createFileName(fileName);
		File audio = new File(featurePath + "/" + "Input_" + audioArtefact.getId() + fileName + ".mp3");
		FileUtils.copyInputStreamToFile(in, audio);

		FFmpeg ffmpeg = new FFmpeg(configData.getFfmpegPath());
		FFprobe ffprobe = new FFprobe(configData.getFfprobePath());

		log.info("Cut audio: {}", audio.getName());

		// Set information to cut the audio
		FFmpegBuilder builder = new FFmpegBuilder()
				.setInput(featurePath + "/" + "Input_" + audioArtefact.getId() + fileName + ".mp3")
				.addOutput(featurePath + "/" + fileName + ".mp3").setAudioCodec("copy")
				.setStartOffset(Long.valueOf(audioArtefact.getStart()), TimeUnit.MILLISECONDS)
				.setDuration(Long.valueOf(audioArtefact.getEnd()) - Long.valueOf(audioArtefact.getStart()),
						TimeUnit.MILLISECONDS)
				.done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

		// Run a one-pass encode
		executor.createJob(builder).run();

		log.info("create file for audio");
		createFile(featurePath, fileName + ".mp3");
		// addFileToZip(file, featurePath + ".mp3", zos);

		log.info("delete file: {}", audio.getName());
		deleteFiles(audio);
	}

	/**
	 * This method gets a video artefact and creates a file in the feature path from
	 * the range saved in the artefact and the asset
	 * 
	 * @param artefact
	 *            The video artefact to edit (The artefact will be casted to video
	 *            type)
	 * @param featurePath
	 *            The path where the artefact file is saved
	 * @throws SQLException
	 *             When there was an database error
	 * @throws IOException
	 *             When files couldn't be created
	 * @author Stefan Schmidt
	 */
	public void createVideoArtefactData(Artefact artefact, String featurePath) throws SQLException, IOException {
		log.info("Called createVideoArtefactData with artefact={}, path={} ", artefact, featurePath);

		VideoArtefact videoArtefact = (VideoArtefact) artefact;

		// Required information
		Asset asset = videoArtefact.getAsset();
		InputStream in = asset.getURLResource().getInputStream();
		String fileName = videoArtefact.getTitle();
		fileName = createFileName(fileName);
		File video = new File(featurePath + "/" + "Input_" + videoArtefact.getId() + fileName + ".mp4");
		FileUtils.copyInputStreamToFile(in, video);

		FFmpeg ffmpeg = new FFmpeg(configData.getFfmpegPath());
		FFprobe ffprobe = new FFprobe(configData.getFfprobePath());

		log.info("Run ffprobe!");

		log.info("Ready to cut video: {}", video.getName());
		// Set information to cut the video
		FFmpegBuilder builder = new FFmpegBuilder()
				.setInput(featurePath + "/" + "Input_" + videoArtefact.getId() + fileName + ".mp4")
				.addOutput(featurePath + "/" + fileName + ".mp4").setAudioCodec("copy").setVideoCodec("copy")
				.setStartOffset(Long.valueOf(videoArtefact.getStart()), TimeUnit.MILLISECONDS)
				.setDuration(Long.valueOf(videoArtefact.getEnd()) - Long.valueOf(videoArtefact.getStart()),
						TimeUnit.MILLISECONDS)
				.done();

		log.info("Cut video!");
		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

		// Run a one-pass encode
		executor.createJob(builder).run();

		log.info("create file for video");
		createFile(featurePath, fileName + ".mp4");

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
	public File createFile(String filePath, String fileName) throws IOException {
		log.info("Called createFile with filePath={}, fileName={} ", filePath, fileName);

		File file = new File(filePath, fileName);

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
	public void deleteFiles(File file) {
		log.info("Called deleteFiles with file={}", file);

		if (file != null) {
			if (file.isDirectory()) { // directory can be empty
				File[] files = file.listFiles();
				if (files.length != 0) {
					for (File f : files) {
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
