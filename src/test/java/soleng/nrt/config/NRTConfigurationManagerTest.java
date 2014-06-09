package soleng.nrt.config;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import soleng.framework.core.config.ConfigConstants;

import com.blackducksoftware.soleng.nrt.config.NRTConfigurationManager;
import com.blackducksoftware.soleng.nrt.model.CustomAttributeRule;
import com.blackducksoftware.soleng.nrt.model.CustomAttributeRule.ATTRIBUTE_TYPE;

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
			configManager = new NRTConfigurationManager(fullConfigFileLocation, ConfigConstants.APPLICATION.CODECENTER);
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
