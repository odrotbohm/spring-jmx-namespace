package org.springframework.jmx.config;

import static org.springframework.util.StringUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Helper class to find logger names above a certain base package with a sertain
 * deepness. So given a base package of {@code foo} and a level of 2 it would
 * select all loggers form a given candidate that match the base package and
 * have two more levels of nesting.
 * 
 * @author Oliver Gierke
 */
class LoggerSelector {

    private final String basePackage;
    private final int levels;


    public LoggerSelector(String basePackage, int levels) {

        this.basePackage = basePackage;
        this.levels = levels;
    }


    /**
     * Returns all logger names for the given source logger.
     * 
     * @param source
     * @return
     */
    public Set<String> getLoggersFor(String source) {

        return getLoggersFor(source, new ArrayList<String>());
    }


    /**
     * Returns all logger names for the given source logger but omitting the
     * ones also given.
     * 
     * @param source
     * @param alreadyFound
     * @return
     */
    public Set<String> getLoggersFor(String source,
            Collection<String> alreadyFound) {

        Set<String> result = new HashSet<String>();

        if (source.equals(basePackage)) {

            if (!alreadyFound.contains(source)) {
                result.add(source);
            }

            return result;
        }

        String toChop =
                source.substring(basePackage.length() + 1, source.length());
        String[] parts = delimitedListToStringArray(toChop, ".");

        for (int i = 0; i <= levels; i++) {

            String suffix =
                    arrayToDelimitedString(Arrays.copyOf(parts, i), ".");
            String candidate =
                    hasText(suffix) ? String.format("%s.%s", basePackage,
                            suffix) : basePackage;

            if (!alreadyFound.contains(candidate)) {
                result.add(candidate);
            }
        }

        return result;
    }
}