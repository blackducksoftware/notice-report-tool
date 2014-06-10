package com.blackducksoftware.soleng.nrt.config;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import com.blackducksoftware.soleng.nrt.model.CustomAttributeRule;
import com.blackducksoftware.soleng.nrt.model.CustomAttributeRule.ATTRIBUTE_TYPE;

public class NRTConstants 
{
	//
	public static final String HTML_TEMPLATE_FILE = "html_template.html";
	
	//// Properties
	// CC
	public static final String CC_APPLICATION_NAME = "cc.application.name";
	public static final String CC_APPLICATION_VERSION = "cc.application.version";
	
	// Attributes
	public static final String CC_ATTRIBUTE_PREFIX = "cc.attribute";
	
	public static final String CC_ATTRIBUTE_SUFFIX_NAME = "name";
	public static final String CC_ATTRIBUTE_SUFFIX_VALUE = "value";
	public static final String CC_ATTRIBUTE_SUFFIX_TYPE= "type";
	
	// Protex
	public static final String PROPERY_PROTEX_PROJECT = "project.name";
	// Generic
	
	public static final String PROPERTY_LICENSE_FILENAMES = "license.filenames";
	public static final String PROPERTY_COPYRIGHT_PATTERNS = "copyright.patterns";
	
	// Report extensions
	public static final String REPORT_HTML_EXTENSION = ".html";
	public static final String REPORT_PLAIN_TEXT_EXTENSION = ".txt";
	// Names
	public static final String PROPERTY_OUTPUT_FILENAME = "output.filename";	
	public static final String DEFAULT_OUTPUT_HTML_FILENAME_NAME = "BlackDuckAttributionReport" + REPORT_HTML_EXTENSION;	
	
	public static final String PROPERTY_SHOW_FILE_PATHS = "show.file.paths";	
	public static final String PROPERTY_SHOW_COMP_VER = "show.component.version";	
	public static final String PROPERTY_SHOW_COPYRIGHT = "show.copyrights";	
	public static final String PROPERTY_COPYRIGHT_CONTEXT_LENGTH = "copyright.context.length";	
	public static final String PROPERTY_INC_LIC_FILENAMES = "include.license.filenames.in.report";	
	public static final String PROPERTY_TEXT_FILE_OUTPUT = "text.file.output";
	public static final String PROPERTY_HTML_FILE_OUTPUT = "html.file.output";
	

	/// HTML 
	public static final String HTML_TITLE_TOC = "Table of Contents";
	
	// Classes for style, also used in unit tests to look up elements
	public static final String HTML_TITLE_CLASS = "title";
	public static final String HTML_TABLE_CLASS = "main-table";
	public static final String HTML_COMPONENT_CLASS = "component-name";
	public static final String HTML_FILE_PATH_CLASS = "file-paths-link";
	public static final String HTML_COPYRIGHT_CLASS = "copyrights-link";
	public static final String HTML_LICENSE_CLASS = "license-text-link";
	public static final String HTML_HOMEPAGE_CLASS = "homepage-link";
	public static final String HTML_LICENSE_TEXT_CLASS = "license-text";
	
	public static final String HTML_TOC_CLASS = "table-of-contents";
	
	// This is the div that holds the JSON representing the component list
	public static final String HTML_JSON_DATA_BLOCK= "bds-json-data";
	
}
