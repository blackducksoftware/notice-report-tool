/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
 *******************************************************************************/
package com.blackducksoftware.tools.nrt.config;

public class NRTConstants {
    // Command line arguments
    public static final String CL_CONFIG_FILE = "config";

    public static final String CL_APPLICATION_TYPE = "application";

    public static final String CL_PROJECT_NAME = "project";

    //
    public static final String HTML_TEMPLATE_FILE = "html_template.html";

    // // Properties
    // CC
    public static final String CC_APPLICATION_NAME = "cc.application.name";

    public static final String CC_APPLICATION_VERSION = "cc.application.version";

    // Attributes
    public static final String CC_ATTRIBUTE_PREFIX = "cc.attribute";

    public static final String CC_ATTRIBUTE_SUFFIX_NAME = "name";

    public static final String CC_ATTRIBUTE_SUFFIX_VALUE = "value";

    public static final String CC_ATTRIBUTE_SUFFIX_TYPE = "type";

    // Protex
    public static final String PROPERY_PROTEX_PROJECT = "project.name";

    public final static String DEFAULT_VERSION = "Unspecified";

    // Generic
    public static final String PROPERTY_LICENSE_FILENAMES = "license.filenames";

    public static final String PROPERTY_COPYRIGHT_PATTERNS = "copyright.patterns";

    // Report extensions
    public static final String REPORT_HTML_EXTENSION = ".html";

    public static final String REPORT_PLAIN_TEXT_EXTENSION = ".txt";

    // Names
    public static final String PROPERTY_OUTPUT_FILENAME = "output.filename";

    public static final String PROPERTY_OUTPUT_LOCATION = "output.location";

    public static final String PROPERTY_SHOW_FILE_PATHS = "show.file.paths";

    public static final String PROPERTY_SHOW_COMP_VER = "show.component.version";

    public static final String PROPERTY_SHOW_COPYRIGHT = "show.copyrights";

    public static final String PROPERTY_COPYRIGHT_CONTEXT_LENGTH = "copyright.context.length";

    public static final String PROPERTY_INC_LIC_FILENAMES = "include.license.filenames.in.report";

    public static final String PROPERTY_TEXT_FILE_OUTPUT = "text.file.output";

    public static final String PROPERTY_HTML_FILE_OUTPUT = "html.file.output";

    // / HTML
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
    public static final String HTML_JSON_DATA_BLOCK = "bds-json-data";

}
