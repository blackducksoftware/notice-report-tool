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
package com.blackducksoftware.tools.nrt.model;

import com.google.gson.annotations.Expose;

/**
 * @author jatoui
 * @title Solutions Architect
 * @email jatoui@blackducksoftware.com
 * @company Black Duck Software
 * @year 2012
 **/

public class LicenseModel {

    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private String text;

    // Is the license, standard, custom, etc. Protex only.
    private String licenseOriginType;

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
    public boolean equals(Object license) {

	if (license == null)
	    return false;
	if (license == this)
	    return true;
	if (!(license instanceof LicenseModel))
	    return false;

	LicenseModel licenseModel = (LicenseModel) license;

	if (licenseModel.getId() != null && this.getId() != null)
	    return licenseModel.getId().equals(this.getId());

	else
	    return licenseModel.getText().equals(this.getText());
    }

    public String getLicenseOriginType() {
	return licenseOriginType;
    }

    public void setLicenseOriginType(String licenseOriginType) {
	this.licenseOriginType = licenseOriginType;
    }

}
