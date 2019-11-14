package com.ghofrani.htw.RAN2.controller.setup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ghofrani.htw.RAN2.Application;
import com.ghofrani.htw.RAN2.database.UserAccess;
import com.ghofrani.htw.RAN2.model.Asset;
import com.ghofrani.htw.RAN2.model.User;

/**
 * Setup for creating the admin account on first start, if it does not already
 * exist.
 * 
 * @author Daniel Wahlmann
 *
 */
@Component
public class Setup implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private boolean alreadySetup = false;

	@Autowired
	private UserAccess userAccess;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private SetupProperties setupProperties;

	@Autowired
	private ConfigData configData;

	/**
	 * Creates an admin user if none exists.
	 * 
	 * @param event Event that caused this to be called
	 */
	@Transactional
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (alreadySetup) {
			return;
		}

		log.debug("Setting path to store assets in to: " + configData.getAssetPath());

		Asset.assetPath = configData.getAssetPath();

		log.debug("Setting up admin account");

		User admin = null;

		List<User> admins = userAccess.selectUsersByRole("ROLE_ADMIN");

		if (admins == null || admins.isEmpty()) {
			admin = new User();
		} else {
			return;
		}

		admin.setEmail(setupProperties.getEmail());
		admin.setUsername(setupProperties.getUsername());
		admin.setPassword(passwordEncoder.encode(setupProperties.getPassword()));
		admin.setEnabled(true);

		List<String> roles = new ArrayList<>();
		roles.add("ROLE_USER");
		roles.add("ROLE_ADMIN");
		admin.setRoles(roles);

		admin.setDailyUploadVolume((long) 0);
		admin.setTotalDataVolume((long) 0);

		admin.setRegistrationDate(Calendar.getInstance().getTime());

		userAccess.saveUser(admin);

		alreadySetup = true;
	}

}
