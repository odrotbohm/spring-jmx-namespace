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
                new LoggerSelector(
                        "org.springframework", 2);

        Set<String> result = selector.getLoggersFor(LOGGER_NAME);

        assertThat(result.size(), is(3));
        assertThat(result, hasItem("org.springframework"));
        assertThat(result, hasItem("org.springframework.jmx"));
        assertThat(result, hasItem("org.springframework.jmx.config"));
    }
}
