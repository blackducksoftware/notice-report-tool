/*
 * Created on August 6, 2013
 * Copyright 2004-2013 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */

package com.blackducksoftware.soleng.nrt.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;

import com.blackducksoftware.soleng.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.soleng.nrt.config.NRTConstants;
import com.blackducksoftware.soleng.nrt.model.ComponentModel;
import com.blackducksoftware.soleng.nrt.model.LicenseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Generators an HTML Report from a provided component map
 * @author akamen
 *
 */
public class HtmlReportGenerator {

	Logger log = Logger.getLogger(this.getClass().getName());
	
	
	private NRTConfigurationManager nrtConfig = null;
	private HashMap<String, ComponentModel> componentMap = null;

	public HtmlReportGenerator(NRTConfigurationManager nrtConfig, HashMap<String, ComponentModel> compmap)
	{
		this.nrtConfig = nrtConfig;
		componentMap = compmap;
	}
	
	
	/**
	 * Copies the HTML template into the finalHtmlOutput
	 * then injects the generates JSON data into the specific div location
	 * and writes it out.
	 * @param expectedFile
	 */
	public void generateHTMLFromTemplate(File finalHtmlOutput) 
	{	

		log.info("Writing to report: " + finalHtmlOutput);
		String jsonComponentList = generateJSONFromObject(componentMap);	
		String jsonPropertyList = generateJSONFromObject(nrtConfig.getOptionsForExport());
		// Construct a variable out of it
		jsonComponentList = "var compList=[" + jsonComponentList + "]";
		jsonPropertyList = "var propList=[" + jsonPropertyList + "]";
		
		PrintWriter writer = null;
		try
		{
			// Read the template
			Document doc = Jsoup.parse(finalHtmlOutput, "UTF-8");
			
			// Inject the JSON
			Elements jsonElementDivBlock = doc.getElementsByClass(NRTConstants.HTML_JSON_DATA_BLOCK);
			
			// This will be empty, but it should exist
			Element jsonDivElement = jsonElementDivBlock.get(0);
			
			if(jsonDivElement != null)
			{		
				// Remove any script tags from it, in case the user populated the template incorrectly with data
				if(jsonDivElement.children().size() > 0 )
				{
					Elements children = jsonDivElement.children();
					for(int i = 0; i < children.size(); i++)
					{
						Element el = children.get(i);
						el.remove();
					}
				}
				
				addNewScriptElementWithJson(jsonDivElement, jsonComponentList);
				addNewScriptElementWithJson(jsonDivElement, jsonPropertyList);
			}
			else
			{
				log.error("Unable to find a valid critical DIV inside HTML template: " + NRTConstants.HTML_JSON_DATA_BLOCK);
			}
			writer = new PrintWriter(finalHtmlOutput,"UTF-8");
			// Write out the file
			writer.write(doc.html());
			writer.flush();
			writer.close();

		} catch (Exception e)
		{
			log.error("Unable to write out final report file!", e);
		}
		finally
		{
			writer.close();
		}
		
	}
	
	private void addNewScriptElementWithJson(Element jsonCompListElement,
			String jsonText) 
	{
		Element scriptElement = jsonCompListElement.appendElement("script");
		DataNode jsonNode = new DataNode(jsonText, "");
		scriptElement.appendChild(jsonNode);
		
	}

	/**
	 * Writes JSON to file
	 * @param outputFilename
	 * @return Returns the String that was written out.
	 */
	public String generateJSONFromObject(Object collection)
	{
		StringBuilder sb = new StringBuilder();
		
		try
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();		
			sb.append(gson.toJson(collection));
		} catch (Exception e)
		{
			log.error("Error while generating JSON", e);
		}
		
