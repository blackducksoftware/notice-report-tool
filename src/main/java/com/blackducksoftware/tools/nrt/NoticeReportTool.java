/*******************************************************************************
 * Copyright (C) 2015 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/
package com.blackducksoftware.tools.nrt;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants.APPLICATION;
import com.blackducksoftware.tools.nrt.config.NRTConstants;

/**
 * TODO: Refactor this class, move out all logic into specific processors
 * 
 * @author akamen
 * 
 */
public class NoticeReportTool {

    static Logger log = Logger.getLogger(NoticeReportTool.class.getClass()
	    .getName());
    private static Options options = new Options();

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

	System.out.println("Notice Report Tool for Black Duck Suite");
	CommandLineParser parser = new DefaultParser();

	options.addOption("h", "help", false, "show help.");

	Option applicationOption = new Option(NRTConstants.CL_APPLICATION_TYPE,
		true, "Application type [PROTEX|CODECENTER] (required)");
	applicationOption.setRequired(true);
	options.addOption(applicationOption);

	Option configFileOption = new Option(NRTConstants.CL_CONFIG_FILE, true,
		"Location of configuration file (required)");
	configFileOption.setRequired(true);
	options.addOption(configFileOption);

	Option projectNameOption = new Option(NRTConstants.CL_PROJECT_NAME,
		true,
		"Name of Protex project (will override configuration file)");
	projectNameOption.setRequired(false);
	options.addOption(projectNameOption);

	File configFile = null;
	APPLICATION applicationType = null;
	String projectName = null;

	try {
	    CommandLine cmd = parser.parse(options, args);

	    if (cmd.hasOption("h"))
		help();

	    // Config File
	    if (cmd.hasOption(NRTConstants.CL_CONFIG_FILE)) {
		String configFilePath = cmd
			.getOptionValue(NRTConstants.CL_CONFIG_FILE);
		log.info("Config file location: " + configFilePath);
		configFile = new File(configFilePath);
		if (!configFile.exists()) {
		    log.error("Configuration file does not exist at location: "
			    + configFile);
		    System.exit(-1);
		}
	    } else {
		log.error("Must specify configuration file!");
		help();
	    }

	    if (cmd.hasOption(NRTConstants.CL_APPLICATION_TYPE)) {
		String bdsApplicationType = cmd
			.getOptionValue(NRTConstants.CL_APPLICATION_TYPE);

		try {
		    applicationType = APPLICATION.valueOf(bdsApplicationType);
		} catch (IllegalArgumentException e) {
		    log.error("No such application type recognized: "
			    + bdsApplicationType);
		    help();
		}

	    } else {
		help();
	    }

	    if (cmd.hasOption(NRTConstants.CL_PROJECT_NAME)) {
		projectName = cmd.getOptionValue(NRTConstants.CL_PROJECT_NAME);
		log.info("User specified project name: " + projectName);
	    }

	    NoticeReportProcessor processor = new NoticeReportProcessor(
		    configFile.getAbsolutePath(), applicationType, projectName);
	    processor.processReport();

	} catch (Exception e) {
	    log.error("Error: " + e.getMessage());
	    help();
	}
    }

    private static void help() {
	HelpFormatter formater = new HelpFormatter();
	formater.printHelp("Help", options);
	System.exit(0);
    }
}
