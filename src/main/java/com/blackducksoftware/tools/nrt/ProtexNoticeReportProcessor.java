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
package com.blackducksoftware.tools.nrt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.StringSearchPattern;
import com.blackducksoftware.sdk.protex.common.StringSearchPatternOriginType;
import com.blackducksoftware.sdk.protex.project.codetree.CharEncoding;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNode;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeType;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscoveryWithMatches;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchMatch;
import com.blackducksoftware.sdk.protex.report.ReportSectionType;
import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants.APPLICATION;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.protex.IProtexServerWrapper;
import com.blackducksoftware.tools.connector.protex.ProtexServerWrapper;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;
import com.blackducksoftware.tools.connector.protex.report.Format;
import com.blackducksoftware.tools.connector.protex.report.ReportUtils;
import com.blackducksoftware.tools.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.tools.nrt.config.NRTConstants;
import com.blackducksoftware.tools.nrt.model.ComponentModel;
import com.blackducksoftware.tools.nrt.model.IDFilesElement;
import com.blackducksoftware.tools.nrt.model.LicenseModel;

public class ProtexNoticeReportProcessor implements INoticeReportProcessor {

    final private Logger log = Logger.getLogger(this.getClass());

    private NRTConfigurationManager nrtConfigManager = null;

    private IProtexServerWrapper<ProtexProjectPojo> protexWrapper = null;

    public ProtexNoticeReportProcessor(NRTConfigurationManager manager,
            APPLICATION appType) throws Exception {
        nrtConfigManager = manager;
        protexWrapper = new ProtexServerWrapper<ProtexProjectPojo>(nrtConfigManager, true);
    }

    /**
     * Gets protex elements
     * 
     * @return
     * @throws Exception
     */
    @Override
    public HashMap<String, ComponentModel> processProject(
            String protexProjectName) throws Exception {
        Map<String, StringSearchPattern> patternMap = null;
        HashMap<String, ComponentModel> componentMappings = new HashMap<String, ComponentModel>();

        if (protexProjectName == null) {
            protexProjectName = nrtConfigManager.getProjectName();
        }

        ProjectPojo protexProject = protexWrapper
                .getProjectByName(protexProjectName);

        if (protexProject == null) {
            throw new Exception("Unable to find project with name: "
                    + protexProjectName);
        }

        String projectId = protexProject.getProjectKey();

        HashMap<String, Set<String>> componentToPathMappings = new HashMap<String, Set<String>>();

        log.info("Getting Components by Project");

        List<ComponentModel> protexComponents = protexWrapper.getProjectManager().getComponentsByProjectId(ComponentModel.class, projectId);

        if (nrtConfigManager.isShowFilePaths()
                || nrtConfigManager.isIncludeLicenseFilenamesInReport()) {
            componentToPathMappings = getMappings(protexProject);

            patternMap = buildPatternMap();
        }

        for (ComponentModel protexComp : protexComponents) {

            // Build name version pair key so that we can look up the component from the identified files report
            String versionName = protexComp.getVersion();
            if (versionName == null) {
                versionName = NRTConstants.DEFAULT_VERSION;
            }
            String nameVersionPair = protexComp.getName() + ":" + versionName;

            try {

                Set<String> paths = componentToPathMappings.get(nameVersionPair);
                if (paths == null) {
                    // This should never happen.
                    log.error("Unable to find paths for component, no copyright information will be available for: " + nameVersionPair);
                    continue;

                }

                // Convert common framework licenses to licensemodels
                getLicensesForComponent(projectId, protexComp);

                // Load all the file paths
                getFilesPathsForComponent(protexComp, paths);

                // Load all the copyrights
                getCopyrightsForComponent(projectId,
                        protexComp, patternMap);

            } catch (Exception e) {
                log.warn("Unable to get component information for id: "
                        + nameVersionPair);
            }

            componentMappings.put(nameVersionPair, protexComp);
        }

        // Look into this later This adds user provided licenses
        if (nrtConfigManager.isIncludeLicenseFilenamesInReport()) {
            for (ComponentModel model : componentMappings.values()) {
                for (String licenseFilename : nrtConfigManager
                        .getLicenseFilenames()) {
                    Set<String> paths = model.getPaths();
                    if (paths != null) {
                        for (String path : paths) {
                            if (FilenameUtils.getName(path).endsWith(
                                    licenseFilename)) {
                                String fileText = getFileText(projectId, path);
                                if (fileText != null) {
                                    LicenseModel licenseModel = new LicenseModel();
                                    licenseModel.setName(licenseFilename);
                                    licenseModel.setId("custom_included_+"
                                            + licenseFilename);
                                    licenseModel.setText(fileText);

                                    model.addNewLicense(licenseModel);
                                }
                            }
                        }
                    } else {
                        log.info("No paths for component: "
                                + model.getNameAndVersion());
                    }
                }
            }
        }

        return componentMappings;
    }

