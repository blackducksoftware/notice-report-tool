package com.blackducksoftware.sdk.notice.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author jatoui
 *  @title Solutions Architect
 *  @email jatoui@blackducksoftware.com
 *  @company Black Duck Software
 *  @year 2012
 **/

public class ComponentModel {


		
	private String name;
	
	private List <String> paths;
	
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
			paths = new ArrayList<String>();
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

	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> paths) {
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
