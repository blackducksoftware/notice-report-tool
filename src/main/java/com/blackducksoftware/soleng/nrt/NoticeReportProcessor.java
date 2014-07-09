package com.blackducksoftware.soleng.nrt;

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

import com.blackducksoftware.soleng.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.soleng.nrt.config.NRTConstants;
import com.blackducksoftware.soleng.nrt.generator.NRTReportGenerator;
import com.blackducksoftware.soleng.nrt.model.ComponentModel;

import soleng.framework.core.config.ConfigConstants.APPLICATION;

/**
 *  This is the main entry point.  Determines which application is being requested
 *  and instantiates the appropriate processor
 *  
 *  @author Ari Kamen
 *  @date Jul 9, 2014
 *
 */
public class NoticeReportProcessor
{
    private Logger log = Logger.getLogger(this.getClass());

    private INoticeReportProcessor bdsProcessor = null;
    private NRTConfigurationManager nrtConfigManager = null;

    public NoticeReportProcessor(String configFileLocation, APPLICATION appType)
	    throws Exception
    {
	nrtConfigManager = new NRTConfigurationManager(configFileLocation,
		appType);

	if (appType == APPLICATION.CODECENTER)
	{
	    bdsProcessor = new CCNoticeReportProcessor(nrtConfigManager,
		    appType);
	} else if (appType == APPLICATION.PROTEX)
	{
	    bdsProcessor = new ProtexNoticeReportProcessor(nrtConfigManager,
		    appType);
	}
    }

    /**
     * Processes the report based on the configuration file.
     * 
     * @throws Exception
     */
    public void processReport() throws Exception
    {
	log.info("Generating Report...");

	File outputFile = calculateReportNameAndLocation(NRTConstants.REPORT_HTML_EXTENSION);

	HashMap<String, ComponentModel> components = null;
	try
	{
	    components = bdsProcessor.processProject();
	} catch (Exception e)
	{
	    throw new Exception("Failure in processing", e);
	}
	
	// Sort the component map to ensure output is alpha sorted
	// We place into TreeMap to ensure order is maintained.
	// The reason we do this after the fact, is because TreeMaps have expensive
	// lookups.  
	List<String> keyList = new ArrayList(components.keySet());
	Collections.sort(keyList);
	
	TreeMap<String, ComponentModel> sortedMap = new TreeMap<String, ComponentModel>();
	for(String key : keyList)
	{
	    sortedMap.put(key, components.get(key));
	}
	

	NRTReportGenerator reportGen = new NRTReportGenerator(nrtConfigManager,
		sortedMap);

	if (nrtConfigManager.isHtmlFileOutput())
	{
	    reportGen.generateHTMLFromTemplate(outputFile);
	    log.info("Finished HTML processing: " + outputFile);
	}

	if (nrtConfigManager.isTextFileOutput())
	{
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
	    throws IOException
    {
	String outputFileName = nrtConfigManager.getOutputFilename();
	File outputFile = null;
	if (outputFileName != null && outputFileName.length() > 0)
	{
	    // Before we copy, replace space encoding if there is one
	    outputFileName = outputFileName.replaceAll(" ", "%20");
	    outputFile = new File(new File("").getAbsolutePath()
		    + File.separator + outputFileName);

	    // If HTML, copy the template over
	    if (extension.equals(NRTConstants.REPORT_HTML_EXTENSION))
	    {
		// Copy the template
		String htmlTemplate = ClassLoader.getSystemResource(
			NRTConstants.HTML_TEMPLATE_FILE).getFile();
		try
		{
		    // Before we copy, replace space encoding if there is one
		    htmlTemplate = htmlTemplate.replaceAll("%20", " ");
		    Files.copy(new File(htmlTemplate).toPath(),
			    outputFile.toPath(),
			    StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e)
		{
		    throw new IOException(
			    "Fatal, unable to prepare HTML remplate: "
				    + e.getMessage());
		}
	    }
	} else
	{
	    outputFile = new File(new File("").getAbsolutePath()
		    + File.separator
		    + NRTConstants.DEFAULT_OUTPUT_HTML_FILENAME_NAME
		    + extension);
	}

	return outputFile;
    }

    /**
     * Strips out offensive characters that may impede write out to disk
     * 
     * @param reportName
     * @return
     */
    private String cleanUpName(String reportName)
    {
	return reportName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    public NRTConfigurationManager getNrtConfigManager()
    {
	return nrtConfigManager;
    }
}