    /**
     * @return
     */
    private Map<String, StringSearchPattern> buildPatternMap() {
        Map<String, StringSearchPattern> patternMap = new HashMap<>();

        for (String copyright : nrtConfigManager.getCopyrightPatterns()) {
            StringSearchPattern pattern = null;
            try {
                pattern = protexWrapper.getInternalApiWrapper()
                        .getPolicyApi()
                        .getStringSearchPatternByName(copyright);
                if (pattern != null) {
                    patternMap.put(pattern.getStringSearchPatternId(),
                            pattern);
                }
            } catch (Exception e) {
                log.warn("Unable to find search pattern object for user specified string: "
                        + copyright);
            }
        }

        return patternMap;
    }

    /**
     * Gathers copyright information for each component Will only work if the
     * user enabled the file paths information as well, otherwise without
     * filepaths it is not possible to get copyright information.
     * 
     * The copyright information, is just a lookup on search patterns.
     * Realistically it could be any pattern.
     * 
     * @param projectId
     * @param component
     * @param componentModel
     * @param patternMap
     */
    private void getCopyrightsForComponent(String projectId,
            ComponentModel componentModel, Map<String, StringSearchPattern> patternMap) {
        if (nrtConfigManager.isShowCopyrights()
                && nrtConfigManager.isShowFilePaths()) {
            log.info("Getting Copyright Info for component: "
                    + componentModel.getNameAndVersion());

            try {
                List<CodeTreeNode> treeNodes = new ArrayList<CodeTreeNode>();
                for (String path : componentModel.getPaths()) {
                    CodeTreeNode node = new CodeTreeNode();
                    node.setName(path);
                    node.setNodeType(CodeTreeNodeType.FILE);
                    treeNodes.add(node);
                }

                List<StringSearchPatternOriginType> patternTypes = new ArrayList<StringSearchPatternOriginType>();

                patternTypes.add(StringSearchPatternOriginType.CUSTOM);
                patternTypes.add(StringSearchPatternOriginType.STANDARD);
                patternTypes.add(StringSearchPatternOriginType.PROJECT_LOCAL);

                List<StringSearchDiscovery> searchDiscoveries = protexWrapper
                        .getInternalApiWrapper()
                        .getDiscoveryApi()
                        .getStringSearchDiscoveries(projectId, treeNodes,
                                patternTypes);

                log.debug("Found search discovery count: "
                        + searchDiscoveries.size());

                for (StringSearchDiscovery searchDiscovery : searchDiscoveries) {
                    Integer contextLength = nrtConfigManager
                            .getCopyrightContextLength();
                    log.debug("Context length: " + contextLength);

                    StringSearchDiscoveryWithMatches discoveryMatch = protexWrapper
                            .getInternalApiWrapper()
                            .getDiscoveryApi()
                            .getStringSearchMatches(projectId, searchDiscovery,
                                    contextLength);

                    StringSearchPattern userSpecifiedPattern = patternMap
                            .get(discoveryMatch.getStringSearchId());
                    if (userSpecifiedPattern != null) {
                        log.debug("Found search match for discovery: "
                                + searchDiscovery.getStringSearchId());
                        List<StringSearchMatch> matches = discoveryMatch
                                .getMatches();
                        log.debug("Found matches for discovery: "
                                + matches.size());
                        for (StringSearchMatch match : matches) {
                            String foundMatch = new String(match.getContext());
                            componentModel.addNewCopyright(foundMatch);

                        }
                    }

                }

            } catch (Exception e) {
                log.error("Unable to get search pattern information: "
                        + e.getMessage());
            }

        }

    }

