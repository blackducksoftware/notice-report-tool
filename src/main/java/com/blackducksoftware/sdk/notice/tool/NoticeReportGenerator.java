package com.blackducksoftware.sdk.notice.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

import com.blackducksoftware.sdk.notice.NoticeReportTool;
import com.blackducksoftware.sdk.notice.model.ComponentModel;
import com.blackducksoftware.sdk.notice.model.LicenseModel;

/**
 * @author jatoui
 * @title Solutions Architect
 * @email jatoui@blackducksoftware.com
 * @company Black Duck Software
 * @year 2012
 **/

public class NoticeReportGenerator {

	Logger log = Logger.getLogger(this.getClass().getName());

	public void generateReport(String projectId, String outputFilename,
			HashMap<String, ComponentModel> components) {
		int licenseCounter = 1;

		try {
			HtmlCanvas html = new HtmlCanvas(new PrintWriter(outputFilename));
			html.script().render(new StringResource("/js/functions.js", false))
					._script();

			html.html().body().h1(new HtmlAttributes().add("align", "center"))
					.content("Notice Report for Project: " + projectId).br()
					.br();
			
			int componentCounter = 1;
			
			html.h2().content("Table of Contents");
			//table of contents section
			for (String component : components.keySet())
			{
				html.a(new HtmlAttributes().add("href",
						"#component_" + componentCounter, false)).content(component).br();
				componentCounter++;
			}
			//reset for actual content area
			componentCounter = 1;
			
			html.br().h2().content("Bill of Materials").br();
			
			html.table(new HtmlAttributes().add("border", "1").add("width", "1000")).tr().th(new HtmlAttributes().add("width", "300")).h3().content("BOM Component")._th().th(new HtmlAttributes().add("width", "700")).h3().content("attributes")._th()._tr();
			
			for (String component : components.keySet()) {
				html.tr(new HtmlAttributes().add("id", "component_"
						+ componentCounter));
				log.info("Processing component: " + component);

				html.td().h4().content(component)._td();
				html.td();
				html.a(new HtmlAttributes().add("href",
						"javascript:toggle(\'paths_" + componentCounter
								+ "\');", false)).content("file paths (" + (components.get(component).getPaths() != null?components.get(component).getPaths().size():"0") + ")");

				html.div(
						new HtmlAttributes()
								.add("id", "paths_" + componentCounter)
								.add("style",
										"display:none;border-style:solid;border-width:1px;"))
						.ul();
				if (components.get(component).getPaths() != null) {

					log.info("has paths: "
							+ StringUtils.collectionToDelimitedString(
									components.get(component).getPaths(), ","));
					for (String path : components.get(component).getPaths())

						html.li().content(path);

				}
				else
				{
					html.li().content("no paths exist for this component");
				}

				html._ul()._div().br();

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
							+ StringUtils.collectionToDelimitedString(
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

	public static void main(String args[]) {
		String projectId = "c_testlicenseattribution";
		String outputFilename = "notice.html";

		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"context.xml");

		NoticeReportTool noticeRepTool = (NoticeReportTool) ac
				.getBean("noticeReportTool");

		// HashMap<String, ComponentModel> components = noticeRepTool
		// .processProject(projectId);

		HashMap<String, ComponentModel> components = new HashMap<String, ComponentModel>();

		ComponentModel MyComponent = new ComponentModel();
		MyComponent.addNewPath("/a.txt");
		MyComponent.addNewCopyright("copyright Jad Atoui");
		LicenseModel MyLicense = new LicenseModel();
		MyLicense.setName("jad License");
		MyLicense.setText("Do as you desire");

		LicenseModel MyLicense2 = new LicenseModel();
		MyLicense2.setName("jad License2");
		MyLicense2.setText("Do as you desire2");
		MyComponent.addNewLicense(MyLicense);
		MyComponent.addNewLicense(MyLicense2);
		components.put("JadComponent", MyComponent);

		ComponentModel MyComponent2 = new ComponentModel();
		MyComponent2.addNewPath("/b.txt");
		MyComponent2.addNewCopyright("copyright Jad Atoui");

		MyComponent2.addNewLicense(MyLicense);
		MyComponent2.addNewLicense(MyLicense2);
		components.put("JadComponent2", MyComponent2);

		ComponentModel MyComponent3 = new ComponentModel();
		MyComponent3.addNewPath("/b.txt");
		

		MyComponent3.addNewLicense(MyLicense2);
		components.put("JadComponent3", MyComponent3);

		noticeRepTool.getNoticeGen().generateReport(projectId, outputFilename,
				components);

	}
	
	
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
