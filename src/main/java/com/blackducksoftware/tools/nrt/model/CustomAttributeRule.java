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

public class CustomAttributeRule {

    public static enum ATTRIBUTE_TYPE {
	FILTER, OVERRIDE
    };

    public static enum OVERRIDE_TYPE {
	LICENSE
    };

    private String name;
    private String value;

    private ATTRIBUTE_TYPE attributeType;

    public CustomAttributeRule(ATTRIBUTE_TYPE type, String name, String value) {
	this.name = name;
	this.value = value;
	this.attributeType = type;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public ATTRIBUTE_TYPE getAttributeType() {
	return attributeType;
    }

    public void setAttributeType(ATTRIBUTE_TYPE attributeType) {
	this.attributeType = attributeType;
    }
}
