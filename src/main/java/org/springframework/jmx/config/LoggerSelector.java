package org.springframework.jmx.config;

import static org.springframework.util.StringUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    private final boolean omitClassLoggers;


    public LoggerSelector(String basePackage, int levels,
            boolean omitClassLoggers) {

        this.basePackage = basePackage;
        this.levels = levels;
        this.omitClassLoggers = omitClassLoggers;
    }


    /**
     * Returns all logger names for the given source logger.
     * 
     * @param source
     * @return
     */
    public Set<JmxLogger> getLoggersFor(String source) {

        return getLoggersFor(source, new JmxLoggers());
    }


    /**
     * Returns all logger names for the given source logger but omitting the
     * ones also given.
     * 
     * @param source
     * @param alreadyFound
     * @return
     */
    public Set<JmxLogger> getLoggersFor(String source, JmxLoggers alreadyFound) {

        CandidateSource loggerSource =
                new CandidateSource(basePackage, source, omitClassLoggers);
        Candidate groupCandidate = Candidate.NO_GROUP_CANDIDATE;
        Set<JmxLogger> result = new HashSet<JmxLogger>();

        for (org.springframework.jmx.config.Candidate candidate : loggerSource
                .getCandidatesUpToLevel(levels)) {

            if (candidate.isGroupCandidate()) {
                groupCandidate = candidate;
            }

            if (!alreadyFound.containsLoggerFor(candidate)) {

                JmxLogger jmxLogger =
                        new JmxLogger(candidate.getName(),
                                groupCandidate.getName());
                result.add(jmxLogger);
            }
        }

        return result;
    }

    /**
     * Class to provide {@link JmxLogger} {@link Candidate}s given a base
     * package and the {@link String} source to retrieve {@link Candidate}s
     * from.
     * 
     * @author Oliver Gierke
     */
    static class CandidateSource {

        private String basePackage;
        private String[] subParts;


        public CandidateSource(String basePackage, String source,
                boolean omitClassLoggers) {

            this.basePackage = basePackage;

            if (source.equals(basePackage)) {
                this.subParts = new String[0];
            } else {
                String toChop =
                        source.substring(basePackage.length() + 1,
                                source.length());
                this.subParts = delimitedListToStringArray(toChop, ".");

                if (lastPartIsClass(subParts) && omitClassLoggers) {
                    this.subParts =
                            Arrays.copyOf(this.subParts, subParts.length - 1);
                }
            }
        }


        private final boolean lastPartIsClass(String[] parts) {

            return parts[parts.length - 1].matches("[A-Z].*");
        }


        public List<Candidate> getCandidatesUpToLevel(int levels) {

            List<Candidate> candidates = new ArrayList<Candidate>();

            for (int i = 0; i <= levels && i <= subParts.length; i++) {

                String suffix =
                        i > subParts.length ? null : arrayToDelimitedString(
                                Arrays.copyOf(subParts, i), ".");
                String candidateString =
                        hasText(suffix) ? String.format("%s.%s", basePackage,
                                suffix) : basePackage;

                candidates.add(new Candidate(candidateString, i));
            }

            return candidates;
        }
    }
}