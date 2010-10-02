package org.springframework.jmx.config;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;


/**
 * @author Oliver Gierke
 */
public class LoggerSelectorUnitTest {

    private static final String LOGGER_NAME = LoggerSelectorUnitTest.class
            .getName();


    @Test
    public void getLoggers() throws Exception {

        LoggerSelector selector =
                new LoggerSelector("org.springframework", 2, true);

        Set<JmxLogger> result = selector.getLoggersFor(LOGGER_NAME);

        assertThat(result.size(), is(3));
        assertThat(result, hasItem(new JmxLogger("org.springframework", null)));
        assertThat(result, hasItem(new JmxLogger("org.springframework.jmx",
                "org.springframework.jmx")));
        assertThat(result, hasItem(new JmxLogger(
                "org.springframework.jmx.config", "org.springframework.jmx")));

        assertTrue(selector.getLoggersFor(LOGGER_NAME, new JmxLoggers(result))
                .isEmpty());
    }
}
