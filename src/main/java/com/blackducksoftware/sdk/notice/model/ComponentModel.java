/*
 * Created on August 6, 2013
 * Copyright 2004-2013 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */

package com.blackducksoftware.sdk.notice.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  @author jatoui
 *  @title Solutions Architect
 *  @email jatoui@blackducksoftware.com
 *  @company Black Duck Software
 *  @year 2012
 **/

public class ComponentModel {


		
	private String name;
	
	private Set <String> paths;
	
	private List <String> copyrights;
	
	private List <LicenseModel> licenses;
	
	@Override
	public boolean equals(Object component)
	{
		
		if (component == null) return false;
	    if (component == this) return true;
	    if (!(component instanceof ComponentModel))return false;
    
		return ((ComponentModel)component).getName().equals(this.getName());
	}
	
	public void addNewLicense(LicenseModel license)
	{
		if(licenses == null)
		{
			licenses = new ArrayList<LicenseModel>();
			licenses.add(license);
		}
		else
		{
			if(!licenses.contains(license))
				licenses.add(license);
		}
		
	}
	
	
	public void addNewCopyright(String copyright)
	{
		if(copyrights == null)
		{
			copyrights = new ArrayList<String>();
			copyrights.add(copyright);
		}
		else
		{
			if(!copyrights.contains(copyright))
				copyrights.add(copyright);
		}
		
	}
	
	
	public void addNewPath(String path)
	{
		if(paths == null)
		{
			paths = new HashSet<String>();
			paths.add(path);
		}
		else
		{
			if(!paths.contains(path))
				paths.add(path);
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getPaths() {
		return paths;
	}

	public void setPaths(Set<String> paths) {
		this.paths = paths;
	}

	public List<String> getCopyrights() {
		return copyrights;
	}

	public void setCopyrights(List<String> copyrights) {
		this.copyrights = copyrights;
	}

	public List<LicenseModel> getLicenses() {
		return licenses;
	}

	public void setLicenses(List<LicenseModel> licenses) {
		this.licenses = licenses;
	}

	
	
}
