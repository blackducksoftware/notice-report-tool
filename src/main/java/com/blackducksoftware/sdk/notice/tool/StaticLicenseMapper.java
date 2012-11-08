package com.blackducksoftware.sdk.notice.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxyV6_1;
import com.blackducksoftware.sdk.protex.license.GlobalLicense;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.license.LicenseInfoColumn;
import com.blackducksoftware.sdk.protex.license.LicenseOriginType;
import com.blackducksoftware.sdk.protex.util.PageFilterFactory;

/**
 *  @author jatoui
 *  @title Solutions Architect
 *  @email jatoui@blackducksoftware.com
 *  @company Black Duck Software
 *  @year 2012
 **/

public class StaticLicenseMapper {

	Logger log = Logger.getLogger(this.getClass().getName());

	private ProtexServerProxyV6_1 proxy;

	public ProtexServerProxyV6_1 getProxy() {
		return proxy;
	}

	public void setProxy(ProtexServerProxyV6_1 proxy) {
		this.proxy = proxy;
	}

	// keeps track of the mapping of license name to license object to
	// prevent having to call SDK method to get license
	// object when seen again
	HashMap<String, GlobalLicense> licenseMap = new HashMap<String, GlobalLicense>();

	HashMap<String, GlobalLicense> licenseIdMap = new HashMap<String, GlobalLicense>();

	public GlobalLicense getLicenseDataById(String licId) {
		
		if(licenseIdMap.containsKey(licId))
			return licenseIdMap.get(licId);
		
		GlobalLicense lic = null;
		
		try {
			lic = proxy.getLicenseApi().getLicenseById(licId);
		} catch (SdkFault e) {
			log.warn("Could not determine license for license ID: " + licId, e);
		}
		if (lic != null) {
			licenseMap.put(lic.getName(), lic);
			licenseMap.put(lic.getLicenseId(), lic);
		}
		return lic;
	}

	public GlobalLicense getLicenseData(String licName) {

		if (licenseMap.containsKey(licName))
			return licenseMap.get(licName);

		GlobalLicense lic = null;
		List<LicenseInfo> licenses = null;
		try {
			List<LicenseOriginType> licOriginTypes = new ArrayList<LicenseOriginType>();
			licOriginTypes.add(LicenseOriginType.TEMPLATE);
			licOriginTypes.add(LicenseOriginType.STANDARD);
			licOriginTypes.add(LicenseOriginType.MODIFIED_STANDARD);
			licOriginTypes.add(LicenseOriginType.CUSTOM);

			licenses = proxy.getLicenseApi().suggestLicenses(
					licName,
					licOriginTypes,
					PageFilterFactory
							.getAllRows(LicenseInfoColumn.LICENSE_NAME));
		}

		catch (SdkFault e) {
			log.warn(
					"Could not determine license for license Name: " + licName,
					e);

		}
		
		if (licenses != null)
			for (LicenseInfo license : licenses) {
				if (license.getName().equals(licName)) {
					lic = getLicenseDataById(license.getLicenseId());
				}
			}

		return lic;
	}
}
