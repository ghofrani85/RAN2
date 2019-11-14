package com.ghofrani.htw.RAN2.controller.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 
 * Spring Web Security configuration.
 * 
 * @author Daniel Wahlmann
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * Using BCrypt as password encoder.
	 * 
	 * @return the PasswordEncoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Initiating the authentication provider.
	 * 
	 * @return The DaoAuthenticationProvider
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	/**
	 * Configures the views that can be accessed by anyone, those that can only be
	 * accessed by authenticated users and the login page.
	 * 
	 * @param http HttpSecurity Object
	 * @throws Exception An error was encountered
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/", "/register", "/css/**", "/registrationconfirm", "/notice", "/passwordreset",
						"/forgotpassword", "/forgotpassword/passwordreset", "/register/registrationconfirm")
					.permitAll()
				.antMatchers("/admin")
					.hasRole("ADMIN")
				.antMatchers("updatePassword")
					.hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
				.anyRequest().authenticated()
					.and()
				.formLogin()
					.loginPage("/login").defaultSuccessUrl("/overview", true).permitAll()
					.and()
				.logout().permitAll();
	}

	/**
	 * Configures custom authentication manager.
	 * 
	 * @param auth The authentification
	 * @throws Exception An error was encountered
	 */
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsService);
	}
}
