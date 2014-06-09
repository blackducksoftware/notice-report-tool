package com.blackducksoftware.soleng.nrt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import soleng.framework.core.config.ConfigConstants.APPLICATION;
import soleng.framework.standard.common.ProjectPojo;
import soleng.framework.standard.protex.ProtexServerWrapper;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.Component;
import com.blackducksoftware.sdk.protex.common.ComponentType;
import com.blackducksoftware.sdk.protex.common.StringSearchPattern;
import com.blackducksoftware.sdk.protex.common.StringSearchPatternOriginType;
import com.blackducksoftware.sdk.protex.component.custom.CustomComponent;
import com.blackducksoftware.sdk.protex.component.standard.StandardComponent;
import com.blackducksoftware.sdk.protex.license.License;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.license.LicenseOriginType;
import com.blackducksoftware.sdk.protex.project.bom.BomComponent;
import com.blackducksoftware.sdk.protex.project.codetree.CharEncoding;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNode;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeType;
import com.blackducksoftware.sdk.protex.project.codetree.PartialCodeTree;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscoveryWithMatches;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchMatch;
import com.blackducksoftware.sdk.protex.project.localcomponent.LocalComponent;
import com.blackducksoftware.sdk.protex.report.ReportSectionType;
import com.blackducksoftware.soleng.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.soleng.nrt.model.ComponentModel;
import com.blackducksoftware.soleng.nrt.model.IDFilesElement;
import com.blackducksoftware.soleng.nrt.model.LicenseModel;

public class ProtexNoticeReportProcessor implements INoticeReportProcessor 
{

	private Logger log = Logger.getLogger(this.getClass());
	

	private NRTConfigurationManager nrtConfigManager = null;
	private ProtexServerWrapper protexWrapper = null;

	public ProtexNoticeReportProcessor(NRTConfigurationManager manager, APPLICATION appType) throws Exception 
	{
		nrtConfigManager = manager;
		protexWrapper = new ProtexServerWrapper(nrtConfigManager.getServerBean(), nrtConfigManager, true);
	}
	
	/**
	 * Gets protex elements
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, ComponentModel> processProject() throws Exception 
	{
		HashMap<String, ComponentModel> componentMappings = new HashMap<String, ComponentModel>();
	
		String protexProjectName = nrtConfigManager.getProjectName();
		ProjectPojo protexProject = protexWrapper.getProjectByName(protexProjectName);
		
		if(protexProject == null)
			throw new Exception("Unable to find project with name: " + protexProjectName);

		String projectId = protexProject.getProjectKey();

		HashMap<String, Set<String>> componentToPathMappings = new HashMap<String, Set<String>>();
		
		if (nrtConfigManager.isShowFilePaths() || nrtConfigManager.isIncludeLicenseFilenamesInReport())
			componentToPathMappings = this.getMappings(protexProject);
		
		log.info("Getting Components");
		List<BomComponent> bomComps = protexWrapper.getInternalApiWrapper().bomApi.getBomComponents(projectId);
		for(BomComponent bomcomponent : bomComps)
		{
			// Common bomcomponent actions
			ComponentModel componentModel = new ComponentModel();
			componentModel.setComponentId(bomcomponent.getComponentId());
			componentModel.setVersion(bomcomponent.getBomVersionName());
			getLicensesForComponent(projectId, bomcomponent.getLicenseInfo(), componentModel);
					
			try{
				Component component = protexWrapper.getInternalApiWrapper().projectApi.
						getComponentById(projectId, bomcomponent.getComponentId());
				
				if(component != null)
				{
					
					// Common component actions
					componentModel.setName(component.getName());		
					
					// Mommy, help me.  In 6.3 SDK there is no way around
					// this painful "instanceof" emulation.  Waiting for 7.x to
					// refactor the hell out of this thing. 
					if(component.getType() == ComponentType.CUSTOM)
					{
						CustomComponent cc = (CustomComponent)component;
						componentModel.setHomePage(cc.getHomePage());
					}
					else if(component.getType() == ComponentType.LOCAL)
					{
						LocalComponent lc = (LocalComponent)component;
						componentModel.setHomePage(lc.getHomePage());
					}
					else if(component.getType() == ComponentType.STANDARD)
					{
						StandardComponent sc = (StandardComponent)component;
						componentModel.setHomePage(sc.getHomePage());
					
					}	
					else if(component.getType() == ComponentType.PROJECT)
					{
						log.info("Skipping component project: " + componentModel.getName());
						continue;
					}
					
					// Load all the file paths
					getFilesPathsForComponent(component, componentModel, componentToPathMappings);
						
					// Load all the copyrights
					getCopyrightsForComponent(projectId, component, componentModel);
						
				}
			} catch (Exception e)
			{
				log.warn("Unable to get component information for id: " + bomcomponent.getComponentId());
			}

			componentMappings.put(componentModel.getComponentId(), componentModel);
		}
	
		/**
		 * TODO:  Look into this later
		 * This adds user provided licenses
		 */
		if (nrtConfigManager.isIncludeLicenseFilenamesInReport())		
		{
				for (String componentKey : componentToPathMappings.keySet()) 
				{
					ComponentModel model = componentMappings.get(componentKey);
					
					for (String licenseFilename : nrtConfigManager.getLicenseFilenames()) 
					{
						for(String path : model.getPaths())
						{
							if (FilenameUtils.getName(path).endsWith(licenseFilename)) 
							{
								String fileText = getFileText(projectId, path);
								if (fileText != null) 
								{
									LicenseModel licenseModel = new LicenseModel();
									licenseModel.setId("custom_included_+" + licenseFilename);
									licenseModel.setText(fileText);
		
									model.addNewLicense(licenseModel);
								}
							}
						}
					}
				}
			}


