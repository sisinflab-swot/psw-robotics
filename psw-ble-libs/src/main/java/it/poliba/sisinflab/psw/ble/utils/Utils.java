package it.poliba.sisinflab.psw.ble.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Utils {
	
	final String propFileName = "config.properties";
	
	final String DEFAULT_ONTOLOGY_PROP = "DEFAULT_ONTOLOGY";
	final String TMP_FOLDER_PROP = "TMP_FOLDER";
	final String MAX_AGE_PROP = "MAX_AGE";
	final String SIM_MODE_PROP = "SIM_MODE";
	
	Properties prop;
	ClassLoader classLoader = getClass().getClassLoader();
	
	public Utils() throws IOException {
		prop = new Properties();
		
		//Get file from resources folder		
		//File file = new File(classLoader.getResource(propFileName).getFile());		
		//InputStream inputStream = new FileInputStream(file);			
		
		InputStream inputStream = classLoader.getResourceAsStream(propFileName);
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		
		prop.load(br);
	}

	public String getTempFolder() {
		return System.getProperty("user.dir") + prop.getProperty(TMP_FOLDER_PROP);
	}
	
	public long getMaxAge() {
		return Long.parseLong(prop.getProperty(MAX_AGE_PROP));
	}
	
	public InputStream getDefaultOntology() {
		String onto = prop.getProperty(DEFAULT_ONTOLOGY_PROP);
		return classLoader.getResourceAsStream("data/" + onto);		
	}
	
	public InputStream getResourceDocument(String path){
		return classLoader.getResourceAsStream("data/" + path);
	}
	
	public boolean getSimulationMode() {
		return Boolean.parseBoolean(prop.getProperty(SIM_MODE_PROP));
	}
}
