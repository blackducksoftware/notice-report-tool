package com.blackducksoftware.tools.nrt.model;

/**
 * Bean representing the abstract attribute object from CodeCenter
 * @author akamen
 *
 */
public class CustomAttributeBean {

	private String id;
	private String name;
	private String value;
	private String description;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
