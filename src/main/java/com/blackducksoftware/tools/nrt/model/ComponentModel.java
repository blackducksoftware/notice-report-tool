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

package com.blackducksoftware.tools.nrt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.sdk.codecenter.attribute.data.AbstractAttribute;
import com.google.gson.annotations.Expose;

/**
 * Our component bean that holds both Protex and CC data
 * 
 * Bean is serialized through JSON.  Only exposed fields are serialized.
 * @author akamen
 *
 */
public class ComponentModel {

	@Expose
	private String componentId;	
	@Expose
	private String name;
	@Expose
	private String version;
	@Expose
	private String homePage;

	private Boolean displayInReport = new Boolean(true);
	
	@Expose
	private Set <String> paths;	
	@Expose
	private List <String> copyrights;
	@Expose
	private List <LicenseModel> licenses;
	
	// Attributes
	private Map<String, CustomAttributeBean> attributeMap = new HashMap<String, CustomAttributeBean>();

	/**
	 * Overriding the equals such that we can create an alphabetical sort on the output
	 */
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

	/**
	 * Concatenated Name and Version
	 * @return
	 */
	public String getNameAndVersion()
	{
		return getName() + ":" + getVersion();
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


	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public Map<String, CustomAttributeBean> getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(Map<String, CustomAttributeBean> attributeMap) {
		this.attributeMap = attributeMap;
	}

	public Boolean isDisplayInReport() {
		return displayInReport;
	}

	public void setDisplayInReport(Boolean displayInReport) {
		this.displayInReport = displayInReport;
	}
}
