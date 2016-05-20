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
package com.blackducksoftware.tools.nrt.codecenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.blackducksoftware.sdk.codecenter.attribute.data.AbstractAttribute;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeNameOrIdToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.request.data.RequestIdToken;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValuePojo;
import com.blackducksoftware.tools.nrt.model.ComponentModel;
import com.blackducksoftware.tools.nrt.model.CustomAttributeBean;

/**
 * Processor for all the custom attributes. Looks up attributes by ID from the
 * attribute API, stores them internally for faster lookup and creates simpler
 * abstractions that are then attached to each component.
 * 
 * 
 * @author akamen
 * 
 */
public class NoticeReportCustomAttributeProcessor {

    final private Logger log = Logger.getLogger(this.getClass());

    private CodeCenterServerWrapper ccWrapper = null;

    private Map<AttributeNameOrIdToken, AbstractAttribute> attributeMap = new HashMap<AttributeNameOrIdToken, AbstractAttribute>();

    public NoticeReportCustomAttributeProcessor(
            CodeCenterServerWrapper ccWrapper) {
        this.ccWrapper = ccWrapper;
    }

    /**
     * Looks through the custom attributes for: - Component - Request And then
     * builds a map of attributes per component. Because attributes are stored
     * internally, becomes more efficient as more components are processed
     * 
     * @param comp
     * @param requestIdToken
     * @param compModel
     */
    public void processCustomAttributesForComponent(String requestId, ComponentModel compModel) {
        List<CustomAttributeBean> requestAttributes = collectRequestAttributeIDs(requestId);
        List<CustomAttributeBean> componentAttributes = collectComponentAttributeIDs(compModel);

        populateAttributeMapForComponent(requestAttributes, compModel);
        populateAttributeMapForComponent(componentAttributes, compModel);
    }

    private void populateAttributeMapForComponent(
            List<CustomAttributeBean> atts, ComponentModel comp) {
        Map<String, CustomAttributeBean> map = comp.getAttributeMap();

        for (CustomAttributeBean att : atts) {
            map.put(att.getName(), att);
            log.debug("Adding attribute: " + att.getName() + " to component: "
                    + comp.getNameAndVersion());
        }

        comp.setAttributeMap(map);
    }

    public List<CustomAttributeBean> collectRequestAttributeIDs(
            String reqId) {
        List<CustomAttributeBean> attributes = null;
        try {

            RequestIdToken reqToken = new RequestIdToken();
            reqToken.setId(reqId);

            List<AttributeValue> requestAttributeValues = ccWrapper
                    .getInternalApiWrapper().getRequestApi().getRequest(reqToken)
                    .getAttributeValues();

            // TODO: Add this method to the Common Framework Request Manager
            // attributes = getAttributesFromMap(requestAttributeValues);

        } catch (SdkFault e) {
            log.warn("Unable to get request  attributes for id:" + reqId);
        }

        return attributes;
    }

    public List<CustomAttributeBean> collectComponentAttributeIDs(ComponentModel comp) {

        List<CustomAttributeBean> attributes = getAttributesFromMap(comp.getAttributeValues().values());
        return attributes;

    }

    /**
     * Checks to see if this attribute has been retrieved before, if not adds it
     * to the map.
     * 
     * @param componentAttributes
     * @param internaList
     */
    public List<CustomAttributeBean> getAttributesFromMap(Collection<AttributeValuePojo> attributeValuePojos) {
        List<CustomAttributeBean> attributeBeans = new ArrayList<CustomAttributeBean>();

        for (AttributeValuePojo attributePojo : attributeValuePojos) {

            if (attributePojo != null) {
                AttributeIdToken token = new AttributeIdToken();
                token.setId(attributePojo.getAttrId());

                if (token != null) {
                    CustomAttributeBean bean = new CustomAttributeBean();
                    bean.setValue(attributePojo.getValue());

                    AbstractAttribute abAttribute = attributeMap.get(token);

                    if (abAttribute == null) {
                        try {
                            abAttribute = ccWrapper.getInternalApiWrapper()
                                    .getAttributeApi().getAttribute(token);
                            attributeMap.put(token, abAttribute);
                        } catch (Exception e) {
                            log.warn("Failed getting attribute information: "
                                    + e.getMessage());
                            log.warn("Token id:" + token.toString());
                        }
                    }

                    attributeBeans.add(populateBeanFromAbstraction(bean,
                            abAttribute));
                }
            }
        }

        return attributeBeans;
    }

    private CustomAttributeBean populateBeanFromAbstraction(
            CustomAttributeBean bean, AbstractAttribute abAttribute) {
        bean.setId(abAttribute.getId().getId());
        bean.setName(abAttribute.getQuestion());
        bean.setDescription(abAttribute.getDescription());

        return bean;
    }
}
