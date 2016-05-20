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
package com.blackducksoftware.tools.nrt;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants.APPLICATION;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.tools.nrt.model.ComponentModel;

/**
 * As of 5/20/16 removing this class. Feature no longer needed and too difficult to support.
 * 
 * @author akamen
 * 
 */

@Deprecated
public class CCNoticeReportProcessor implements INoticeReportProcessor {

    final private Logger log = Logger.getLogger(this.getClass());

    private NRTConfigurationManager nrtConfigManager = null;

    private CodeCenterServerWrapper ccWrapper = null;

    public enum ATTRIBUTE_COMPONENT_VALUES {
        LICENSE
    };

    public CCNoticeReportProcessor(NRTConfigurationManager nrtConfigManager,
            APPLICATION bdsAppType) throws Exception {
        throw new UnsupportedOperationException("This feature has been disabled.");
    }

    /*
     * (non-JSDoc)
     * 
     * @see com.blackducksoftware.tools.nrt.INoticeReportProcessor#processProject(java.lang.String)
     */
    @Override
    public HashMap<String, ComponentModel> processProject(String projectName) throws Exception {
        // TODO Auto-generated function stub
        return null;
    }

}
