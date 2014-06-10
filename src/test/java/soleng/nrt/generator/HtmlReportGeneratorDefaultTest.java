package soleng.nrt.generator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import soleng.framework.core.config.ConfigConstants.APPLICATION;

import com.blackducksoftware.soleng.nrt.NoticeReportTool;
import com.blackducksoftware.soleng.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.soleng.nrt.config.NRTConstants;
import com.blackducksoftware.soleng.nrt.generator.NRTReportGenerator;
import com.blackducksoftware.soleng.nrt.model.ComponentModel;
import com.blackducksoftware.soleng.nrt.model.LicenseModel;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Tests the HTML construction portion of the NRT
 * This uses default values for all config options.
 * Not server specific.
 * 
 * @author akamen
 *
 */
public class HtmlReportGeneratorDefaultTest extends HtmlReportGeneratorSetup
{
	private static String configFile = "nrt_config_basic.properties";

	@ClassRule 
    public static TemporaryFolder junitWorkingFolder = new TemporaryFolder();
	// Soon to be populated HTML contents
	private static HtmlPage doc = null;
	
	@BeforeClass 
	public static void setupFiles() throws IOException	
	{		
		String htmlTemplateStr = ClassLoader.getSystemResource(htmlTemplate).getFile();
		basicReportOutputLocation = junitWorkingFolder.newFile("int_test.html");
		Files.copy(new File(htmlTemplateStr).toPath(), basicReportOutputLocation.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		generator = setupFiles(configFile, basicReportOutputLocation);
		doc = getDocForBasicReport();
	}	
	
	@Test
	public void testJsonOutput() throws IOException
	{
		String ourTestFile = ClassLoader.getSystemResource("test_json_output.js").getFile();
		try
		{
			String jsonOutput = generator.generateJSONFromObject(testComponents);
		
			// Compare line-by-line our expected result
			try(
				BufferedReader generatedBuffer = new BufferedReader(new StringReader(jsonOutput));
				BufferedReader expectedBuffer = new BufferedReader(new FileReader(ourTestFile)))
			{
				String generatedLine = null;
				String expectedLine = null;
				
				while( (generatedLine=generatedBuffer.readLine()) != null )
				{
					expectedLine = expectedBuffer.readLine();
					Assert.assertEquals(expectedLine, generatedLine);
				}				
			} catch (Exception e)
			{
				Assert.fail("Unable to perform file match: " + e.getMessage());
			}
		} catch (Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testTableOfContents()
	{
		List<?> tocElement = doc.getByXPath(getXPathName(NRTConstants.HTML_TOC_CLASS));
		Assert.assertEquals(1, tocElement.size());
	}
	

	@Test
	public void testBasicReportRowsAndColumns() throws IOException
	{
		List<HtmlElement> tableElement = doc.getElementsByTagName("table");
		List<HtmlElement> tableRows = tableElement.get(0).getElementsByTagName("tr");
		int tableRowCount = tableRows.size();
		
		// We expect three rows.
		// One for header, two for contents.
		Assert.assertEquals(3, tableRowCount);
		
		// Test for columns
		// We expect two for the most basic
		List<HtmlElement> tableColumns = tableElement.get(0).getElementsByTagName("th");
		int tableColumnCount = tableColumns.size();
		Assert.assertEquals(2, tableColumnCount);
	}
	
	/**
	 * Check to see if the name of component matches
	 * @throws IOException
	 */
	@Test
	public void testComponentName() throws IOException
	{
		List<HtmlDivision> compElements = (List<HtmlDivision>) doc.getByXPath(getXPathName(NRTConstants.HTML_COMPONENT_CLASS));
		
		// Check if we get it
		Assert.assertEquals(testComponents.size(), compElements.size());
		
		// Check name of first element
		ComponentModel compTestModel = this.testComponents.get(COMP_ONE_NAME);
		HtmlDivision firstElement = compElements.get(0);
		Assert.assertEquals(compTestModel.getNameAndVersion(), firstElement.getFirstChild().getTextContent());
	}
	
	
	
	@Test
	public void testFilePaths() throws IOException
	{
		List<HtmlDivision> filePaths = (List<HtmlDivision>) doc.getByXPath(getXPathName(NRTConstants.HTML_FILE_PATH_CLASS));
		
		Assert.assertEquals(2, filePaths.size());
		
		// One component should contain paths
		HtmlDivision fileFilePathListForCompOne = filePaths.get(0);
		List<HtmlElement> pathsForCompOne = fileFilePathListForCompOne.getElementsByTagName("li");
		Assert.assertEquals(2, pathsForCompOne.size());
		
		// The other should contain one (one informs the user that nothing is there)
		HtmlDivision fileFilePathListForCompTwo = filePaths.get(1);
		List<HtmlElement> pathsForCompTwo = fileFilePathListForCompTwo.getElementsByTagName("li");
		Assert.assertEquals(1, pathsForCompTwo.size());
		
	}
	
	@Test
	public void testCopyRights() throws IOException
	{
		List<HtmlDivision> copyrights = (List<HtmlDivision>) doc.getByXPath(getXPathName(NRTConstants.HTML_COPYRIGHT_CLASS));
		
		Assert.assertEquals(2, copyrights.size());
		
		// One component should contain paths
		HtmlDivision cpListForCompOne = copyrights.get(0);
		List<HtmlElement>  cpsCompOne = cpListForCompOne.getElementsByTagName("li");
		Assert.assertEquals(1, cpsCompOne.size());
		
		// The other should contain one (one informs the user that nothing is there)
		HtmlDivision cpListForCompTwo = copyrights.get(1);
		List<HtmlElement>  cpsCompTwo = cpListForCompTwo.getElementsByTagName("li");
		Assert.assertEquals(2, cpsCompTwo.size());
		
	}
	
	@Test
	public void testLicenses() throws IOException
	{
		List<HtmlDivision> licenseLinks = (List<HtmlDivision>) doc.getByXPath(getXPathName(NRTConstants.HTML_LICENSE_CLASS));
		
		Assert.assertEquals(2, licenseLinks.size());
		
		// One component should contain paths
		HtmlDivision licenseListForCompOne = licenseLinks.get(0);
		List<HtmlElement>  licForCompOne = licenseListForCompOne.getElementsByTagName("li");
		Assert.assertEquals(1, licForCompOne.size());
		
		// The other should contain one (one informs the user that nothing is there)
		HtmlDivision licenseListForCompTwo = licenseLinks.get(1);
		List<HtmlElement>  licForCompTwo = licenseListForCompTwo.getElementsByTagName("li");
		Assert.assertEquals(2, licForCompTwo.size());
	}		
	
	@Test
	public void testHomePageLink() throws IOException
	{
		List<HtmlDivision> licenseLinks = (List<HtmlDivision>) doc.getByXPath(getXPathName(NRTConstants.HTML_LICENSE_CLASS));
		Assert.assertEquals(2, licenseLinks.size());
	}
}
