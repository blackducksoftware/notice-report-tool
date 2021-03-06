/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 *******************************************************************************/
package com.blackducksoftware.tools.nrt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants.APPLICATION;
import com.blackducksoftware.tools.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.tools.nrt.config.NRTConstants;
import com.blackducksoftware.tools.nrt.generator.NRTReportGenerator;
import com.blackducksoftware.tools.nrt.model.ComponentModel;

/**
 * This is the main entry point. Determines which application is being requested
 * and instantiates the appropriate processor
 * 
 * @author Ari Kamen
 * @date Jul 9, 2014
 * 
 */
public class NoticeReportProcessor {
    final private Logger log = Logger.getLogger(this.getClass());

    private INoticeReportProcessor bdsProcessor;

    private NRTConfigurationManager nrtConfigManager;

    private File outputFile;

    private String projectName;

    private APPLICATION appType;

    public NoticeReportProcessor(String configFileLocation,
            APPLICATION appType, String projectName) throws Exception {
        if (projectName != null && projectName.length() > 0) {
            this.projectName = projectName;
        }

        this.appType = appType;

        nrtConfigManager = new NRTConfigurationManager(configFileLocation,
                appType, projectName);

        outputFile = calculateReportNameAndLocation(NRTConstants.REPORT_HTML_EXTENSION);
    }

    /**
     * @throws Exception
     * 
     */
    public void connect() throws Exception {
        if (appType == APPLICATION.CODECENTER) {
            bdsProcessor = new CCNoticeReportProcessor(nrtConfigManager);
        } else if (appType == APPLICATION.PROTEX) {
            bdsProcessor = new ProtexNoticeReportProcessor(nrtConfigManager);
        }
    }

    /**
     * Processes the report based on the configuration file.
     * 
     * @throws Exception
     */
    public void processReport() throws Exception {
        log.info("Generating Report...");

        if (bdsProcessor == null) {
            throw new Exception("NRT Processor not configured!");
        }

        HashMap<String, ComponentModel> components = null;
        try {
            components = bdsProcessor.processProject(projectName);
        } catch (Exception e) {
            throw new Exception("Failure in processing", e);
        }

        // Sort the component map to ensure output is alpha sorted
        // We place into TreeMap to ensure order is maintained.
        // The reason we do this after the fact, is because TreeMaps have
        // expensive
        // lookups.
        List<String> keyList = new ArrayList<String>(components.keySet());
        Collections.sort(keyList);

        TreeMap<String, ComponentModel> sortedMap = new TreeMap<String, ComponentModel>();
        for (String key : keyList) {
            sortedMap.put(key, components.get(key));
        }

        NRTReportGenerator reportGen = new NRTReportGenerator(nrtConfigManager,
                sortedMap);

        if (nrtConfigManager.isHtmlFileOutput()) {
            reportGen.generateHTMLFromTemplate(outputFile);
            log.info("Finished HTML processing: " + outputFile);
        }

        if (nrtConfigManager.isTextFileOutput()) {
            log.info("Generating text output");
            reportGen.generateTextReport(nrtConfigManager.getProjectName());
            log.info("Finished text processing: " + outputFile);
        }

        log.info("Done!");
    }

    /**
     * Prepares the final report location by copying the template from the
     * supplied resources and creates an appropriate name depending on
     * configuration settings.
     * 
     * @param extension
     * @return
     * @throws IOException
     */
    public File calculateReportNameAndLocation(String extension)
            throws IOException {
        String outputFileName = nrtConfigManager.getOutputFilename();
        String outputFileLocation = nrtConfigManager.getOutputFileLocation();

        outputFileLocation = determineLocation(outputFileLocation);

        File outputFile = null;
        if (outputFileName != null && outputFileName.length() > 0) {
            outputFileName = outputFileName + extension;
            outputFile = new File(outputFileLocation + File.separator
                    + outputFileName);
        } else {
            // If the output file name is not provided, then use the project
            // name
            outputFileName = nrtConfigManager.getProjectName() + extension;
            outputFile = new File(
                    new File(outputFileLocation).getAbsolutePath()
                            + File.separator + outputFileName);
        }

        // Before we copy, replace space encoding if there is one
        outputFileName = cleanUpName(outputFileName);

        // If HTML, copy the template over
        if (extension.equals(NRTConstants.REPORT_HTML_EXTENSION)) {
            // Copy the template
            String htmlTemplate = ClassLoader.getSystemResource(
                    NRTConstants.HTML_TEMPLATE_FILE).getFile();
            try {
                // Before we copy, replace space encoding if there is one
                htmlTemplate = htmlTemplate.replaceAll("%20", " ");
                Files.copy(new File(htmlTemplate).toPath(),
                        outputFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException(
                        "Fatal, unable to prepare HTML remplate: "
                                + e.getMessage());
            }
        }

        return outputFile;
    }

    /**
     * Determines if location exists, if not returns absolute path of existing
     * location
     * 
     * @param outputFileLocation
     * @return
     * @throws Exception
     */
    private String determineLocation(String outputFileLocation)
            throws IOException {
        String location = "";
        File f = null;
        if (outputFileLocation.length() > 0) {
            f = new File(outputFileLocation);
            if (!f.exists()) {
                throw new IOException(
                        "The location specified by the user does not exist: "
                                + f);
            }
        } else {
            f = new File("");

        }
        log.debug("Report location at: " + location);
        location = f.getAbsolutePath();
        return location;

    }

    /**
     * Strips out offensive characters that may impede write out to disk
     * 
     * @param reportName
     * @return
     */
    private String cleanUpName(String reportName) {
        return reportName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    public NRTConfigurationManager getNrtConfigManager() {
        return nrtConfigManager;
    }

}
