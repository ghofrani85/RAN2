package com.ghofrani.htw.RAN2.controller;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.ghofrani.htw.RAN2.Application;

/**
 * Responsible for cleaning the downloads folder
 * 
 * @author Vivian Holzapfel
 *
 */

public class CleanupFiles {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	/**
	 * cleans files older than specified threshold days
	 */
	@Scheduled(cron = "0 0 0,6,12,18 * * *")
	public void cleanUpFiles() {
		log.info("Clean up of downloads folder");

		File folder = new File("./tmp");
		cleanUpFilesAfterTime(folder);

	}

	private void cleanUpFilesAfterTime(File folder) {
		int threshold = 1;
		File[] files;

		if(folder != null) {
			if (folder.isDirectory()) {
				files = folder.listFiles();
				if (files != null) {
					for (File f : files) {
						cleanUpFilesAfterTime(f);
					}
				}
			} else {
				if (filterbyAge(folder, threshold)) {
					folder.delete();
				}
			}
		}
	}

	private boolean filterbyAge(File folder, int threshold) {
		long diff = new Date().getTime() - folder.lastModified();

		return diff > threshold * 24L * 60L * 60L * 1000L;
	}

	/**
	 * 
	 * Deletes everything in a folder
	 * 
	 * @param file
	 *            Directory where files will be deleted
	 */
	public void deleteFiles(File file) {
		log.info("Called deleteFiles with file={}", file);

		if(file != null) {
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					deleteFiles(f);
				}
				file.delete();
			} else {
				file.delete();
			}
		}
	}

}
