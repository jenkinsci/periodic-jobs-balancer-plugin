package com.paypal.jenkinsci.project.scanner.properties;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class PropertiesUtilTest {

    @Test
    public void testProperties() {
//				logfilename=projects_scanner_logger.log
//				lowFrequencyIconFileName=/jenkins/plugin/projects-scanner/icons/low.png
//				mediumLowFrequencyIconFileName=/jenkins/plugin/projects-scanner/icons/medium_low.png
//				mediumHighFrequencyIconFileName=/jenkins/plugin/projects-scanner/icons/medium_high.png
//				highFrequencyIconFileName=/jenkins/plugin/projects-scanner/icons/high.jpg
//				veryHighFrequencyIconFileName=/jenkins/plugin/projects-scanner/icons/very_high.png
//				extHighFrequencyIconFileName=/jenkins/plugin/projects-scanner/icons/extremely_high.png

        assertNotNull(PropertiesUtil.getInstance());
        assertNotNull(PropertiesUtil.getInstance().getProperty("logfilename"));
        assertNotNull(PropertiesUtil.getInstance().getProperty("lowFrequencyIconFileName"));
        assertNotNull(PropertiesUtil.getInstance().getProperty("mediumLowFrequencyIconFileName"));
        assertNotNull(PropertiesUtil.getInstance().getProperty("mediumHighFrequencyIconFileName"));
        assertNotNull(PropertiesUtil.getInstance().getProperty("highFrequencyIconFileName"));
        assertNotNull(PropertiesUtil.getInstance().getProperty("veryHighFrequencyIconFileName"));
        assertNotNull(PropertiesUtil.getInstance().getProperty("extHighFrequencyIconFileName"));


    }


}
