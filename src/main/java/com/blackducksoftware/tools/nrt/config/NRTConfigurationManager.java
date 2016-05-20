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
package com.blackducksoftware.tools.nrt.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.nrt.model.CustomAttributeRule;
import com.blackducksoftware.tools.nrt.model.CustomAttributeRule.ATTRIBUTE_TYPE;

public class NRTConfigurationManager extends ConfigurationManager {

    final private Logger log = Logger.getLogger(this.getClass());

    // Code Center
    private String ccapplicationName = null;

    private String ccapplicationVersion = null;

    // Protex
    private String protexProjectName;

    private List<String> licenseFilenames;

    private List<String> copyrightPatterns;

    // The file name of the report.
    private String outputFilename;

    // The location on disk of the report
    private String outputFileLocation;

    // Switches
    private Boolean textFileOutput;

    private Boolean htmlFileOutput; // In case user does not want the HTML

    // output.
    private Boolean showFilePaths;

    private Boolean showComponentVersion;

    private Boolean showCopyrights;

    private Boolean includeLicenseFilenamesInReport;

    // Modifiers
    private Integer copyrightContextLength = null;

    // Custom attributes
    private List<CustomAttributeRule> customAttributes = new ArrayList<CustomAttributeRule>();

    // Which BDS application is the user using
    private APPLICATION bdsApplicationType;

    public NRTConfigurationManager(String configFileLocation, APPLICATION app,
            String projectName) throws Exception {
        super(configFileLocation);
        bdsApplicationType = app;
        protexProjectName = projectName;
        initLocalProperties();

    }

    private void initLocalProperties() throws Exception {
        // Load required
        if (bdsApplicationType == APPLICATION.CODECENTER) {
            ccapplicationName = getProperty(NRTConstants.CC_APPLICATION_NAME);
            ccapplicationVersion = getProperty(NRTConstants.CC_APPLICATION_VERSION);
        } else if (bdsApplicationType == APPLICATION.PROTEX) {
            if (protexProjectName == null || protexProjectName.length() == 0) {
                protexProjectName = getProperty(NRTConstants.PROPERY_PROTEX_PROJECT);
            } else {
                log.info("User command line argument project provided: "
                        + protexProjectName);
            }
        } else {
            log.error("Missing valid application type, acceptable types: "
                    + APPLICATION.PROTEX + "," + APPLICATION.CODECENTER);
            throw new Exception("Please specify known application type!");
        }

        // / Load optional
        String patternList = getOptionalProperty(
                NRTConstants.PROPERTY_COPYRIGHT_PATTERNS, "", String.class);
        copyrightPatterns = Arrays.asList(StringUtils.split(patternList, ","));
        String licenseNameList = getOptionalProperty(
                NRTConstants.PROPERTY_LICENSE_FILENAMES, "", String.class);
        licenseFilenames = Arrays.asList(StringUtils
                .split(licenseNameList, ","));

        // Optional filename
        outputFilename = getOptionalProperty(
                NRTConstants.PROPERTY_OUTPUT_FILENAME, "", String.class);
        outputFileLocation = getOptionalProperty(
                NRTConstants.PROPERTY_OUTPUT_LOCATION, "", String.class);

        // Switches
        setHtmlFileOutput(getOptionalProperty(
                NRTConstants.PROPERTY_HTML_FILE_OUTPUT, true, Boolean.class));
        setTextFileOutput(getOptionalProperty(
                NRTConstants.PROPERTY_TEXT_FILE_OUTPUT, false, Boolean.class));
        setIncludeLicenseFilenamesInReport(getOptionalProperty(
                NRTConstants.PROPERTY_INC_LIC_FILENAMES, true, Boolean.class));
        setShowFilePaths(getOptionalProperty(
                NRTConstants.PROPERTY_SHOW_FILE_PATHS, true, Boolean.class));
        setShowComponentVersion(getOptionalProperty(
                NRTConstants.PROPERTY_SHOW_COMP_VER, true, Boolean.class));
        setShowCopyrights(getOptionalProperty(
                NRTConstants.PROPERTY_SHOW_COPYRIGHT, true, Boolean.class));

        // Modifiers
        setCopyrightContextLength(getOptionalProperty(
                NRTConstants.PROPERTY_COPYRIGHT_CONTEXT_LENGTH, 100,
                Integer.class));

        // Custom attributes
        populateAttributes();

    }

