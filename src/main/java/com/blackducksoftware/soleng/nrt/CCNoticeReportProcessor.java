package com.blackducksoftware.soleng.nrt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import soleng.framework.core.config.ConfigConstants.APPLICATION;
import soleng.framework.standard.codecenter.CodeCenterServerWrapper;

import com.blackducksoftware.sdk.codecenter.application.ApplicationApi;
import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.License;
import com.blackducksoftware.sdk.codecenter.cola.data.LicenseSummary;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;
import com.blackducksoftware.soleng.nrt.codecenter.NoticeReportCustomAttributeProcessor;
import com.blackducksoftware.soleng.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.soleng.nrt.model.ComponentModel;
import com.blackducksoftware.soleng.nrt.model.CustomAttributeBean;
import com.blackducksoftware.soleng.nrt.model.CustomAttributeRule;
import com.blackducksoftware.soleng.nrt.model.CustomAttributeRule.ATTRIBUTE_TYPE;
import com.blackducksoftware.soleng.nrt.model.CustomAttributeRule.OVERRIDE_TYPE;
import com.blackducksoftware.soleng.nrt.model.LicenseModel;

public class CCNoticeReportProcessor implements INoticeReportProcessor
{

    private Logger log = Logger.getLogger(this.getClass());

    private NRTConfigurationManager nrtConfigManager = null;
    private CodeCenterServerWrapper ccWrapper = null;
    private NoticeReportCustomAttributeProcessor nrtCaProcessor = null;

    public enum ATTRIBUTE_COMPONENT_VALUES
    {
	LICENSE
    };

    public CCNoticeReportProcessor(NRTConfigurationManager nrtConfigManager,
	    APPLICATION bdsAppType) throws Exception
    {
	this.nrtConfigManager = nrtConfigManager;
	ccWrapper = new CodeCenterServerWrapper(
		nrtConfigManager.getServerBean(), nrtConfigManager);
	nrtCaProcessor = new NoticeReportCustomAttributeProcessor(ccWrapper);
    }

    public HashMap<String, ComponentModel> processProject()
    {
	HashMap<String, ComponentModel> componentMap = new HashMap<String, ComponentModel>();
	try
	{
	    ApplicationApi aApi = ccWrapper.getInternalApiWrapper().applicationApi;
	    ApplicationNameVersionToken token = new ApplicationNameVersionToken();
	    token.setName(nrtConfigManager.getCCApplicationName());
	    token.setVersion(nrtConfigManager.getCCApplicationVersion());
	    Application app = aApi.getApplication(token);

	    List<ComponentModel> components = getComponentsForApplication(app);
	    applyCustomAttributeRules(components);

	    // Create a hashmap for the legacy HTML output
	    // TODO: Create a method that accepts a list
	    componentMap = new HashMap<String, ComponentModel>();
	    for (ComponentModel comp : components)
	    {
		String key = comp.getName() + ":" + comp.getVersion();

		if (comp.isDisplayInReport())
		    componentMap.put(key, comp);
		else
		{
		    log.debug("Excluding component from report: "
			    + comp.getNameAndVersion());
		}
	    }

	} catch (Exception e)
	{
	    log.error("Unable to process report", e);
	}

	return componentMap;

    }

    private List<ComponentModel> getComponentsForApplication(Application app)
    {
	List<ComponentModel> components = new ArrayList<ComponentModel>();
	try
	{
	    log.debug("Gathering list of components for application: "
		    + app.getName());
	    List<RequestSummary> requests = ccWrapper.getInternalApiWrapper().applicationApi
		    .getApplicationRequests(app.getNameVersion());

	    for (RequestSummary request : requests)
	    {
		// Get the individual component
		ComponentModel compModel = new ComponentModel();
		ColaApi cApi = ccWrapper.getInternalApiWrapper().colaApi;
		Component comp = cApi.getCatalogComponent(request
			.getApplicationComponentToken().getComponentId());

		log.debug("Got component: " + comp.getName() + ":"
			+ comp.getVersion());

		compModel.setComponentId(comp.getId().getId());
		compModel.setName(comp.getName());
		compModel.setVersion(comp.getVersion());
		compModel.setHomePage(comp.getHomepage());

		// Get the associated license
		List<LicenseSummary> licSummaries = comp.getDeclaredLicenses();
		for (LicenseSummary licSummary : licSummaries)
		{
		    LicenseModel licModel = new LicenseModel();
		    License license = cApi
			    .getLicense(licSummary.getNameToken());

		    licModel.setName(license.getNameToken().getName());
		    licModel.setText(license.getText());
		    licModel.setId(license.getId().getId());

		    compModel.addNewLicense(licModel);
		}

		nrtCaProcessor.processCustomAttributesForComponent(comp,
			request.getId(), compModel);

		// Add it to list
		components.add(compModel);

	    }
	} catch (Exception e)
	{
	    log.error("Error in gathering components", e);
	}

	return components;
    }

