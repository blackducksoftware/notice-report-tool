package com.blackducksoftware.tools.nrt.generator;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants.APPLICATION;
import com.blackducksoftware.tools.nrt.NoticeReportProcessor;
import com.blackducksoftware.tools.nrt.config.NRTConstants;

/**
 * Tests the basic functions of the NRT Processor
 * @author akamen
 *
 */
public class NoticeReportProcessorTest 
{
	private static String configFileName = "nrt_config_basic.properties";
	private static NoticeReportProcessor reportProcessor = null;
	
	@ClassRule 
    public static TemporaryFolder junitWorkingFolder = new TemporaryFolder();
	
	@BeforeClass 
	public static void setupFiles() throws Exception	
	{		
		String configFile = ClassLoader.getSystemResource(configFileName).getFile();
		
		// Project name derived from config file, not command line	
		String pojectName = "DefaultProject";
		reportProcessor = new NoticeReportProcessor(configFile, APPLICATION.CODECENTER, pojectName);
		
	}	
	
	/**
	 * Tests the default copy, where the config file does not specify anything.
	 * @throws IOException 
	 * @throws Exception 
	 */
	@Test
	public void testDefaultHTMLFileCopy() throws IOException 
	{		
		String expectedName = "DefaultProject.html";
	
			File finalFile = 
					reportProcessor.calculateReportNameAndLocation(NRTConstants.REPORT_HTML_EXTENSION);

			Assert.assertEquals(expectedName, finalFile.getName());

	}
	
	/**
	 * If a user supplies a file name.
	 * @throws IOException 
	 */
	@Test
	public void testUserSuppliedFileName() throws IOException
	{
		String userSuppliedName = "MyReport";
		String originalName = reportProcessor.getNrtConfigManager().getOutputFilename();
		
		// Set our name
		reportProcessor.getNrtConfigManager().setOutputFilename(userSuppliedName);
		File finalFile = 
				reportProcessor.calculateReportNameAndLocation(NRTConstants.REPORT_HTML_EXTENSION);
		
		Assert.assertEquals(userSuppliedName+".html", finalFile.getName());
		
		// cleanup
		reportProcessor.getNrtConfigManager().setOutputFilename(originalName);
		
	}
	
	/**
	 * If there are spaces in the file paths/names, make sure it encodes correctly.
	 * @throws IOException 
	 */
	@Test
	public void testUserSuppliedFileNameWithSpaces() throws IOException
	{
		String userSuppliedName = "My Report";
		String originalName = reportProcessor.getNrtConfigManager().getOutputFilename();
		
		// Set our name
		reportProcessor.getNrtConfigManager().setOutputFilename(userSuppliedName);
		
		File finalFile = 
				reportProcessor.calculateReportNameAndLocation(NRTConstants.REPORT_HTML_EXTENSION);
		
		
		Assert.assertEquals("My Report.html", finalFile.getName());
		
		// cleanup
		reportProcessor.getNrtConfigManager().setOutputFilename(originalName);
	}
}
