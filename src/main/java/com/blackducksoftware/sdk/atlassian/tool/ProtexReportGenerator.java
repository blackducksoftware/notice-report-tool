package com.blackducksoftware.sdk.atlassian.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxyV6_1;
import com.blackducksoftware.sdk.protex.report.Report;
import com.blackducksoftware.sdk.protex.report.ReportFormat;
import com.blackducksoftware.sdk.protex.report.ReportSection;
import com.blackducksoftware.sdk.protex.report.ReportSectionType;
import com.blackducksoftware.sdk.protex.report.ReportTemplateRequest;

/**
 *  @author jatoui
 *  @title Solutions Architect
 *  @email jatoui@blackducksoftware.com
 *  @company Black Duck Software
 *  @year 2012
 **/

public class ProtexReportGenerator {
	Logger log = Logger.getLogger(this.getClass().getName());

	private ProtexServerProxyV6_1 proxy;

	public ProtexServerProxyV6_1 getProxy() {
		return proxy;
	}

	public void setProxy(ProtexServerProxyV6_1 proxy) {
		this.proxy = proxy;
	}

	public Document generate(String projectId, ReportSectionType type) {
		ReportTemplateRequest reportReq = new ReportTemplateRequest();
		ReportSection section = new ReportSection();
		section.setLabel(type.name());
		section.setSectionType(type);
		reportReq.getSections().add(section);
		reportReq.setTitle(type.name());

		Report report = null;

		try {
			report = proxy.getReportApi().generateAdHocProjectReport(projectId,
					reportReq, ReportFormat.HTML);
		} catch (SdkFault e) {
			log.error("Could not generate String Searches report for project "
					+ projectId, e);
			System.exit(-1);
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Document doc = null;
		try {
			report.getFileContent().writeTo(os);
			doc = Jsoup
					.parse(IOUtils.toInputStream(os.toString()), "UTF-8", "");
		} catch (IOException e) {
			log.error("Could not parse report output", e);
			System.exit(-1);
		}

		return doc;

	}

}
