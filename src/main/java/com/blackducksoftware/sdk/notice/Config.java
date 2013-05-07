package com.blackducksoftware.sdk.notice;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author jatoui
 * @title Solutions Architect
 * @email jatoui@blackducksoftware.com
 * @company Black Duck Software
 * @year 2013
 **/

public class Config {

	Logger log = Logger.getLogger(this.getClass());
	
	private List <String> licenseFilenames;
	
	private List <String> copyrightPatterns;
	
	private String projectName;

	private String protexUri;
	
	private String protexUsername;
	
	private String protexPassword;
	
	private String outputFilename;
	




	public Config() throws Exception
	{
		Properties prps = new Properties();
		// TODO Auto-generated method stub
		try {
			prps.load(new BufferedInputStream(new FileInputStream(
					"config.properties")));

		} catch (FileNotFoundException e1) {
			log.error("Cannot find properties file: config.properties", e1);
			throw e1;
		} catch (IOException e1) {
			log.error("Cannot read properties file: config.properties", e1);
			throw e1;
		}
		
		licenseFilenames = Arrays.asList(StringUtils.split(
				prps.getProperty("license.filenames"), ","));
		
		copyrightPatterns = Arrays.asList(StringUtils.split(
				prps.getProperty("copyright.patterns"), ","));
		
		protexUri = prps.getProperty("protex.uri");
		
		protexUsername = prps.getProperty("protex.username");
		
		protexPassword = prps.getProperty("protex.password");
		
		projectName = prps.getProperty("project.name");
		
		outputFilename = prps.getProperty("output.filename");
		
	}
	
	
	public List <String> getLicenseFilenames()
	{
		return licenseFilenames;
	}
	
	public List<String> getCopyrightPatterns() {
		return copyrightPatterns;
	}

	
	public String getProtexUri() {
		return protexUri;
	}


	public String getProtexUsername() {
		return protexUsername;
	}


	public String getProtexPassword() {
		return protexPassword;
	}

	public String getProjectName() {
		return projectName;
	}
	
	public String getOutputFilename() {
		return outputFilename;
	}

}
