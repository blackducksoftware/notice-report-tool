/*
 * Created on August 6, 2013
 * Copyright 2004-2013 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */

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
	
	private String showFilePaths;
	
	private String showComponentVersion;
	
	private String showCopyrights;
	
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
		
		showFilePaths = prps.getProperty("show.file.paths");
		
		showComponentVersion = prps.getProperty("show.component.version");
		
		showCopyrights = prps.getProperty("show.copyrights");
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
	
	public String getShowFilePaths() {
		return showFilePaths;
	}
	
	public String getShowComponentVersion() {
		return showComponentVersion;
	}
	
	public String getShowCopyrights() {
		return showCopyrights;
	}

}
