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

/**
 *  @author jatoui
 *  @title Solutions Architect
 *  @email jatoui@blackducksoftware.com
 *  @company Black Duck Software
 *  @year 2012
 **/

public class LicenseModel {

	private String id;
	
	private String name;
	
	private String text;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object license)
	{
		
		if (license == null) return false;
	    if (license == this) return true;
	    if (!(license instanceof LicenseModel))return false;
    
	    LicenseModel licenseModel = (LicenseModel)license;
	    
	    
	    if(licenseModel.getId() != null  && this.getId() != null)
	    	return licenseModel.getId().equals(this.getId());
	 
	    else
	    	return licenseModel.getText().equals(this.getText());
	}
	
	
}