    // TODO: In the future, allow uses to specify multiple attributes
    // TODO: Read these configurations from XML, this is a nightmare.
    private void populateAttributes() {
        ATTRIBUTE_TYPE[] attributes = CustomAttributeRule.ATTRIBUTE_TYPE
                .values();
        for (ATTRIBUTE_TYPE attribute : attributes) {
            String keyName = "";
            String keyValue = "";

            if (attribute == ATTRIBUTE_TYPE.FILTER) {
                keyName = NRTConstants.CC_ATTRIBUTE_PREFIX + "."
                        + ATTRIBUTE_TYPE.FILTER.toString().toLowerCase() + "."
                        + NRTConstants.CC_ATTRIBUTE_SUFFIX_NAME;
                keyValue = NRTConstants.CC_ATTRIBUTE_PREFIX + "."
                        + ATTRIBUTE_TYPE.FILTER.toString().toLowerCase() + "."
                        + NRTConstants.CC_ATTRIBUTE_SUFFIX_VALUE;
            } else if (attribute == ATTRIBUTE_TYPE.OVERRIDE) {
                keyName = NRTConstants.CC_ATTRIBUTE_PREFIX + "."
                        + ATTRIBUTE_TYPE.OVERRIDE.toString().toLowerCase()
                        + "." + NRTConstants.CC_ATTRIBUTE_SUFFIX_NAME;
                keyValue = NRTConstants.CC_ATTRIBUTE_PREFIX + "."
                        + ATTRIBUTE_TYPE.OVERRIDE.toString().toLowerCase()
                        + "." + NRTConstants.CC_ATTRIBUTE_SUFFIX_VALUE;
            }
            String caName = getOptionalProperty(keyName);
            if (caName != null) {
                String caValue = getOptionalProperty(keyValue, "", String.class);
                CustomAttributeRule ca = new CustomAttributeRule(attribute,
                        caName.trim(), caValue.trim());
                customAttributes.add(ca);
            }
        }
    }

    /**
     * Retrieves a list of properties that are suitable for export Will be
     * transformed into JSON
     * 
     * @return
     */
    public Map<String, Object> getOptionsForExport() {
        Map<String, Object> options = new HashMap<String, Object>();

        if (bdsApplicationType == APPLICATION.CODECENTER) {
            options.put("project_name", ccapplicationName);
        }
        if (bdsApplicationType == APPLICATION.PROTEX) {
            options.put("project_name", protexProjectName);
        }

        populateOptionsWithFormattedPair(options,
                NRTConstants.PROPERTY_INC_LIC_FILENAMES.replace(".", "_"),
                isIncludeLicenseFilenamesInReport());
        populateOptionsWithFormattedPair(options,
                NRTConstants.PROPERTY_SHOW_FILE_PATHS, isShowFilePaths());
        populateOptionsWithFormattedPair(options,
                NRTConstants.PROPERTY_SHOW_COMP_VER, isShowComponentVersion());
        populateOptionsWithFormattedPair(options,
                NRTConstants.PROPERTY_SHOW_COPYRIGHT, isShowCopyrights());

        return options;
    }

    /**
     * Properly formats the property to make it JSON viable
     * 
     * @param options
     * @param key
     * @param value
     */
    private void populateOptionsWithFormattedPair(Map<String, Object> options,
            String key, Object value) {
        options.put(key.replace(".", "_"), value);
    }

    public String getCCApplicationName() {
        return ccapplicationName;
    }

    public String getCCApplicationVersion() {
        return ccapplicationVersion;
    }

    public String getProjectName() {
        return protexProjectName;
    }

    public void setProjectName(String projectName) {
        protexProjectName = projectName;
    }

    public List<String> getLicenseFilenames() {
        return licenseFilenames;
    }

    public void setLicenseFilenames(List<String> licenseFilenames) {
        this.licenseFilenames = licenseFilenames;
    }

    public List<String> getCopyrightPatterns() {
        return copyrightPatterns;
    }

    public void setCopyrightPatterns(List<String> copyrightPatterns) {
        this.copyrightPatterns = copyrightPatterns;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public Boolean isIncludeLicenseFilenamesInReport() {
        return includeLicenseFilenamesInReport;
    }

    public void setIncludeLicenseFilenamesInReport(
            Boolean includeLicenseFilenamesInReport) {
        this.includeLicenseFilenamesInReport = includeLicenseFilenamesInReport;
    }

    public Boolean isShowCopyrights() {
        return showCopyrights;
    }

    public void setShowCopyrights(Boolean showCopyrights) {
        this.showCopyrights = showCopyrights;
    }

    public Boolean isShowComponentVersion() {
        return showComponentVersion;
    }

    public void setShowComponentVersion(Boolean showComponentVersion) {
        this.showComponentVersion = showComponentVersion;
    }

    public Boolean isShowFilePaths() {
        return showFilePaths;
    }

    public void setShowFilePaths(Boolean showFilePaths) {
        this.showFilePaths = showFilePaths;
    }

    public Boolean isTextFileOutput() {
        return textFileOutput;
    }

    public void setTextFileOutput(Boolean textFileOutput) {
        this.textFileOutput = textFileOutput;
    }

    public List<CustomAttributeRule> getCustomAttributeRules(ATTRIBUTE_TYPE type) {
        List<CustomAttributeRule> attributes = new ArrayList<CustomAttributeRule>();

        for (CustomAttributeRule a : customAttributes) {
            ATTRIBUTE_TYPE attributeType = a.getAttributeType();
            if (attributeType == type) {
                attributes.add(a);
            }
        }

        return attributes;
    }

    public Integer getCopyrightContextLength() {
        return copyrightContextLength;
    }

    private void setCopyrightContextLength(Integer copyrightContextLength) {
        this.copyrightContextLength = copyrightContextLength;
    }

    public Boolean isHtmlFileOutput() {
        return htmlFileOutput;
    }

    private void setHtmlFileOutput(Boolean htmlFileOutput) {
        this.htmlFileOutput = htmlFileOutput;
    }

    public String getOutputFileLocation() {
        return outputFileLocation;
    }

}
