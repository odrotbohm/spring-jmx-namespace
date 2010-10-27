package org.springframework.jmx.config;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Integration test for {@link JmxLoggerBeanDefinitionParser}.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:logback-logger.xml")
public class LogbackLoggerBeanDefinitionParserIntegrationTest {

    @Autowired
    List<LogbackJmxLogger> logger;


    @Test
    public void testname() throws Exception {

        assertNotNull(logger);
        assertFalse(logger.isEmpty());
    }
}
