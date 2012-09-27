package com.blackducksoftware.sdk.atlassian.tool;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.util.Base64;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.blackducksoftware.sdk.atlassian.model.ComponentModel;
import com.blackducksoftware.sdk.atlassian.model.LicenseModel;
import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxyV6_1;
import com.blackducksoftware.sdk.protex.license.GlobalLicense;
import com.blackducksoftware.sdk.protex.project.codetree.CharEncoding;

/**
 *  @author jatoui
 *  @title Solutions Architect
 *  @email jatoui@blackducksoftware.com
 *  @company Black Duck Software
 *  @year 2012
 **/

public class PathPopulator {

	Logger log = Logger.getLogger(this.getClass().getName());

	private ProtexServerProxyV6_1 proxy;

	private String projectId;
	
	public ProtexServerProxyV6_1 getProxy() {
		return proxy;
	}

	public void setProxy(ProtexServerProxyV6_1 proxy) {
		this.proxy = proxy;
	}

	private StaticLicenseMapper licenseMapper;

	public StaticLicenseMapper getLicenseMapper() {
		return licenseMapper;
	}

	public void setLicenseMapper(StaticLicenseMapper licenseMapper) {
		this.licenseMapper = licenseMapper;
	}

	private List<String> licenseFileNames;

	public List<String> getLicenseFileNames() {
		return licenseFileNames;
	}

	public void setLicenseFileNames(List<String> licenseFileNames) {
		this.licenseFileNames = licenseFileNames;
	}

	public void populatePathsForBomEntries(String projectId,
			HashMap<String, ComponentModel> components, Document doc) {
		
		this.projectId = projectId;
		Elements vrows = doc.select("table[class=reportTable] > tbody > tr");

		for (Element vrow : vrows) {

			Elements vcolumns = vrow.select("td");

			String discType = vcolumns.get(1).text();

			String path = vcolumns.get(2).text();

			String compName = vcolumns.get(6).text();

			String compVersion = vcolumns.get(7).text();

			String licName = vcolumns.get(8).text();

			if (components.containsKey(compName)) {
				components.get(compName).addNewPath(path);
			} else {
				ComponentModel component = new ComponentModel();
				component.setName(compName);
				if (!"".equals(licName)) {
					GlobalLicense license = licenseMapper
							.getLicenseData(licName);
					if (license != null) {
						LicenseModel licenseModel = new LicenseModel();
						licenseModel.setName(licName);
						licenseModel.setText(license.getText());

						component.addNewLicense(licenseModel);
					}

				}
				component.addNewPath(path);
				components.put(compName, component);
			}
			
			boolean isLicenseFilename = false;
			for(String licenseFilename : licenseFileNames)
			{
				if(FilenameUtils.getName(path).endsWith(licenseFilename))
					isLicenseFilename = true;
					
			}
			
			if (isLicenseFilename) {
				String fileText = getFileText(path);
				if(fileText != null)
				{
					LicenseModel licenseModel = new LicenseModel();
					
					licenseModel.setText(fileText);

					components.get(compName).addNewLicense(licenseModel);
				}
			
			}
			
			

		}
	}

	private String getFileText(String path) {
		String fileText = null;

		log.info("hit with license name found for file " + path);

		try {
			// reading the uploaded content of the file from the
			// database
			fileText = new String(proxy.getCodeTreeApi()
					.getFileContent(projectId, path,CharEncoding.NONE));
			
			
//			fileText = new String(Base64.decode(proxy.getCodeTreeApi()
//					.getFileContentAsString(projectId, path,
//							CharEncoding.BASE_64)));
//			fileText = proxy.getCodeTreeApi()
//					.getFileContentAsString(projectId, path,
//					CharEncoding.NONE);
		} catch (SdkFault e) {
			log.warn(
					path
							+ " needs to be re-configured as File Upload type and project re-scanned in order to process by this tool.",
					e);
		} 
//			catch (WSSecurityException e) {
//			log.warn(path
//					+ " could not be decoded from its Base64 representation", e);
//		}

		return fileText;

	}
}
