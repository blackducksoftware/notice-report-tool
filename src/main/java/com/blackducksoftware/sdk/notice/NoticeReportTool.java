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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.blackducksoftware.protex.sdk.ProtexReportWrapper;
import com.blackducksoftware.protex.sdk.ProtexSDKWrapper;
import com.blackducksoftware.protex.sdk.ProtexServerProxyUtils;
import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.notice.model.ComponentModel;
import com.blackducksoftware.sdk.notice.model.LicenseModel;
import com.blackducksoftware.sdk.notice.report.HtmlReportGenerator;

import com.blackducksoftware.sdk.protex.common.ComponentInfo;
import com.blackducksoftware.sdk.protex.common.ComponentInfoColumn;

import com.blackducksoftware.sdk.protex.license.GlobalLicense;
import com.blackducksoftware.sdk.protex.license.License;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.bom.BomComponent;
import com.blackducksoftware.sdk.protex.project.codetree.CharEncoding;

import com.blackducksoftware.sdk.protex.util.PageFilterFactory;

/**
 * @author jatoui
 * @title Solutions Architect
 * @email jatoui@blackducksoftware.com
 * @company Black Duck Software
 * @year 2012
 **/

public class NoticeReportTool {

	Logger log = Logger.getLogger(this.getClass().getName());

	private static Config config;

	private ProtexSDKWrapper sdkWrapper;

	private ProtexReportWrapper reportWrapper;

	public NoticeReportTool() throws Exception {

		sdkWrapper = new ProtexSDKWrapper(config.getProtexUri(),
				config.getProtexUsername(), config.getProtexPassword());

		reportWrapper = new ProtexReportWrapper(config.getProtexUri(),
				config.getProtexUsername(), config.getProtexPassword());
	}
	
	private HashMap<String, List<String>> pathToLicenseIdMapping = new HashMap<String, List<String>>();

	private HashMap<String, ComponentModel> processProject() throws Exception {

		if (!sdkWrapper.projectExists(config.getProjectName())) {
			log.error("Project with name " + config.getProjectName()
					+ " not found");
			System.exit(1);
		}

		String projectId = sdkWrapper.getProjectId(config.getProjectName());

		HashMap<String, Set<String>> componentToPathMappings = reportWrapper
				.getComponentToPathMappings(projectId);

		HashMap<String, List<String>> pathToCopyrightMappings = reportWrapper
				.getPathtoCopyrightMappings(projectId,
						config.getCopyrightPatterns(), pathToLicenseIdMapping);

		HashMap<String, ComponentModel> components = new HashMap<String, ComponentModel>();

		for (String componentKey : componentToPathMappings.keySet()) {
			ComponentModel model = new ComponentModel();
			components.put(componentKey, model);

			model.setName(componentKey);

			// Hashtable index is represented in format [Component
			// Name]:[Version] and need to
			// extract the Component Name
			String compName = componentKey.substring(0,
					componentKey.lastIndexOf(":"));

			//setKBLevelInformation(projectId, compName, model);
			
			addLicenseFromKB(projectId, compName, model);

			Set<String> paths = componentToPathMappings.get(componentKey);

			model.setPaths(paths);

			for (String path : paths) {
				List<String> copyrights = pathToCopyrightMappings.get(path);
				if (copyrights != null)
					for (String copyright : copyrights)
						model.addNewCopyright(copyright);
				
				if (pathToLicenseIdMapping.get(path) != null)

					for (String licenseId : pathToLicenseIdMapping.get(path)) {

						License license = ProtexServerProxyUtils.getProjectApi(
								config.getProtexUri(), config.getProtexUsername(),
								config.getProtexPassword()).getLicenseById(
								projectId, licenseId);
						
						if (license != null) {
							LicenseModel licenseModel = new LicenseModel();
							licenseModel.setId(licenseId);
							licenseModel.setName(license.getName());
							licenseModel.setText(license.getText());

							model.addNewLicense(licenseModel);
						}						
					}				

				for (String licenseFilename : config.getLicenseFilenames()) {
					if (FilenameUtils.getName(path).endsWith(licenseFilename)) {
						String fileText = getFileText(projectId, path);
						if (fileText != null) {
							LicenseModel licenseModel = new LicenseModel();

							licenseModel.setText(fileText);

							model.addNewLicense(licenseModel);
						}
					}

				}

			}

		}

		return components;
	}

	
	private void addLicenseFromKB(String projectId, String compName, ComponentModel model) throws SdkFault {
		List<BomComponent> bomComponents = ProtexServerProxyUtils.getBomApi(config.getProtexUri(), config.getProtexUsername(),
				config.getProtexPassword()).getBomComponents(projectId);
		
		for (BomComponent bomComponent : bomComponents) {
			if (ProtexServerProxyUtils.getProjectApi(config.getProtexUri(), config.getProtexUsername(),
				config.getProtexPassword()).getComponentById(projectId, bomComponent.getComponentId()).getName().equals(compName)) {
				
				LicenseInfo licenseInfo = bomComponent.getLicenseInfo();
				String licenseId = licenseInfo.getLicenseId();
						
				if (licenseId != null && !licenseId.equals("")) {
	
					License license = ProtexServerProxyUtils.getProjectApi(
							config.getProtexUri(), config.getProtexUsername(),
							config.getProtexPassword()).getLicenseById(
							projectId, licenseId);
	
					if (license != null) {
						LicenseModel licenseModel = new LicenseModel();
						licenseModel.setName(license.getName());
						licenseModel.setText(license.getText());
	
						model.addNewLicense(licenseModel);
					}
	
				}
				break;
			}
		}
	}

