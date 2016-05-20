/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
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

        if (license == null) {
            return false;
        }
        if (license == this) {
            return true;
        }
        if (!(license instanceof LicenseModel)) {
            return false;
        }

        LicenseModel licenseModel = (LicenseModel) license;

        if (licenseModel.getId() != null && getId() != null) {
            return licenseModel.getId().equals(getId());
        } else {
            return licenseModel.getText().equals(getText());
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public String getLicenseOriginType() {
        return licenseOriginType;
    }

    public void setLicenseOriginType(String licenseOriginType) {
        this.licenseOriginType = licenseOriginType;
    }

}
