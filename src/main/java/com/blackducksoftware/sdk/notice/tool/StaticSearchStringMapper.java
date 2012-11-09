package com.blackducksoftware.sdk.notice.tool;

import org.apache.log4j.Logger;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxyV6_1;
import com.blackducksoftware.sdk.protex.common.StringSearchPattern;
import com.blackducksoftware.sdk.protex.license.GlobalLicense;

/**
 *  @author jatoui
 *  @title Solutions Architect
 *  @email jatoui@blackducksoftware.com
 *  @company Black Duck Software
 *  @year 2012
 **/

public class StaticSearchStringMapper {

	Logger log = Logger.getLogger(this.getClass().getName());

	private ProtexServerProxyV6_1 proxy;

	private StaticLicenseMapper licenseMapper;

	public StaticLicenseMapper getLicenseMapper() {
		return licenseMapper;
	}

	public void setLicenseMapper(StaticLicenseMapper licenseMapper) {
		this.licenseMapper = licenseMapper;
	}

	public ProtexServerProxyV6_1 getProxy() {
		return proxy;
	}

	public void setProxy(ProtexServerProxyV6_1 proxy) {
		this.proxy = proxy;
	}

	// gets associated license for a String Search pattern
	public String getLicenseData(String searchName) {

		//return pattern object
		StringSearchPattern pattern = null;
		try {
			pattern = proxy.getPolicyApi().getStringSearchPatternByName(
					searchName);
		} catch (SdkFault e) {
			log.warn("Could not determine search pattern for pattern name: "
					+ searchName, e);
		}

		
		if (pattern != null) {
			String licenseId = pattern.getAssociatedLicenseId();
			if (licenseId != null) {
				//ensures that the GlobalLicense object is added to the licenseMapper
				GlobalLicense lic = licenseMapper.getLicenseDataById(licenseId);
				
				if(lic != null)
					return lic.getLicenseId();
			}
		}

		return null;
	}

}
