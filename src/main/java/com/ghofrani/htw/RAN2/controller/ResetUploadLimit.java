package com.ghofrani.htw.RAN2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.service.IUserService;

/**
 * Scheduled task for resetting the daily upload limit.
 * 
 * @author Daniel Wahlmann
 *
 */
@Component
public class ResetUploadLimit {

	@Autowired
	private IUserService userService;

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	/**
	 * Resets the daily upload limit for all users once a day.
	 */
	@Scheduled(cron = "0 0 0 * * * ")
	public void resetDailyLimit() {
		log.info("Reset daily upload limit.");

		userService.resetDailyUploadLimit();
	}

}
