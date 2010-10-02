package org.springframework.jmx.config;

import org.springframework.util.Assert;


/**
 * Value object to capture a JMX exposed logger.
 * 
 * @author Oliver Gierke
 */
final class JmxLogger {

    private static final String OVERALL_NAME = "Spring JMX loggers";

    private final String name;
    private final String group;


    /**
     * Creates a new {@link JmxLogger}.
     * 
     * @param name
     * @param group
     */
    public JmxLogger(String name, String group) {

        Assert.hasText(name);
        this.name = name;
        this.group = group;
    }


    /**
     * Builds the JMX object name to register the logger under.
     * 
     * @return
     */
    public String getJmxName() {

        StringBuilder builder = new StringBuilder(OVERALL_NAME);
        builder.append(":name=").append(name);

        if (group != null) {
            builder.append(",type=").append(group);
        }

        return builder.toString();
    }


    /**
     * @return
     */
    public String getName() {

        return name;
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

        JmxLogger that = (JmxLogger) obj;

        boolean nameEqual = this.name.equals(that.name);
        boolean groupEqual =
                group == null ? that.group == null : this.group
                        .equals(that.group);

        return nameEqual && groupEqual;
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
        result += 31 * (group == null ? 0 : group.hashCode());
        return result;
    }
}
