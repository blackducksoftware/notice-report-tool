package com.blackducksoftware.soleng.nrt;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import soleng.framework.core.config.ConfigConstants.APPLICATION;

import com.blackducksoftware.soleng.nrt.config.NRTConstants;

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

		Option projectNameOption = new Option(NRTConstants.CL_APPLICATION_TYPE,
				true, "Application type [PROTEX|CODECENTER] (required)");
		projectNameOption.setRequired(true);
		options.addOption(projectNameOption);

		Option configFileOption = new Option(NRTConstants.CL_CONFIG_FILE, true,
				"Location of configuration file (required)");
		configFileOption.setRequired(true);
		options.addOption(configFileOption);

		File configFile = null;
		APPLICATION applicationType = null;
		
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
				
				try{
				applicationType = APPLICATION.valueOf(bdsApplicationType);
				} catch (IllegalArgumentException e)
				{
					log.error("No such application type recognized: " + bdsApplicationType);
					help();
				}
				
			} else {
				help();
			}

			NoticeReportProcessor processor = new NoticeReportProcessor(
					configFile.getAbsolutePath(), applicationType);
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
