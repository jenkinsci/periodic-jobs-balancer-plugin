package com.paypal.jenkinsci.project.scanner.logger;

import com.paypal.jenkinsci.project.scanner.properties.PropertiesUtil;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * @author otahboub
 *         The purpose of this class is to enable critical events logging into a specific log file
 */
public final class EventsLogger {

    private Logger logger;
    private FileHandler fileHandler;

    private static EventsLogger instance;

    private EventsLogger() {

        try {
            logger = Logger.getLogger("periodic-scanner-logger");
            fileHandler = new FileHandler(PropertiesUtil.getInstance().getProperty("logfilename"), true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
    }

    public static EventsLogger getInstance() {

        if (instance == null) {
            instance = new EventsLogger();
        }
        return instance;
    }

    public void logInfo(String logEvent) {
        logger.info(logEvent);
    }

    public void logWarning(String logEvent) {
        logger.warning(logEvent);
    }

    public void logError(String logEvent) {
        logger.log(Level.SEVERE, logEvent);
    }


}
