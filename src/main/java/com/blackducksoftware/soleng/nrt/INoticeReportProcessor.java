package com.blackducksoftware.soleng.nrt;

import java.io.File;
import java.util.HashMap;

import com.blackducksoftware.soleng.nrt.model.ComponentModel;

/**
 * Interface outlining processor behavior.
 * @author akamen
 *
 */
public interface INoticeReportProcessor 
{
	public HashMap<String, ComponentModel> processProject() throws Exception; 	
}