    /**
     * If user wants to see file paths, process
     * 
     * @param component
     * @param componentModel
     * @param componentToPathMappings
     */
    private void getFilesPathsForComponent(ComponentModel componentModel, Set<String> paths) {
        // Filepaths if necessary
        if (nrtConfigManager.isShowFilePaths()) {
            log.debug("Gathering file paths for component: "
                    + componentModel.getNameAndVersion());
            if (paths == null)
            {
                log.error("No paths available for component: " + componentModel.getComponentId());
                return;
            }

            try {
                for (String path : paths) {
                    componentModel.addNewPath(path);
                }

            } catch (Exception e) {
                log.error("Error getting file paths for: "
                        + componentModel.getNameAndVersion());
                log.error(e.getMessage());
            }

        }

    }

    private void getLicensesForComponent(String projectId, ComponentModel componentModel) {

        List<ProtexLicensePojo> licensePojos = componentModel.getLicenses();
        if (licensePojos != null)
        {
            for (ProtexLicensePojo license : licensePojos)
            {
                // License information
                LicenseModel licenseModel = new LicenseModel();

                licenseModel.setId(license.getId());
                licenseModel.setName(license.getName());
                licenseModel.setText(license.getLicenseText());

                componentModel.addNewLicense(licenseModel);

            }
        }
        else
        {
            log.warn("No license information available for: " + componentModel.getNameAndVersion());
        }
    }

    private String getFileText(String projectId, String path) {
        String fileText = null;

        log.info("hit with license name found for file " + path);

        try {
            // reading the uploaded content of the file from the
            // database
            fileText = new String(protexWrapper.getInternalApiWrapper()
                    .getCodeTreeApi()
                    .getFileContent(projectId, path, CharEncoding.NONE));

        } catch (SdkFault e) {
            log.warn(
                    path
                            + " needs to be re-configured as File Upload type and project re-scanned in order to process by this tool.",
                    e);
        }

        return fileText;

    }

    /**
     * This is currently the fastest way to get identified files per component
     * The SDK currently does not expose any faster way to get Paths per
     * component.
     * 
     * @param pojo
     * @return
     * @throws Exception
     */
    private HashMap<String, Set<String>> getMappings(ProjectPojo pojo)
            throws Exception {
        HashMap<String, Set<String>> componentToPathMapping = new HashMap<String, Set<String>>();

        ReportUtils reportUtils = new ReportUtils();

        List<IDFilesElement> idElements =
                reportUtils.getReportSection(protexWrapper, pojo, ReportSectionType.IDENTIFIED_FILES.toString(), Format.CSV, IDFilesElement.class);

        log.info("Parsing identified files...");
        for (IDFilesElement idElement : idElements) {
            String compName = idElement.getValue("Component");
            String compVersion = idElement.getValue("Version");
            String path = idElement.getValue("File/Folder");
            // Key must be more than component name, as the name is not guaranteed unique.
            String key = compName + ":" + compVersion;

            Set<String> paths = componentToPathMapping.get(key);
            if (paths == null) {
                paths = new HashSet<String>();
                componentToPathMapping.put(key, paths);
            }
            paths.add(path);
        }

        return componentToPathMapping;
    }

}
