package com.ghofrani.htw.RAN2.controller.event;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.service.IUserService;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Application Listener for OnRegistrationCompleteEvent. Sends emails with
 * verification links when new accounts are created.
 * 
 * @author Daniel Wahlmann
 *
 */
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private MessageSource messages;

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void onApplicationEvent(OnRegistrationCompleteEvent event) throws MailException {
		log.info("Registered OnRegistrationCompleteEvent");

		try {
			this.confirmRegistraion(event);
		} catch (MailException e) {
			throw e;
		}

	}

	/**
	 * Creates and sends a confirmation email.
	 * 
	 * @param event
	 */
	private void confirmRegistraion(OnRegistrationCompleteEvent event) throws MailException {
		User user = event.getUser();
		String token = UUID.randomUUID().toString();
		userService.createVerificationToken(user, token);

		log.info("peparing email");

		String recipientAddress = user.getEmail();
		String subject = "ProdLinRe Registration Confirmation";
		String confirmationUrl = event.getAppUrl() + "/registrationconfirm?token=" + token;
		String message = messages.getMessage("message.regSucc", null, event.getLocale());

		log.info("sending email is commented");
/*
		SimpleMailMessage email = new SimpleMailMessage();
		email.setFrom("ProductLinRE");
		email.setTo(recipientAddress);
		email.setSubject(subject);
		email.setText(message + " \r\n" + confirmationUrl);
		try {
			mailSender.send(email);
		} catch (MailException e) {
			log.error("Error while trying to send mail");
			throw e;
		}
*/
		log.info("email sent");
	}

}
