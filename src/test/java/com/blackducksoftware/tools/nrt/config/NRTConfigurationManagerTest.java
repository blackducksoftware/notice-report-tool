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
package com.blackducksoftware.tools.nrt.config;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants;
import com.blackducksoftware.tools.nrt.model.CustomAttributeRule;
import com.blackducksoftware.tools.nrt.model.CustomAttributeRule.ATTRIBUTE_TYPE;

/**
 * Tests the notice report tool's configuration manager
 * 
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
        CustomAttributeRule filterAttribute = filterAttributes.get(0);

        Assert.assertEquals(filterAttribute.getName(), "Include In Report");
        Assert.assertEquals(filterAttribute.getValue(), "No");
        Assert.assertEquals(filterAttribute.getAttributeType(), ATTRIBUTE_TYPE.FILTER);

        Assert.assertEquals(overrideAttribute.getName(), "My_Overrider");
        Assert.assertEquals(overrideAttribute.getValue(), "License");
        Assert.assertEquals(overrideAttribute.getAttributeType(), ATTRIBUTE_TYPE.OVERRIDE);
    }

}
