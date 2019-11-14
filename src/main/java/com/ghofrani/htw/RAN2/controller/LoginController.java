package com.ghofrani.htw.RAN2.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller for the login page
 * @author Lukas Beckmann
 *
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.controller.setup.ConfigData;

@Controller
public class LoginController {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ConfigData configData;

	private String welcome;

	/**
	 * The Login Controller. It is necessary for setting the info text at login.
	 * 
	 * @param model ModelObject for the View
	 * @return Loaded View for login
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/login")
	public String loadLogin(Model model) {

		if (welcome == null) { // load welcome text
			try {
				Path welcomePath = Paths.get(configData.getWelcomeTextPath());
				log.info("getting welcome text from: " + welcomePath.toAbsolutePath());
				byte[] encoded = Files.readAllBytes(Paths.get(configData.getWelcomeTextPath()));
				welcome = new String(encoded, StandardCharsets.UTF_8);
			} catch (IOException e) {
				log.error("Login Controller: Error while reading the welcome text: " + e.getMessage());
				welcome = "";
			}
		}

		model.addAttribute("infoText", welcome);
		return "login";
	}
}