    /**
     * Looks up the configuration to see whether user specified any rules for
     * custom attributes If so, then iterate through each component and perform
     * the necessary actions.
     * 
     * @param components
     */
    private void applyCustomAttributeRules(List<ComponentModel> components)
    {
	List<CustomAttributeRule> filterAttributeRules = nrtConfigManager
		.getCustomAttributeRules(ATTRIBUTE_TYPE.FILTER);
	for (CustomAttributeRule filterRule : filterAttributeRules)
	{
	    String filterValue = filterRule.getValue();
	    for (ComponentModel component : components)
	    {
		log.debug("Custom Attribute[filter] for custom attribute application on component: "
			+ component.getNameAndVersion());
		Map<String, CustomAttributeBean> map = component
			.getAttributeMap();
		String ruleName = filterRule.getName();

		CustomAttributeBean bean = map.get(ruleName);
		// If the bean exists, then this component has the requisite
		// settings.
		if (bean != null)
		{
		    log.debug("Filter custom attribute exists: "
			    + bean.getName());
		    // Now we check if the value of the filter rule, is the same
		    // as the value set by the custom attribute
		    if (filterValue.trim().equals(bean.getValue()))
		    {
			// If matches, then apply filter.
			component.setDisplayInReport(false);
		    }
		}
	    }
	}

	List<CustomAttributeRule> overrideAttributeRules = nrtConfigManager
		.getCustomAttributeRules(ATTRIBUTE_TYPE.OVERRIDE);
	for (CustomAttributeRule overrideRule : overrideAttributeRules)
	{
	    String overrideName = overrideRule.getName();

	    for (ComponentModel component : components)
	    {
		log.debug("Custom attribute[override] for custom attribute application on component: "
			+ component.getNameAndVersion());
		Map<String, CustomAttributeBean> map = component
			.getAttributeMap();
		CustomAttributeBean bean = map.get(overrideName);
		if (bean != null)
		{
		    log.debug("Override custom attribute exists: "
			    + bean.getName());
		    String overrideValue = overrideRule.getValue();
		    // Handle the LICENSE case for now, more will be added later
		    overrideValue = overrideValue.toLowerCase();
		    if (overrideValue.equals(OVERRIDE_TYPE.LICENSE.toString()
			    .toLowerCase()))
		    {
			log.debug(overrideValue
				+ " override rule found, applying...");
			// We want to grab the first license, and replace the
			// text with the value from the bean.
			LicenseModel licenseToOverride = component
				.getLicenses().remove(0);
			if (licenseToOverride != null)
			{
			    String appendOverrideString = ATTRIBUTE_TYPE.OVERRIDE
				    .toString();
			    String newLicenseText = bean.getValue();
			    licenseToOverride.setText(newLicenseText);
			    licenseToOverride.setName(licenseToOverride
				    .getName()
				    + "("
				    + appendOverrideString
				    + ")");
			    licenseToOverride.setId(licenseToOverride.getId()
				    + "_" + appendOverrideString);
			    component.addNewLicense(licenseToOverride);

			    log.debug("Overrode license: "
				    + licenseToOverride.getName());
			}
		    } else
		    {
			log.debug("Unrecognized override type: "
				+ overrideValue);
		    }
		}
	    }
	}
    }
}