	// The .suggestComponents method below sometimes returns "strange results", like components named "Javascript AKA JavaScript blah blah"
	// I have replaced this setKBLevelInformation method with the addLicenseFromKB method above
	/*
	private void setKBLevelInformation(String projectId, String compName,
			ComponentModel model) throws SdkFault {
		List<ComponentInfo> matches = ProtexServerProxyUtils.getProjectApi(
				config.getProtexUri(), config.getProtexUsername(),
				config.getProtexPassword()).suggestComponents(projectId,
				compName, false,
				PageFilterFactory.getAllRows(ComponentInfoColumn.COMPONENT_ID));

		for (ComponentInfo match : matches) {
			if (match.getName().equals(compName)) {
				String licenseId = match.getPrimaryLicenseId();

				if (licenseId != null && !licenseId.equals("")) {

					License license = ProtexServerProxyUtils.getProjectApi(
							config.getProtexUri(), config.getProtexUsername(),
							config.getProtexPassword()).getLicenseById(
							projectId, licenseId);

					if (license != null) {
						LicenseModel licenseModel = new LicenseModel();
						licenseModel.setName(license.getName());
						licenseModel.setText(license.getText());

						model.addNewLicense(licenseModel);
					}

				}
			}

		}
	}
	*/

	private String getFileText(String projectId, String path) {
		String fileText = null;

		log.info("hit with license name found for file " + path);

		try {
			// reading the uploaded content of the file from the
			// database
			fileText = new String(ProtexServerProxyUtils.getCodeTreeApi(
					config.getProtexUri(), config.getProtexUsername(),
					config.getProtexPassword()).getFileContent(projectId, path,
					CharEncoding.NONE));

		} catch (SdkFault e) {
			log.warn(
					path
							+ " needs to be re-configured as File Upload type and project re-scanned in order to process by this tool.",
					e);
		}

		return fileText;

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		config = new Config();

		NoticeReportTool noticeRepTool = new NoticeReportTool();

		HashMap<String, ComponentModel> components = noticeRepTool
				.processProject();

		HtmlReportGenerator reportGen = new HtmlReportGenerator();

		reportGen.generateReport(config.getProjectName(),
				config.getOutputFilename(), components);

	}
}
