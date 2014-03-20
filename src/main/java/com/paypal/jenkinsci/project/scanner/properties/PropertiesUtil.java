package com.paypal.jenkinsci.project.scanner.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public final class PropertiesUtil {

	private static PropertiesUtil instance;
	private final String filename = "config.properties";
	private Properties properties;

	public static PropertiesUtil getInstance() {

		if (instance == null) {
			instance = new PropertiesUtil();
		}
		return instance;
	}

	private PropertiesUtil() {

		properties = new Properties();
		InputStream input = null;

		input = PropertiesUtil.class.getClassLoader().getResourceAsStream(
				filename);
				// load a properties file from class path, inside static method
		try {
			properties.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());

		}
	}

	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}

}
