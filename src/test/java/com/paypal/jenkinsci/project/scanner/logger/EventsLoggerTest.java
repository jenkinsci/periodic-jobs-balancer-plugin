package com.paypal.jenkinsci.project.scanner.logger;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@PrepareForTest({java.util.logging.Logger.class, java.util.logging.FileHandler.class})

public class EventsLoggerTest {

    @Test
    public void testEventsLogger() {

        EventsLogger logger = EventsLogger.getInstance();
        assertNotNull(logger);

        logger.logInfo("Action was performed");
        logger.logWarning("Action was perfomed");
        logger.logError("A Fatal action was perfomed");

    }

    @Test(expected = Exception.class)
    public void testEventsLoggerInstanceWhenExceptionisThrown() {

        mockStatic(com.paypal.jenkinsci.project.scanner.properties.PropertiesUtil.class);
        PowerMockito.spy(com.paypal.jenkinsci.project.scanner.properties.PropertiesUtil.class);
        PowerMockito.doThrow(new Exception()).when(com.paypal.jenkinsci.project.scanner.properties.PropertiesUtil.getInstance().getProperty(Mockito.anyString()));

        EventsLogger log = EventsLogger.getInstance();
        log.logInfo("Action was performed");


    }


}
