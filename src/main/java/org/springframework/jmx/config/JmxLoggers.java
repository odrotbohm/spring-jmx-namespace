package org.springframework.jmx.config;

import java.util.Collection;
import java.util.HashSet;


/**
 * Wrapper class for a collection of {@link JmxLogger}s. Allows determining if
 * there's already a {@link JmxLogger} registered for a given {@link Candidate}.
 * 
 * @author Oliver Gierke
 */
class JmxLoggers {

    private final Collection<JmxLogger> loggers;


    public JmxLoggers() {

        this.loggers = new HashSet<JmxLogger>();
    }


    public JmxLoggers(Collection<JmxLogger> loggers) {

        this();
        this.loggers.addAll(loggers);
    }


    public boolean containsLoggerFor(Candidate candidate) {

        for (JmxLogger logger : loggers) {
            if (logger.getName().equals(candidate.getName())) {
                return true;
            }
        }

        return false;
    }


    public void add(JmxLogger logger) {

        if (!loggers.contains(logger)) {
            loggers.add(logger);
        }
    }
}