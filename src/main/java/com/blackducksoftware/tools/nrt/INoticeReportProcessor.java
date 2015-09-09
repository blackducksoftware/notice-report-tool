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

import com.blackducksoftware.tools.nrt.model.ComponentModel;

/**
 * Interface outlining processor behavior.
 * 
 * @author akamen
 * 
 */
public interface INoticeReportProcessor {
    /**
     * 
     * @param projectName
     *            Name of Protex or CC project/application
     * @return
     * @throws Exception
     */
    public HashMap<String, ComponentModel> processProject(String projectName)
	    throws Exception;
}
