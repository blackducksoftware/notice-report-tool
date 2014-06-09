package com.blackducksoftware.soleng.nrt;

import org.apache.log4j.Logger;

import soleng.framework.core.config.ConfigConstants.APPLICATION;

/**
 * TODO: Refactor this class, move out all logic into specific processors
 * @author akamen
 *
 */
public class NoticeReportTool {

	static Logger log = Logger.getLogger(NoticeReportTool.class.getClass().getName());

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception 
	{
	    try 
	    {
	    	if(args.length != 2)
	    	{
	    		log.error("Expecting a configuration file location and application type");
	    		System.exit(1);
	    	}
	    	
	    	String configFileLocation = args[0];
	    	String bdsApplicationType = args[1];
	    	
	    	if(bdsApplicationType == null || bdsApplicationType.length() ==0)
	    		throw new Exception("Please provide valid application type! PROTEX|CODECENTER");
	    	

	    	NoticeReportProcessor processor = new NoticeReportProcessor(configFileLocation, APPLICATION.valueOf(bdsApplicationType));
	    	processor.processReport();
	        
	    } catch (RuntimeException e) {
	        log.error("Error: " + e.getMessage());
	        System.exit(-1);
	    }


	}
}
