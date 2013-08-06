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

package com.blackducksoftware.sdk.notice.report;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;

import com.blackducksoftware.sdk.notice.Config;
import com.blackducksoftware.sdk.notice.model.ComponentModel;
import com.blackducksoftware.sdk.notice.model.LicenseModel;

/**
 * @author jatoui
 * @title Solutions Architect
 * @email jatoui@blackducksoftware.com
 * @company Black Duck Software
 * @year 2012
 **/

public class HtmlReportGenerator {

	Logger log = Logger.getLogger(this.getClass().getName());
	
	private static Config config;

	public void generateReport(String projectName, String outputFilename,
			HashMap<String, ComponentModel> components) throws Exception {
		int licenseCounter = 1;
		config = new Config();

		try {
			HtmlCanvas html = new HtmlCanvas(new PrintWriter(outputFilename));
			html.script().render(new StringResource("/js/functions.js", false))
					._script();

			html.html().body().h1(new HtmlAttributes().add("align", "center"))
					.content("Notice Report for Project: " + projectName).br()
					.br();
			
			int componentCounter = 1;
			
			html.h2().content("Table of Contents");
			//table of contents section
			boolean showComponentVersion = false;
			if (config.getShowComponentVersion().toLowerCase().equals("true")) {
				showComponentVersion = true;
			}
			
			Object[] keys = components.keySet().toArray();
			Arrays.sort(keys);
			
			
			for (Object component : keys)
			{
				html.a(new HtmlAttributes().add("href",
						"#component_" + componentCounter, false)).content(showComponentVersion ? component.toString() : component.toString().substring(0,
								component.toString().lastIndexOf(":"))).br();
				componentCounter++;
			}
			
			//reset for actual content area
			componentCounter = 1;
			
			html.br().h2().content("Bill of Materials").br();
			
			html.table(new HtmlAttributes().add("border", "1").add("width", "1000")).tr().th(new HtmlAttributes().add("width", "300")).h3().content("BOM Component")._th().th(new HtmlAttributes().add("width", "700")).h3().content("attributes")._th()._tr();
			
			for (Object component : keys) {
				html.tr(new HtmlAttributes().add("id", "component_"
						+ componentCounter));
				log.info("Processing component: " + (showComponentVersion ? component.toString() : component.toString().substring(0,
						component.toString().lastIndexOf(":"))));

				html.td().h4().content(showComponentVersion ? component.toString() : component.toString().substring(0,
						component.toString().lastIndexOf(":")))._td();
				html.td();
				
				if (config.getShowFilePaths().toLowerCase().equals("true")) {
					html.a(new HtmlAttributes().add("href",
							"javascript:toggle(\'paths_" + componentCounter
									+ "\');", false)).content("file paths (" + (components.get(component).getPaths() != null ? components.get(component).getPaths().size():"0") + ")");
	
					html.div(
							new HtmlAttributes()
									.add("id", "paths_" + componentCounter)
									.add("style",
											"display:none;border-style:solid;border-width:1px;"))
							.ul();
					if (components.get(component).getPaths() != null) {
	
						log.info("has paths: "
								+ StringUtils.join(
										components.get(component).getPaths(), ","));
						for (String path : components.get(component).getPaths())
	
							html.li().content(path);
	
					}
					else
					{
						html.li().content("no paths exist for this component");
					}
	
					html._ul()._div().br();
				}
				
				if (config.getShowCopyrights().toLowerCase().equals("true")) {
					html.a(new HtmlAttributes().add("href",
							"javascript:toggle(\'copyright_" + componentCounter
									+ "\');", false)).content("copyrights (" + (components.get(component).getCopyrights() != null?components.get(component).getCopyrights().size():"0") + ")");
	
					html.div(
							new HtmlAttributes()
									.add("id", "copyright_" + componentCounter)
									.add("style",
											"display:none;border-style:solid;border-width:1px;"))
							.ul();
	
					if (components.get(component).getCopyrights() != null) {
						log.info("has copyrights: "
								+ StringUtils.join(
										components.get(component).getCopyrights(),
										","));
						for (String copyright : components.get(component)
								.getCopyrights()) {
							html.li().content(copyright);
						}
	
					}
					else
					{
						html.li().content("no copyright strings exist for this component");
					}
	
					html._ul()._div().br();
				}

				html.a(new HtmlAttributes().add("href",
						"javascript:toggle(\'licensetexts_" + componentCounter
						+ "\');", false)).content("License texts (" + (components.get(component)

								.getLicenses()!=null?components.get(component)

										.getLicenses().size():"0") + ")");
				html.div(
						new HtmlAttributes()
								.add("id", "licensetexts_" + componentCounter)
								.add("style",
										"display:none;border-style:solid;border-width:1px;")).ul();
				

				if (components.get(component).getLicenses() != null) {
					log.info("has " + components.get(component)

					.getLicenses().size() + " licenses");
					
					for (LicenseModel license : components.get(component)
							.getLicenses()) {

						String licenseName = license.getName() != null ? license
								.getName() + "(Taken from KnowledgeBase)" : "license_" + licenseCounter + "(Taken from scanned file)";
						
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

						licenseCounter++;

					}
				}
				else
				{
					html.li().content("no licenses exist for this component");
				}
				html._ul()._div();
				html._td()._tr();
				componentCounter++;
				html.getOutputWriter().flush();
			}
			html._table();
			// html._div();
			html._body();
			html._html();
			html.getOutputWriter().flush();
		} catch (IOException e) {
			System.err.print("Error writing to output html file");
			e.printStackTrace();
		}
	}

//	public static void main(String args[]) {
//		String projectId = "c_testlicenseattribution";
//		String outputFilename = "notice.html";
//
//		ApplicationContext ac = new ClassPathXmlApplicationContext(
//				"context.xml");
//
//		NoticeReportTool noticeRepTool = (NoticeReportTool) ac
//				.getBean("noticeReportTool");
//
//		// HashMap<String, ComponentModel> components = noticeRepTool
//		// .processProject(projectId);
//
//		HashMap<String, ComponentModel> components = new HashMap<String, ComponentModel>();
//
//		ComponentModel MyComponent = new ComponentModel();
//		MyComponent.addNewPath("/a.txt");
//		MyComponent.addNewCopyright("copyright Jad Atoui");
//		LicenseModel MyLicense = new LicenseModel();
//		MyLicense.setName("jad License");
//		MyLicense.setText("Do as you desire");
//
//		LicenseModel MyLicense2 = new LicenseModel();
//		MyLicense2.setName("jad License2");
//		MyLicense2.setText("Do as you desire2");
//		MyComponent.addNewLicense(MyLicense);
//		MyComponent.addNewLicense(MyLicense2);
//		components.put("JadComponent", MyComponent);
//
//		ComponentModel MyComponent2 = new ComponentModel();
//		MyComponent2.addNewPath("/b.txt");
//		MyComponent2.addNewCopyright("copyright Jad Atoui");
//
//		MyComponent2.addNewLicense(MyLicense);
//		MyComponent2.addNewLicense(MyLicense2);
//		components.put("JadComponent2", MyComponent2);
//
//		ComponentModel MyComponent3 = new ComponentModel();
//		MyComponent3.addNewPath("/b.txt");
//		
//
//		MyComponent3.addNewLicense(MyLicense2);
//		components.put("JadComponent3", MyComponent3);
//
//		noticeRepTool.getNoticeGen().generateReport(projectId, outputFilename,
//				components);
//
//	}
	
	
//	private boolean isCleanHtml(String licText)
//	{
//		try {
//			Jsoup
//			.parse(IOUtils.toInputStream(licText), "UTF-8", "");
//		} catch (IOException e) {
//			return false;
//		}
//		return true;
//	}

}
