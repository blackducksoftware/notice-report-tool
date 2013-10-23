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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Logger;

import com.blackducksoftware.protex.sdk.ProtexReportWrapper;
import com.blackducksoftware.protex.sdk.ProtexSDKWrapper;
import com.blackducksoftware.protex.sdk.ProtexServerProxyUtils;
import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.notice.model.ComponentModel;
import com.blackducksoftware.sdk.notice.model.LicenseModel;
import com.blackducksoftware.sdk.notice.report.HtmlReportGenerator;

import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxyV6_1;
import com.blackducksoftware.sdk.protex.common.Component;
import com.blackducksoftware.sdk.protex.common.ComponentInfo;
import com.blackducksoftware.sdk.protex.common.ComponentInfoColumn;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersion;
import com.blackducksoftware.sdk.protex.component.version.ComponentVersionApi;

import com.blackducksoftware.sdk.protex.license.GlobalLicense;
import com.blackducksoftware.sdk.protex.license.License;
import com.blackducksoftware.sdk.protex.license.LicenseApi;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.bom.BomApi;
import com.blackducksoftware.sdk.protex.project.bom.BomComponent;
import com.blackducksoftware.sdk.protex.project.codetree.CharEncoding;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeApi;

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
	
	static ProjectApi projectApi = null;
	
	static BomApi bomApi = null;
	
	static ComponentVersionApi componentVersionApi = null;
	
	static LicenseApi licenseApi = null;
	
	static CodeTreeApi codeTreeApi = null;
	

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
		
		System.out.println("\nProject Name: " + config.getProjectName() + "\n");

		String projectId = sdkWrapper.getProjectId(config.getProjectName());

		System.out.println("\nGetting Component Info...\n");
		
		HashMap<String, Set<String>> componentToPathMappings = new HashMap<String, Set<String>>();
		
		if (config.getShowFilePaths().toLowerCase().equals("true") || config.getIncludeLicenseFilenamesInReport().toLowerCase().equals("true"))
			componentToPathMappings = reportWrapper.getComponentToPathMappings(projectId);
		else
			componentToPathMappings = reportWrapper.getComponentNameAndVersion(projectId);

		System.out.println("\nGetting Copyright Info...\n");
		
		HashMap<String, List<String>> pathToCopyrightMappings = new HashMap<String, List<String>>();
		
		if (config.getShowCopyrights().toLowerCase().equals("true"))
			pathToCopyrightMappings = reportWrapper.getPathtoCopyrightMappings(projectId,
							config.getCopyrightPatterns(), pathToLicenseIdMapping);

		HashMap<String, ComponentModel> components = new HashMap<String, ComponentModel>();
		
		System.out.println();
		
		for (String componentKey : componentToPathMappings.keySet()) {
			System.out.println("Processing information for: " + componentKey);
			
			ComponentModel model = new ComponentModel();
			components.put(componentKey, model);

			model.setName(componentKey);

			// Hashtable index is represented in format [Component Name]:[Version] and we need to
			// extract the Component Name and version
			
			String compName = componentKey.substring(0,componentKey.lastIndexOf(":"));
			
			String compVersion = componentKey.substring(componentKey.lastIndexOf(":")+1, componentKey.length());

			//setKBLevelInformation(projectId, compName, model);
			
			addLicenseFromKB(projectId, compName, compVersion, model);

			Set<String> paths = componentToPathMappings.get(componentKey);

			model.setPaths(paths);

			for (String path : paths) {
				List<String> copyrights = pathToCopyrightMappings.get(path);
				if (copyrights != null)
					for (String copyright : copyrights)
						model.addNewCopyright(copyright);
				
				if (pathToLicenseIdMapping.get(path) != null)
					for (String licenseId : pathToLicenseIdMapping.get(path)) {

						License license = projectApi.getLicenseById(
								projectId, licenseId);
						
						if (license != null) {
							LicenseModel licenseModel = new LicenseModel();
							licenseModel.setId(licenseId);
							licenseModel.setName(license.getName());
							licenseModel.setText(license.getText());

							model.addNewLicense(licenseModel);
						}						
					}
				
				if (config.getIncludeLicenseFilenamesInReport().toLowerCase().equals("true"))
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

	
	private void addLicenseFromKB(String projectId, String compName, String compVersion, ComponentModel model) throws SdkFault {
		List<BomComponent> bomComponents = bomApi.getBomComponents(projectId);
		
		for (BomComponent bomComponent : bomComponents) {
			String bomComponentId = bomComponent.getComponentId();
			String bomComponentVersionId = bomComponent.getVersionId();
			
			ComponentVersion compVersionObj = new ComponentVersion();
			String compVersionObjName = new String();
			String compVersionObjVersion = new String();
			
			// If the current bomComponent is NOT the project, then get it's ComponentVersion object
			if (!bomComponentId.equals(projectId)) {
				compVersionObj = componentVersionApi.getComponentVersionById(bomComponentId, bomComponentVersionId != null ? bomComponentVersionId : "");
				
				compVersionObjName = compVersionObj.getComponentName();
				compVersionObjVersion = compVersionObj.getVersionName();
			}

			// If the component name matches, but the version is null, that means the version was manually overridden in the UI
			// so now we have to get the license from the bomComponent, instead of the compVersionObj
			if (compVersionObjName.equals(compName) && compVersionObjVersion == null) {
				
					LicenseInfo licenseInfo = bomComponent.getLicenseInfo();

					String licenseId = new String();
					
					if (licenseInfo != null)
						licenseId = licenseInfo.getLicenseId();
							
					if (licenseId != null && !licenseId.equals("")) {
		
						License license = projectApi.getLicenseById(
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
			
			// If both the component name and version match, then we have the exact component and can get the license info from the compVersionObj
			else if (compVersionObjName.equals(compName) && compVersionObjVersion.equals(compVersion)) {
				
				List<LicenseInfo> compVersionObjLicenseInfos = new ArrayList<LicenseInfo>();
				
				compVersionObjLicenseInfos = compVersionObj.getLicenses();
				
				for (LicenseInfo licenseInfo : compVersionObjLicenseInfos) {
					String licenseId = new String();
					
					if (licenseInfo != null)
						licenseId = licenseInfo.getLicenseId();
							
					if (licenseId != null && !licenseId.equals("")) {
		
						License license = projectApi.getLicenseById(
								projectId, licenseId);
		
						if (license != null) {
							LicenseModel licenseModel = new LicenseModel();
							licenseModel.setName(license.getName());
							licenseModel.setText(license.getText());
		
							model.addNewLicense(licenseModel);
						}
		
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
			fileText = new String(codeTreeApi.getFileContent(projectId, path,
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
		System.out.println("NoticeReportTool v0.5\n");
		config = new Config();
		//long startTime = System.nanoTime();
		
	    try {
	        Long connectionTimeout = 120 * 1000L;
	        ProtexServerProxyV6_1 myProtexServer = new ProtexServerProxyV6_1(config.getProtexUri(), config.getProtexUsername(), config.getProtexPassword(),
	                connectionTimeout);
	        
	        licenseApi = myProtexServer.getLicenseApi();
	        projectApi = myProtexServer.getProjectApi();
	        bomApi = myProtexServer.getBomApi();
	        componentVersionApi = myProtexServer.getComponentVersionApi();
	        codeTreeApi = myProtexServer.getCodeTreeApi();
	        
            TLSClientParameters tlsClientParameters = new TLSClientParameters();
            tlsClientParameters.setDisableCNCheck(true);
            
            org.apache.cxf.endpoint.Client client = org.apache.cxf.frontend.ClientProxy.getClient(licenseApi);
            HTTPConduit http = (HTTPConduit) client.getConduit();
            http.setTlsClientParameters(tlsClientParameters);
            
            client = org.apache.cxf.frontend.ClientProxy.getClient(projectApi);
            http = (HTTPConduit) client.getConduit();
            http.setTlsClientParameters(tlsClientParameters);
            
            client = org.apache.cxf.frontend.ClientProxy.getClient(bomApi);
            http = (HTTPConduit) client.getConduit();
            http.setTlsClientParameters(tlsClientParameters);  
	        
            client = org.apache.cxf.frontend.ClientProxy.getClient(componentVersionApi);
            http = (HTTPConduit) client.getConduit();
            http.setTlsClientParameters(tlsClientParameters);  
            
            client = org.apache.cxf.frontend.ClientProxy.getClient(codeTreeApi);
            http = (HTTPConduit) client.getConduit();
            http.setTlsClientParameters(tlsClientParameters); 
	        
	
	    } catch (RuntimeException e) {
	        System.err.println("\nConnection to server '" + config.getProtexUri() + "' failed: " + e.getMessage());
	        System.exit(-1);
	    }

		NoticeReportTool noticeRepTool = new NoticeReportTool();

		HashMap<String, ComponentModel> components = noticeRepTool
				.processProject();

		HtmlReportGenerator reportGen = new HtmlReportGenerator();

		System.out.println("\nGenerating Report...");
		
		reportGen.generateReport(config.getProjectName(),
				config.getOutputFilename(), components);
		
		System.out.println("\nDone!");
		
		//long finishTime = System.nanoTime() - startTime;
		//System.out.printf("\nDone! \nTime: %.3f seconds", finishTime / 1e9);

	}
}
