package com.blackducksoftware.sdk.atlassian;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.blackducksoftware.sdk.atlassian.model.ComponentModel;
import com.blackducksoftware.sdk.atlassian.model.LicenseModel;
import com.blackducksoftware.sdk.atlassian.tool.NoticeReportGenerator;
import com.blackducksoftware.sdk.atlassian.tool.PathPopulator;
import com.blackducksoftware.sdk.atlassian.tool.ProtexReportGenerator;
import com.blackducksoftware.sdk.atlassian.tool.StringSearchPopulator;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxyV6_1;
import com.blackducksoftware.sdk.protex.report.ReportSectionType;

/**
 * @author jatoui
 * @title Solutions Architect
 * @email jatoui@blackducksoftware.com
 * @company Black Duck Software
 * @year 2012
 **/

public class NoticeReportTool {

	ProtexServerProxyV6_1 proxy;

	ProtexReportGenerator reportGen;

	PathPopulator pathPopulator;

	StringSearchPopulator searchPopulator;

	NoticeReportGenerator noticeGen;
	
	Logger log = Logger.getLogger(this.getClass().getName());
	
	public NoticeReportGenerator getNoticeGen() {
		return noticeGen;
	}

	public void setNoticeGen(NoticeReportGenerator noticeGen) {
		this.noticeGen = noticeGen;
	}
	

	public ProtexServerProxyV6_1 getProxy() {
		return proxy;
	}

	public void setProxy(ProtexServerProxyV6_1 proxy) {
		this.proxy = proxy;
	}

	public ProtexReportGenerator getReportGen() {
		return reportGen;
	}

	public void setReportGen(ProtexReportGenerator reportGen) {
		this.reportGen = reportGen;
	}

	public PathPopulator getPathPopulator() {
		return pathPopulator;
	}

	public void setPathPopulator(PathPopulator pathPopulator) {
		this.pathPopulator = pathPopulator;
	}

	public StringSearchPopulator getSearchPopulator() {
		return searchPopulator;
	}

	public void setSearchPopulator(StringSearchPopulator searchPopulator) {
		this.searchPopulator = searchPopulator;
	}

	private HashMap<String, ComponentModel> processProject(String projectId) {

		System.out.println("Generating Identified Files report");

		Document idFiles = reportGen.generate(projectId,
				ReportSectionType.IDENTIFIED_FILES);

		HashMap<String, ComponentModel> components = new HashMap<String, ComponentModel>();

		System.out.println("Associating file paths with BOM entries");
		pathPopulator
				.populatePathsForBomEntries(projectId, components, idFiles);

		System.out.println("Generating String Searches report");
		Document searches = reportGen.generate(projectId,
				ReportSectionType.STRING_SEARCHES);

		System.out
				.println("Associating License and Copyright Search Strings with BOM entries");
		searchPopulator.populateCopyrightsForComponent(
				Collections.list(Collections.enumeration(components.values())),
				searches);

		return components;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String projectId = "c_testlicenseattribution";
		String outputFilename = "notice.html";

		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"context.xml");

		NoticeReportTool noticeRepTool = (NoticeReportTool) ac
				.getBean("noticeReportTool");

		HashMap<String, ComponentModel> components = noticeRepTool
				.processProject(projectId);

	
		noticeRepTool.getNoticeGen().generateReport(projectId, outputFilename, components);

	}
}
