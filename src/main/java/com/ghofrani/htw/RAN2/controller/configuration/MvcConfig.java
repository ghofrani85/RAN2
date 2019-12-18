package com.ghofrani.htw.RAN2.controller.configuration;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Basic Spring Boot configuration and beans.
 * 
 * @author Daniel Wahlmann
 *
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

	/**
	 * Registers View Controllers.
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/project").setViewName("project");
		registry.addViewController("/overview").setViewName("overview");
		registry.addViewController("/asset").setViewName("asset");
		registry.addViewController("/").setViewName("forward:/login");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/register").setViewName("register");
		registry.addViewController("/login.html").setViewName("forward:/overview");
		registry.addViewController("/admin").setViewName("admin");
		registry.addViewController("/folder").setViewName("folder");
		//registry.addViewController("/folder").setViewName("folder");
		registry.addViewController("/browse").setViewName("browse");
		registry.addViewController("/notice").setViewName("notice");
		registry.addViewController("/userSettings").setViewName("userSettings");
	}

	/**
	 * Registers interceptors.
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	/**
	 * Localizes messages.
	 * 
	 * @return the LocaleResolver
	 */
	@Bean
	public LocaleResolver localeResolver() {
		final CookieLocaleResolver resolver = new CookieLocaleResolver();
		resolver.setDefaultLocale(Locale.US);
		resolver.setCookieName("localeInfo");

		resolver.setCookieMaxAge(90 * 24 * 60 * 60);
		return resolver;
	}

	/**
	 * Provides the message source.
	 * 
	 * @return the MessageSource
	 */
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
	}

	/**
	 * Detects and changes the locale based on the 'lang' parameter.
	 * 
	 * @return the LocaleChangeInterceptor
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}
	
	/**
	 * Provides localization for custom validation messages.
	 */
	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource());
		return validator;
	}
}
