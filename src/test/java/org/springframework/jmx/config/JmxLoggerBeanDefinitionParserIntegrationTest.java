package org.springframework.jmx.config;

import static org.junit.Assert.*;

import org.apache.log4j.jmx.LoggerDynamicMBean;
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
@ContextConfiguration("classpath:jmx-logger.xml")
public class JmxLoggerBeanDefinitionParserIntegrationTest {

    @Autowired
    LoggerDynamicMBean logger;


    @Test
    public void testname() throws Exception {

        assertNotNull(logger);
    }
}
