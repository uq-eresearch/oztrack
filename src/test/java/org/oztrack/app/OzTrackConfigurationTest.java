package org.oztrack.app;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 12:58 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = TestConstants.TEST_CONTEXT)
public class OzTrackConfigurationTest {
    private Properties properties = new Properties();

    @Autowired
    private OzTrackConfiguration registryConfigurationImpl;

    @Before
    public void setUp() throws Exception {
        //Get the actual values

        InputStream resourceAsStream = null;
        try {
            resourceAsStream = OzTrackConfigurationImpl.class.getResourceAsStream("/conf/properties/test.properties");
            if (resourceAsStream == null) {
                throw new Exception("Configuration file not found, please ensure there is a 'registry.properties' on the classpath");
            }
            properties.load(resourceAsStream);
        } catch (IOException ex) {
            throw new Exception("Failed to load configuration properties", ex);
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException ex) {
                    // so what?
                }
            }
        }
    }

    @Test
    public void testGetVersion() throws Exception {
        String expectedVersionNumber = registryConfigurationImpl.getVersion();
        String actualVersion = getProperty(properties, "application.version", "null");
        assertEquals("Version Number", expectedVersionNumber, actualVersion);
    }

    private static String getProperty(Properties properties, String propertyName, String defaultValue) {
        String result = properties.getProperty(propertyName);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }
}
