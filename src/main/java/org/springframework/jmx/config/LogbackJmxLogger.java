package org.springframework.jmx.config;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


/**
 * Simple wrapper class to easily expose a Logback logger to JMX.
 * 
 * @author Oliver Gierke
 */
@ManagedResource
public class LogbackJmxLogger {

    private final Logger logger;


    public LogbackJmxLogger(Logger logger) {

        this.logger = logger;
    }


    @ManagedAttribute
    public String getName() {

        return logger.getName();
    }


    @ManagedAttribute
    public String getLevel() {

        Level level = logger.getLevel();
        return level == null ? "" : level.toString();
    }


    @ManagedAttribute
    public void setLevel(String level) {

        Level toSet = Level.toLevel(level);

        // Level.toLevel defaults to DEBUG, so only set it if input was DEBUG
        // already
        if (!"DEBUG".equals(level) && Level.DEBUG.equals(toSet)) {
            return;
        }

        this.logger.setLevel(toSet);
    }
}