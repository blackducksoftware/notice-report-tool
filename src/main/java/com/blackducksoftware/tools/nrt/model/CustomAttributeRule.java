package com.blackducksoftware.tools.nrt.model;

public class CustomAttributeRule {

	public static enum ATTRIBUTE_TYPE {FILTER, OVERRIDE};
	
	public static enum OVERRIDE_TYPE {LICENSE};
	
	private String name;
	private String value;
	
	private ATTRIBUTE_TYPE attributeType;

	public CustomAttributeRule(ATTRIBUTE_TYPE type, String name, String value)
	{
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
