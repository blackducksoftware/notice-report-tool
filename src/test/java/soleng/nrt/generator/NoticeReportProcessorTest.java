package soleng.nrt.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import soleng.framework.core.config.ConfigConstants.APPLICATION;

import com.blackducksoftware.soleng.nrt.NoticeReportProcessor;
import com.blackducksoftware.soleng.nrt.config.NRTConstants;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

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
		reportProcessor = new NoticeReportProcessor(configFile, APPLICATION.CODECENTER);
		
	}	
	
	/**
	 * Tests the default copy, where the config file does not specify anything.
	 * @throws IOException 
	 * @throws Exception 
	 */
	@Test
	public void testDefaultHTMLFileCopy() throws IOException 
	{		
		String expectedName = "BlackDuckAttributionReport.html";
	
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
		String userSuppliedName = "MyReport.html";
		reportProcessor.getNrtConfigManager().setOutputFilename(userSuppliedName);
		File finalFile = 
				reportProcessor.calculateReportNameAndLocation(NRTConstants.REPORT_HTML_EXTENSION);
		
		Assert.assertEquals(userSuppliedName, finalFile.getName());
		
	}
	
	/**
	 * If there are spaces in the file paths/names, make sure it encodes correctly.
	 * @throws IOException 
	 */
	@Test
	public void testUserSuppliedFileNameWithSpaces() throws IOException
	{
		String userSuppliedName = "My Report.html";
		reportProcessor.getNrtConfigManager().setOutputFilename(userSuppliedName);
		
		File finalFile = 
				reportProcessor.calculateReportNameAndLocation(NRTConstants.REPORT_HTML_EXTENSION);
		
		Assert.assertEquals("My%20Report.html", finalFile.getName());
	}
}
