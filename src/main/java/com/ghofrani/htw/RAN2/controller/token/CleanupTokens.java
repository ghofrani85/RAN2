package com.ghofrani.htw.RAN2.controller.token;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.service.IUserService;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Scheduled task for cleaning up the verification and password reset tokens in
 * the database.
 * 
 * @author Daniel Wahlmann
 *
 */
@Component
public class CleanupTokens {

	@Autowired
	private IUserService userService;

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	/**
	 * Cleans up the database by deleting expired verification tokens and associated
	 * users, if their account has not been activated.
	 */
	@Scheduled(cron = "0 0 0,6,12,18 * * *")
	public void cleanUpVerificationTokens() {
		log.info("Clean up of Verification Tokens");

		List<VerificationToken> tokens = userService.getVerificationTokens();

		Calendar cal = Calendar.getInstance();
		for (VerificationToken token : tokens) {
			if (token.getExpirationDate().getTime() - cal.getTime().getTime() <= 0) {
				User user = token.getUser();
				if (!user.isEnabled()) {
					userService.deleteUser(user);
				}

				userService.deleteVerificationToken(token);
			}
		}
	}

	/**
	 * Cleans up the database by deleting expired password reset tokens.
	 */
	@Scheduled(cron = "0 0 1,7,13,19 * * *")
	public void cleanUpPasswordResetTokens() {
		log.info("Clean up of Password Reset Tokens");

		List<PasswordResetToken> tokens = userService.getPasswordResetTokens();

		Calendar cal = Calendar.getInstance();
		for (PasswordResetToken token : tokens) {
			if (token.getExpirationDate().getTime() - cal.getTime().getTime() <= 0) {
				userService.deletePasswordResetToken(token);
			}
		}
	}
}
