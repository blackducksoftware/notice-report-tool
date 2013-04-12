package com.blackducksoftware.sdk.notice.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.blackducksoftware.sdk.notice.model.ComponentModel;
import com.blackducksoftware.sdk.notice.model.LicenseModel;
import com.blackducksoftware.sdk.protex.license.GlobalLicense;

/**
 * @author jatoui
 * @title Solutions Architect
 * @email jatoui@blackducksoftware.com
 * @company Black Duck Software
 * @year 2012
 **/

public class StringSearchPopulator {

	Logger log = Logger.getLogger(this.getClass().getName());

	private static final int PATTERN_NAME_INDEX = 0;

	private static final int FILE_PATH_INDEX = 2;

	private static final int MATCH_INDEX = 5;
	
	private List<String> copyrightPatterns;

	private StaticSearchStringMapper searchMapper;

	private StaticLicenseMapper licenseMapper;

	public StaticLicenseMapper getLicenseMapper() {
		return licenseMapper;
	}

	public void setLicenseMapper(StaticLicenseMapper licenseMapper) {
		this.licenseMapper = licenseMapper;
	}

	public StaticSearchStringMapper getSearchMapper() {
		return searchMapper;
	}

	public void setSearchMapper(StaticSearchStringMapper searchMapper) {
		this.searchMapper = searchMapper;
	}

	public List<String> getCopyrightPatterns() {
		return copyrightPatterns;
	}

	public void setCopyrightPatterns(List<String> copyrightPatterns) {
		this.copyrightPatterns = copyrightPatterns;
	}

	private HashMap<String, List<String>> pathToCopyrightMapping = new HashMap<String, List<String>>();

	private HashMap<String, List<String>> pathToLicenseIdMapping = new HashMap<String, List<String>>();

	// reads the data in the Searches report encapsulated into the Document object
	// and fills into the components list
	public void populateCopyrightsForComponent(List<ComponentModel> components,
			Document doc) {
		
		//builds pathToCopyrightMapping and pathToLicenseMapping has tables
		populateCopyrightsForPaths(doc);

		//for all paths for a Component, add all the copyright and licenses to the ComponentModel object
		for (ComponentModel component : components) {
			for (String path : component.getPaths()) {
				if (pathToCopyrightMapping.get(path) != null)

					for (String copyright : pathToCopyrightMapping.get(path))
						component.addNewCopyright(copyright);

				if (pathToLicenseIdMapping.get(path) != null)

					for (String licenseId : pathToLicenseIdMapping.get(path)) {

						GlobalLicense lic = licenseMapper
								.getLicenseDataById(licenseId);
						LicenseModel license = new LicenseModel();
						license.setId(licenseId);
						license.setName(lic.getName());
						license.setText(lic.getText());
						component.addNewLicense(license);
					}
			}
		}

	}

	
	private void populateCopyrightsForPaths(Document doc) {

		Elements vrows = doc.select("table[class=reportTable] > tbody > tr");

		for (Element vrow : vrows) {

			Elements vcolumns = vrow.select("td");

			String patternName = vcolumns.get(PATTERN_NAME_INDEX).text();

			String path = "/" + vcolumns.get(FILE_PATH_INDEX).text();

			String match = vcolumns.get(MATCH_INDEX).html();

			String licenseId = searchMapper.getLicenseData(patternName);

			if (licenseId != null) {

				List<String> licenseIds = pathToLicenseIdMapping.get(path);

				if (licenseIds == null) {
					licenseIds = new ArrayList<String>();
					pathToLicenseIdMapping.put(path, licenseIds);
				}

				if (!licenseIds.contains(licenseId)) {
					licenseIds.add(licenseId);
				}
			}

			if (match.equals("[no source information available]")) // special
																	// designation
																	// for
																	// matches
																	// to
																	// file
																	// with
																	// patterns
																	// with
																	// Upload
																	// File
																	// option=No
			{
				log.warn("Cannot determine String Searches for file "
						+ path
						+ " due to pattern not having Upload Code option = true");
			} else {
				//if String Search pattern is designates a Copyright
				if (copyrightPatterns.contains(patternName)) {
					List<String> copyrights = pathToCopyrightMapping.get(path);

					if (copyrights == null) {
						copyrights = new ArrayList<String>();
						pathToCopyrightMapping.put(path, copyrights);
					}

					int index = match.indexOf("</i>");

					String copyrightStringToAdd = match.substring(index + 5)
							.replaceAll("\\r\\n|\\r|\\n", " ");
					log.info("Entering copyright information for file: " + path
							+ " copyright= " + copyrightStringToAdd);
					if (!copyrights.contains(copyrightStringToAdd))
						copyrights.add(copyrightStringToAdd);

				}

			}
		}

	}
}
