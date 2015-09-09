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
package com.blackducksoftware.tools.nrt.config;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants;
import com.blackducksoftware.tools.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.tools.nrt.model.CustomAttributeRule;
import com.blackducksoftware.tools.nrt.model.CustomAttributeRule.ATTRIBUTE_TYPE;

/**
 * Tests the notice report tool's configuration manager
 * @author akamen
 *
 */
public class NRTConfigurationManagerTest 
{
	// Custom config file
	private static String configFile = "nrt_config_with_custom_attributes.properties";
	
	private static NRTConfigurationManager configManager = null;
	
	@BeforeClass 
	public static void setupFiles() throws IOException	
	{		
		String fullConfigFileLocation = ClassLoader.getSystemResource(configFile).getFile();
		try {
			String projectName = null;
			configManager = 
					new NRTConfigurationManager(
							fullConfigFileLocation, ConfigConstants.APPLICATION.CODECENTER, projectName);
		} catch (Exception e) {
			Assert.fail("Could not create configuration manager!: " + e.getMessage());
		}
	}
	
	@Test
	public void testCustomAttributes()
	{
		List<CustomAttributeRule> overrideAttributes = configManager.getCustomAttributeRules(ATTRIBUTE_TYPE.OVERRIDE);
		List<CustomAttributeRule> filterAttributes = configManager.getCustomAttributeRules(ATTRIBUTE_TYPE.FILTER);
		
		// Expect two
		Assert.assertEquals(1, overrideAttributes.size());
		Assert.assertEquals(1, filterAttributes.size());
		
		// Test their name and value
		CustomAttributeRule overrideAttribute = overrideAttributes.get(0);
		CustomAttributeRule filterAttribute  = filterAttributes.get(0);
		
		Assert.assertEquals(filterAttribute.getName(),"Include In Report");
		Assert.assertEquals(filterAttribute.getValue(),"No");
		Assert.assertEquals(filterAttribute.getAttributeType(),ATTRIBUTE_TYPE.FILTER);
		
		Assert.assertEquals(overrideAttribute.getName(),"My_Overrider");
		Assert.assertEquals(overrideAttribute.getValue(),"License");
		Assert.assertEquals(overrideAttribute.getAttributeType(),ATTRIBUTE_TYPE.OVERRIDE);
	}
	
}
