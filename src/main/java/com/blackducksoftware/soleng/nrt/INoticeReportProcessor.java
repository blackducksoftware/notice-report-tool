package com.blackducksoftware.soleng.nrt;

import java.util.HashMap;

import com.blackducksoftware.soleng.nrt.model.ComponentModel;

/**
 * Interface outlining processor behavior.
 * @author akamen
 *
 */
public interface INoticeReportProcessor 
{
	/**
	 * 
	 * @param projectName Name of Protex or CC project/application
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, ComponentModel> processProject(String projectName) throws Exception; 	
}
