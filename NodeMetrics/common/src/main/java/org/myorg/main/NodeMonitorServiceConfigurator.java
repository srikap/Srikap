package org.myorg.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
/**
 * Main entry point for the application. 
 * Main service NMS starts during servlet context creation.
 * Also loads properties and serves property values.
 * 
 * @author srikap
 *
 */
public class NodeMonitorServiceConfigurator {
	//Singleton. 
	private static NodeMonitorServiceConfigurator instance = null;
	//Properties
	private Properties prop = new Properties();
	private Logger logger = Logger.getLogger(NodeMonitorServiceConfigurator.class);
	
	
	public static NodeMonitorServiceConfigurator getInstance() {
		if (instance == null){
			instance = new NodeMonitorServiceConfigurator();
		}
		return instance;
	}

	/**
	 * Constructor. Starts the NMS service;
	 */
	public NodeMonitorServiceConfigurator() {
		prop = new Properties();
		URL url = NodeMonitorServiceConfigurator.class.getClassLoader().getResource("config.properties");
		try {
			prop.load(url.openStream());
			logger.debug("Loaded configuration properties file");
		} catch (FileNotFoundException e) {
			logger.fatal(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.fatal(e.getMessage());
			System.exit(1);
		}
	}
	
	public String getProperty(String property, String defaultValue){
		return prop.getProperty(property, defaultValue);
	}
	
	/**
	 * Convert from String to integer
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	public Integer getIntProperty(String property, int defaultValue){
		String val =  prop.getProperty(property,  String.valueOf(defaultValue));
		Integer parsedInt = null;
		try {
			parsedInt = Integer.valueOf(val);
		} catch(NumberFormatException ex){
			logger.error("Property:" + property + " should be an int value");
			return defaultValue;
		}
		return parsedInt;
	}
	
	/**
	 * Convert from String to long
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	public Long getLongProperty(String property, long defaultValue){
		String val =  prop.getProperty(property,  String.valueOf(defaultValue));
		Long parsedLong = null;
		try {
			parsedLong = Long.valueOf(val);
		} catch(NumberFormatException ex){
			logger.error("Property:" + property + " should be a long value");
			return defaultValue;
		}
		return parsedLong;
	}
	
}
