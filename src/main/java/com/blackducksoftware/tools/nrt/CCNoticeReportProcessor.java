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
