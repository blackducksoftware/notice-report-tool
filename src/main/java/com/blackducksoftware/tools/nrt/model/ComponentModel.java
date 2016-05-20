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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.google.gson.annotations.Expose;

/**
 * Our component bean that holds both Protex and CC data
 * 
 * Bean is serialized through JSON. Only exposed fields are serialized.
 * 
 * @author akamen
 * 
 */
public class ComponentModel extends CodeCenterComponentPojo {

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
    private Set<String> paths;

    @Expose
    private List<String> copyrights;

    @Expose
    private List<LicenseModel> licenses;

    // Attributes
    private Map<String, CustomAttributeBean> attributeMap = new HashMap<String, CustomAttributeBean>();

    /**
     * Overriding the equals such that we can create an alphabetical sort on the
     * output
     */
    @Override
    public boolean equals(Object component) {

        if (component == null) {
            return false;
        }
        if (component == this) {
            return true;
        }
        if (!(component instanceof ComponentModel)) {
            return false;
        }

        return ((ComponentModel) component).getName().equals(getName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((componentId == null) ? 0 : componentId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public void addNewLicense(LicenseModel license) {
        if (licenses == null) {
            licenses = new ArrayList<LicenseModel>();
            licenses.add(license);
        } else {
            if (!licenses.contains(license)) {
                licenses.add(license);
            }
        }

    }

    public void addNewCopyright(String copyright) {
        if (copyrights == null) {
            copyrights = new ArrayList<String>();
            copyrights.add(copyright);
        } else {
            if (!copyrights.contains(copyright)) {
                copyrights.add(copyright);
            }
        }

    }

    public void addNewPath(String path) {
        if (paths == null) {
            paths = new HashSet<String>();
            paths.add(path);
        } else {
            if (!paths.contains(path)) {
                paths.add(path);
            }
        }

    }

    /**
     * Concatenated Name and Version
     * 
     * @return
     */
    public String getNameAndVersion() {
        return getName() + ":" + getVersion();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
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

    @Override
    public String getVersion() {
        return version;
    }

    @Override
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

    /**
     * Returns a list of licenses specific to this Component Model.
     * 
     * @return
     */
    public List<LicenseModel> getLicenseModels()
    {
        return licenses;
    }
}
