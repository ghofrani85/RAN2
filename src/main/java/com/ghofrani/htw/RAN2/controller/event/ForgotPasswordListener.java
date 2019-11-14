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
 * Application Listener for OnForgotPasswordEvent. Sends emails with password
 * reset tokens when an existing email is entered in the forgot password form.
 * 
 * @author Daniel Wahlmann
 *
 */
@Component
public class ForgotPasswordListener implements ApplicationListener<OnForgotPasswordEvent> {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private IUserService userService;

	@Autowired
	private MessageSource messages;

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void onApplicationEvent(OnForgotPasswordEvent event) throws MailException {

		log.info("Received OnForgotPasswordEvent.");

		try {
			this.sendPasswordResetToken(event);
		} catch (MailException e) {
			throw e;
		}

	}

	/**
	 * Creates and sends password reset emails.
	 * 
	 * @param event
	 */
	private void sendPasswordResetToken(OnForgotPasswordEvent event) throws MailException {
		User user = event.getUser();
		String token = UUID.randomUUID().toString();
		userService.createPasswordResetToken(user, token);

		String recipientAddress = user.getEmail();
		String subject = "ProdLinRe Password Reset";
		String confirmationUrl = event.getAppUrl() + "/passwordreset?token=" + token;
		String message = messages.getMessage("message.passwordReset", null, event.getLocale());

		SimpleMailMessage email = new SimpleMailMessage();
		email.setFrom("ProductLinRE");
		email.setTo(recipientAddress);
		email.setSubject(subject);
		email.setText(message + " \r\n" + confirmationUrl);
		try {
			mailSender.send(email);
		} catch (MailException e) {
			log.error("Error while sending email.");
			throw e;
		}

	}

}
