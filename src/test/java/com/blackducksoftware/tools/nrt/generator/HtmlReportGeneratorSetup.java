/*******************************************************************************
 * Copyright (C) 2015 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/
package com.blackducksoftware.tools.nrt.generator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.junit.Assert;

import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants.APPLICATION;
import com.blackducksoftware.tools.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.tools.nrt.generator.NRTReportGenerator;
import com.blackducksoftware.tools.nrt.model.ComponentModel;
import com.blackducksoftware.tools.nrt.model.LicenseModel;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Common setup for the various html generator tests
 * 
 * @author akamen
 * 
 */
public class HtmlReportGeneratorSetup {
    // The HTML template file used for setting up our html
    protected static String htmlTemplate = "html_template.html";
    protected static File basicReportOutputLocation = null;
    protected static NRTReportGenerator generator = null;

    protected static String COMP_ONE_NAME = "ComponentOne";
    protected static String COMP_TWO_NAME = "ComponentTwo";

    // Container for the data
    protected static TreeMap<String, ComponentModel> testComponents = null;
    protected static NRTConfigurationManager configManager = null;

    protected static NRTReportGenerator setupFiles(String configFile,
	    File basicReportOutputLocation) throws IOException {
	NRTReportGenerator generator = null;
	String fullConfigFileLocation = ClassLoader.getSystemResource(
		configFile).getFile();

	// Using code center, but it does not really matter.
	try {
	    // Simulating a project that is not passed in via command line
	    String projectName = null;
	    configManager = new NRTConfigurationManager(fullConfigFileLocation,
		    APPLICATION.CODECENTER, projectName);
	} catch (Exception e1) {
	    Assert.fail(e1.getMessage());
	}

	populateComponents();

	generator = new NRTReportGenerator(configManager, testComponents);

	// Write it out
	try {
	    generator.generateHTMLFromTemplate(basicReportOutputLocation);
	} catch (Exception e) {
	    Assert.fail(e.getMessage());
	}

	return generator;
    }

    public static void populateComponents() {
	/**
	 * Create two basic components
	 */
	testComponents = new TreeMap<String, ComponentModel>();

	// Fake out a license
	LicenseModel licModelOne = createTestLicense("apache_id_fake",
		"FakeApache", "Fake Apache text");
	LicenseModel licModelTwo = createTestLicense("mit_id_fake", "FakeMIT",
		"This is a bunch of MIT text with some <b>HTML</b> thrown in for good measure.");

	// Create components
	List<LicenseModel> testLicenseList = new ArrayList<LicenseModel>();
	testLicenseList.add(licModelOne);
	ComponentModel modelOne = createTestComponent(COMP_ONE_NAME, "1.0",
		"http://google.com", testLicenseList);
	testLicenseList.add(licModelTwo);
	ComponentModel modelTwo = createTestComponent(COMP_TWO_NAME, "2.3",
		"http://github.com", testLicenseList);

	// Add file paths to one
	String filePathOne = "//somepath//test//";
	String filePathTwo = "//somePathTwo//testTwo";
	modelOne.addNewPath(filePathOne);
	modelOne.addNewPath(filePathTwo);
	// Add copyrights to another
	String copyRightOne = "Copyright Test One";
	String copyRightTwo = "Copyright Test Two";
	modelTwo.addNewCopyright(copyRightOne);
	modelTwo.addNewCopyright(copyRightTwo);

	testComponents.put(modelOne.getName(), modelOne);
	testComponents.put(modelTwo.getName(), modelTwo);

    }

    private static LicenseModel createTestLicense(String id, String name,
	    String text) {
	LicenseModel model = new LicenseModel();

	model.setId(id);
	model.setName(name);
	model.setText(text);

	return model;
    }

    private static ComponentModel createTestComponent(String name,
	    String version, String homePage, List<LicenseModel> licList) {
	ComponentModel model = new ComponentModel();

	model.setComponentId(name + version);
	model.setHomePage(homePage);
	model.setName(name);
	model.setVersion(version);

	for (LicenseModel lic : licList)
	    model.addNewLicense(lic);

	return model;
    }

    /**
     * Wraps the given classname in the format needed by HtmlUnit to perform an
     * XPath lookup Format: ("//div[@class='class1Name']");
     * 
     * @param className
     * @return
     */
    protected static String getXPathName(String className) {
	String xPathName = "//div[@class='" + className + "']";
	return xPathName;
    }

    /**
     * Returns an HTMLUnit object
     * 
     * @return
     */
    protected static HtmlPage getDocForBasicReport() {
	HtmlPage doc = null;

	try {
	    WebClient webClient = new WebClient();
	    doc = webClient.getPage(basicReportOutputLocation.toURL());
	} catch (IOException e) {
	    Assert.fail("Could not read html report: " + e.getMessage());
	}
	return doc;
    }

}