		return componentMappings;
	}
	
	/**
	 * Gathers copyright information for each component
	 * Will only work if the user enabled the file paths information as well, 
	 * otherwise without filepaths it is not possible to get copyright information.
	 * 
	 * The copyright information, is just a lookup on search patterns.  Realistically it could be
	 * any pattern.
	 * 
	 * @param projectId
	 * @param component
	 * @param componentModel
	 */
	private void getCopyrightsForComponent(String projectId, Component component, ComponentModel componentModel) 
	{
		if(nrtConfigManager.isShowCopyrights() && nrtConfigManager.isShowFilePaths())
		{				
			log.info("Getting Copyright Info for component: " + componentModel.getNameAndVersion());
			Map<String, StringSearchPattern> patternMap = new HashMap<>();
			
			for(String copyright : nrtConfigManager.getCopyrightPatterns())
			{
				StringSearchPattern pattern = null;
				try{
					pattern = protexWrapper.getInternalApiWrapper().policyApi.getStringSearchPatternByName(copyright);
					if(pattern != null)
						patternMap.put(pattern.getStringSearchPatternId(), pattern);
				} catch (Exception e)
				{
					log.warn("Unable to find search pattern object for user specified string: " + copyright);
				}
			}
			
			try
			{					
				PartialCodeTree tree = new PartialCodeTree();
				tree.setParentPath("/");
				for(String path : componentModel.getPaths())
				{
					CodeTreeNode node = new CodeTreeNode();
					node.setName(path);
					node.setNodeType(CodeTreeNodeType.FILE);
					tree.getNodes().add(node);
				}
				
				List<StringSearchPatternOriginType> patternTypes = new ArrayList<StringSearchPatternOriginType>();
				patternTypes.add(StringSearchPatternOriginType.CUSTOM);
				patternTypes.add(StringSearchPatternOriginType.STANDARD);
				patternTypes.add(StringSearchPatternOriginType.PROJECT_LOCAL);
				
				List<StringSearchDiscovery> searchDiscoveries = 
						protexWrapper.getInternalApiWrapper().discoveryApi.getStringSearchDiscoveries(projectId, tree, patternTypes);
				
				log.debug("Found search discovery count: " + searchDiscoveries.size());
				
				for(StringSearchDiscovery searchDiscovery : searchDiscoveries)
				{
					Integer contextLength = nrtConfigManager.getCopyrightContextLength();
					log.debug("Context length: " + contextLength);
					
					StringSearchDiscoveryWithMatches discoveryMatch  =  
							protexWrapper.getInternalApiWrapper().discoveryApi.
							getStringSearchMatches(projectId, searchDiscovery, contextLength);
					
					StringSearchPattern userSpecifiedPattern = patternMap.get(discoveryMatch.getStringSearchId());
					if(userSpecifiedPattern != null)
					{
						log.debug("Found search match for discovery: " + searchDiscovery.getStringSearchId());
						List<StringSearchMatch> matches = discoveryMatch.getMatches();
						log.debug("Found matches for discovery: " + matches.size());
						for(StringSearchMatch match : matches)
						{
							String foundMatch = new String(match.getContext());
							componentModel.addNewCopyright(foundMatch);
							
						}
					}
				
				}
				
			} catch (Exception e)
			{
				log.error("Unable to get search pattern information: " + e.getMessage());
			}
			
		}
		
	}

	/**
	 * If user wants to see file paths, process 
	 * @param component
	 * @param componentModel
	 * @param componentToPathMappings
	 */
	private void getFilesPathsForComponent(Component component, ComponentModel componentModel, HashMap<String, Set<String>> componentToPathMappings) 
	{
		// Filepaths if necessary
		if(nrtConfigManager.isShowFilePaths())
		{
			log.debug("Gathering file paths for component: " + component.getComponentId());
			
			try
			{
				Set<String> paths = componentToPathMappings.get(componentModel.getNameAndVersion());
				for(String path : paths)
				{
					componentModel.addNewPath(path);
				}
				
			} catch (Exception e)
			{
				log.error("Error getting identifications: " + e.getMessage());
			}
			
		}
		
	}

	/**
	 * Gets the license information for the license associated to the project's component.
	 * @param licenseInfo
	 * @param componentModel
	 */
	private void getLicensesForComponent(String projectId, LicenseInfo licenseInfo, ComponentModel componentModel) 
	{
		// License information
		LicenseModel licenseModel = new LicenseModel();
		
		licenseModel.setId(licenseInfo.getLicenseId());
		licenseModel.setName(licenseInfo.getName());
		

		License lic = null;
		
		try
		{
			lic=	protexWrapper.getInternalApiWrapper().
			projectApi.getLicenseById(projectId, licenseModel.getId());
		} catch (Exception e)
		{
			log.warn("Error getting license from server for license name: " + licenseModel.getName());
		}
		
		
		LicenseOriginType originType = lic.getLicenseOriginType();
		licenseModel.setLicenseOriginType(originType.toString());
		
		licenseModel.setText(new String(lic.getText()));
		componentModel.addNewLicense(licenseModel);
			
	
	
	}

	private String getFileText(String projectId, String path) {
		String fileText = null;

		log.info("hit with license name found for file " + path);

		try {
			// reading the uploaded content of the file from the
			// database
			fileText = new String(protexWrapper.getInternalApiWrapper().codeTreeApi.getFileContent(projectId, path,
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
	 * This is currently the fastest way to get identified files per component
	 *  The SDK currently does not expose any faster way to get Paths per component.
		
	 * @param pojo
	 * @return
	 * @throws Exception
	 */
	private HashMap<String, Set<String>> getMappings(ProjectPojo pojo) throws Exception 
	{
		HashMap<String, Set<String>> componentToPathMapping = new HashMap<String, Set<String>>();

		log.info("Getting identified files section report");
		List<IDFilesElement> idElements = 
				protexWrapper.getReportSection(pojo, ReportSectionType.IDENTIFIED_FILES.toString(), IDFilesElement.class);
	
		log.info("Parsing identified files...");
		for(IDFilesElement idElement : idElements)
		{
			String compName = idElement.getValue("Component");
			String compVersion = idElement.getValue("Version");
			String path = idElement.getValue("File/Folder");
			
			String key = compName + ":" + compVersion;
			
			Set<String> paths = componentToPathMapping.get(key);
			if(paths == null)
			{
				paths = new HashSet<String>();
				componentToPathMapping.put(key, paths);
			}
			paths.add(path);
		}

		return componentToPathMapping;
	}
	
}