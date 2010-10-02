package org.springframework.jmx.config;

import org.springframework.util.Assert;


/**
 * Candidate to become a JMX exposed logger. Value object to ask it whether it
 * might serve as group as well.
 * 
 * @author Oliver Gierke
 */
class Candidate {

    public static final Candidate NO_GROUP_CANDIDATE = new Candidate("<NULL>",
            0) {

        @Override
        public String getName() {

            return null;
        };
    };

    private final String name;
    private final int level;


    /**
     * Creates a new {@link Candidate}.
     * 
     * @param name
     * @param level
     */
    public Candidate(String name, int level) {

        Assert.hasText(name);
        this.name = name;
        this.level = level;
    }


    /**
     * Returns the name of the candidate.
     * 
     * @return the name
     */
    public String getName() {

        return name;
    };


    /**
     * Returns whther the {@link Candidate} is a grouping one.
     * 
     * @return
     */
    public boolean isGroupCandidate() {

        return this.level == 1;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        Candidate that = (Candidate) obj;

        return this.name.equals(that.name) && this.level == that.level;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int result = 17;
        result += 31 * name.hashCode();
        result += 31 * level;
        return result;
    }
}