		return sb.toString();
	}
	
	/**
	 * Pre 1.0 output mechanism.
	 * Deprecated in favor of template approach
	 * TODO:  Refactor out the text output from here and expose as separate method.
	 * @param projectName
	 * @param outputFilename
	 * @throws Exception
	 */
	@Deprecated
	public void generateReport(String projectName, String outputFilename) throws Exception 
	{
		// Configurations
		boolean showComponentVersion = nrtConfig.isShowComponentVersion();
		boolean textFileOutput = nrtConfig.isTextFileOutput();

		PrintStream outputTextFile = null;
		FileOutputStream outputStream = null;
		
		try {
			HtmlCanvas html = new HtmlCanvas(new PrintWriter(outputFilename));
			
			html.script().render(new StringResource("/js/functions.js", false))._script();
			html.macros().stylesheet(ClassLoader.getSystemResource("css/nrt_css.css").getFile());

			html.html().body().h1(new HtmlAttributes().add("align", "center"))
					.content("Notice Report for Project: " + projectName).br()
					.br();
			
			int componentCounter = 1;
			int licenseCounter = 1;
			
			/**
			 * Generate a Table of Contents
			 */
			List<String> componentNames = generateTableOfContents(html, componentCounter, showComponentVersion, projectName);
			
			//reset for actual content area
			componentCounter = 1;
			
			html.br().h2().content("Bill of Materials").br();
			
			html.table(
					new HtmlAttributes().class_(NRTConstants.HTML_TABLE_CLASS)).
					
					tr().
						th(new HtmlAttributes().add("width", "300")).h3().content("BOM Component")._th().
						th(new HtmlAttributes().add("width", "700")).h3().content("Attributes")._th().
					_tr();
			
			if (textFileOutput) {
				try {
					File dir = new File(".\\" + projectName + "_text_files\\");
					dir.mkdirs();
				}
				catch (SecurityException e) 
				{
					log.error("Unable to create directory for file output", e);
				}
			}
			
			for (String componentName : componentNames) 
			{
				// Create the component column
				html.
				tr(new HtmlAttributes().add("id", "component_"	+ componentCounter));
				
					log.info("Processing component: " + (showComponentVersion ? componentName : componentName.substring(0,
							componentName.lastIndexOf(":"))));
	
					// Component name
					html.td().
							h4().
								div(new HtmlAttributes().class_(NRTConstants.HTML_COMPONENT_CLASS))
									.content(showComponentVersion ? componentName : componentName.substring(0,
									componentName.lastIndexOf(":")))
							._h4()
						._td();
					
					html.td();
					
						if (textFileOutput) {
							try {
								outputStream = new FileOutputStream(showComponentVersion ? ".\\" + projectName + "_text_files\\" + componentName.replace(':', '-') + ".txt" : 
									 													   ".\\" + projectName + "_text_files\\" + componentName.substring(0, componentName.toString().lastIndexOf(":")).replace(':', '-') + ".txt");
								
								outputTextFile = new PrintStream(outputStream);
							}
							catch (FileNotFoundException e) {
									outputStream.close();
									outputTextFile.close();
									e.printStackTrace();
							}
						}
						
						// Add copyrights, filepaths and license text into the next column.
						/**
						 * Write out the file paths
						 */
						writeOutFilePaths(html, componentCounter, componentName, outputTextFile);
						html.br();
						/**
						 * Write out all the copy rights
						 */
						writeOutCopyrights(html, componentCounter, componentName, outputTextFile);		
						html.br();
						/**
						 * Write out all the licenses
						 */
						writeOutLicenseText(html, componentCounter, licenseCounter, componentName, outputTextFile);
	
					
					html._td();
				html._tr();
				componentCounter++;
				html.getOutputWriter().flush();
			} // For all component names
			
			// Close out all the HTML
			html._table();
			html._body();
			html._html();
			
			
			html.getOutputWriter().flush();
			
			if (textFileOutput) {
				outputStream.close();
				outputTextFile.close();
			}
			
		} catch (IOException e) {
			log.error("Error writing to output html file", e);
		}
	}

	private void writeOutLicenseText(HtmlCanvas html, int componentCounter, int licenseCounter,
			String componentName, PrintStream outputTextFile)
	{
		try{
			html.a(new HtmlAttributes().add("href",
					"javascript:toggle(\'licensetexts_" + componentCounter
					+ "\');", false)).content("License texts (" + (componentMap.get(componentName)
	
							.getLicenses()!=null?componentMap.get(componentName)
	
									.getLicenses().size():"0") + ")");
			html.div(
				new HtmlAttributes()
					.class_(NRTConstants.HTML_LICENSE_CLASS)
					.add("id", "licensetexts_" + componentCounter)
					);
			
			// Start list
			html.ul();
			
			if (nrtConfig.isTextFileOutput()) {
				outputTextFile.println();
				outputTextFile.println("License texts (" + (componentMap.get(componentName).getLicenses()!=null?componentMap.get(componentName).getLicenses().size():"0") + ")");
			}
	
			if (componentMap.get(componentName).getLicenses() != null) 
			{
				log.info("has " + componentMap.get(componentName)
	
				.getLicenses().size() + " licenses");
				
				for (LicenseModel license : componentMap.get(componentName)
						.getLicenses()) {
	
					String licenseName = license.getName() != null ? license.getName() + "(Taken from KnowledgeBase)" : 
																	 "license_" + licenseCounter + "(Taken from scanned file)";
					
					if (license.getName() != null) {
						html.li()
							.a(new HtmlAttributes().add("href",
									"javascript:toggle(\'license_"
											+ licenseCounter + "\');",
									false))
							.content(licenseName)
							.div(new HtmlAttributes()
									.add("id", "license_" + licenseCounter)
									.add("style",
											"display:none;border-style:solid;border-width:1px;"))
							.content(license.getText(), false)._li();														
					}
					else
					{
						html.li()
						.a(new HtmlAttributes().add("href",
								"javascript:toggle(\'license_"
										+ licenseCounter + "\');",
								false))
						.content(licenseName)
						.div(new HtmlAttributes()
								.add("id", "license_" + licenseCounter)
								.add("style",
										"display:none;border-style:solid;border-width:1px;"))
						.content("<pre>" + StringEscapeUtils.escapeHtml(license.getText()) + "</pre>", false)._li();
					}
	
					if (nrtConfig.isTextFileOutput()) {
						outputTextFile.println();
						outputTextFile.println("==========================================================================");
						outputTextFile.println(licenseName);
						outputTextFile.print(StringEscapeUtils.unescapeHtml(Jsoup.clean(license.getText(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false))));
					}
					
					licenseCounter++;
	
				} // for all licenses
		
			} // if licenses exist
			else
			{
				html.li().content("no licenses exist for this component");
			}
			
			// Close list, close div
			html._ul()._div();
		} catch(Exception e)
		{
			log.error("Error writing out licenses", e);
		}
	}

	private void writeOutCopyrights(HtmlCanvas html, int componentCounter,
			String componentName, PrintStream outputTextFile) 
	{
		try
		{
			if (nrtConfig.isShowCopyrights()) 
			{
				html.a(new HtmlAttributes().add("href",
						"javascript:toggle(\'copyright_" + componentCounter
								+ "\');", false)).content("copyrights (" + (componentMap.get(componentName).getCopyrights() != null?componentMap.get(componentName).getCopyrights().size():"0") + ")");
	
				html.div(new HtmlAttributes()
							.class_(NRTConstants.HTML_COPYRIGHT_CLASS)
							.add("id", "copyright_" + componentCounter)						
						);
				
				// Start list
				html.ul();
				
				if (nrtConfig.isTextFileOutput()) {
					outputTextFile.println();
					outputTextFile.println("copyrights (" + (componentMap.get(componentName).getCopyrights() != null?componentMap.get(componentName).getCopyrights().size():"0") + ")");
				}
	
				if (componentMap.get(componentName).getCopyrights() != null) 
				{
					log.info("has copyrights: "	+ StringUtils.join(	componentMap.get(componentName).getCopyrights(),","));
					
					for (String copyright : componentMap.get(componentName)
							.getCopyrights()) 
					{
						html.li().content(copyright);
					}
					
					if (nrtConfig.isTextFileOutput()) {
						for (String copyright : componentMap.get(componentName).getCopyrights())
							outputTextFile.println(copyright);
					}
	
				} // if copyrights exist
				else
				{
					html.li().content("No copyright strings exist for this component");
				}
				// end list, close div
				html._ul()._div();
			} 
		} catch (Exception e)
		{
			log.error("Unable to write out copyrights", e);
		}
	
	}

	private void writeOutFilePaths(
			HtmlCanvas html, 
			int componentCounter, 
			String componentName, 
			PrintStream outputTextFile) 
	{

		if (nrtConfig.isShowFilePaths()) 
		{
			try
			{
				html.a(new HtmlAttributes().add("href",
						"javascript:toggle(\'paths_" + componentCounter
								+ "\');", false)).
								content("file paths (" + (componentMap.get(componentName).getPaths() != null ? 
										componentMap.get(componentName).getPaths().size():"0") + ")");
		
				html.div(
						new HtmlAttributes()
							.class_(NRTConstants.HTML_FILE_PATH_CLASS)
							.add("id", "paths_" + componentCounter)
						);
				
				// Start list
				html.ul();
				
				if (nrtConfig.isTextFileOutput())
					outputTextFile.println(
							"file paths (" + (componentMap.get(componentName).getPaths() != null ? 
									componentMap.get(componentName).getPaths().size():"0") + ")");
									
				if (componentMap.get(componentName).getPaths() != null) 
				{
					log.info("has paths: "	+ StringUtils.join(componentMap.get(componentName).getPaths(), ","));
					
					for (String path : componentMap.get(componentName).getPaths())
					{
						html.li().content(path);
					}
					if (nrtConfig.isTextFileOutput()) 
					{
						for (String path : componentMap.get(componentName).getPaths())
						{
							outputTextFile.println(path);
						}
					}		
				}
				else
				{
					html.li().content("no paths exist for this component");
				}
		
				html._ul()._div();
			} // try
			catch(Exception e)
			{
				log.error("Unable to write out file paths", e);
			}
		}
		else
		{
			log.debug("Skipping paths section, set to false");
		}
		
	}

	private List<String> generateTableOfContents(
			HtmlCanvas html,int componentCounter, boolean showComponentVersion, String projectName) 
	{
		List<String> componentNames = new ArrayList<String>();
		try{
			html.div(new HtmlAttributes().class_(NRTConstants.HTML_TOC_CLASS));
				html.h2().content(NRTConstants.HTML_TITLE_TOC);
				
				// TODO: Huh?  That seems bad.
				componentMap.remove(projectName+":Unspecified"); //remove the Project from the final html report
				
				Set<String> keySet = componentMap.keySet();
				componentNames = new ArrayList<String>(keySet);
				Collections.sort(componentNames);
						
				for (Object component : componentNames)
				{
					html.a(new HtmlAttributes().add("href",
							"#component_" + componentCounter, false)).content(showComponentVersion ? component.toString() : component.toString().substring(0,
									component.toString().lastIndexOf(":"))).br();
					componentCounter++;
				}
			html._div();
		} catch (IOException ioe)
		{
			log.error("Error while generating TOC", ioe);
		}
		
		return componentNames;
	}

	/**
	 * Generates plain-text fileoutput for the attributions.
	 * @param outputFile
	 */
	public void generateFileoutput(File outputFile) {
		throw new UnsupportedOperationException();		
	}

}
