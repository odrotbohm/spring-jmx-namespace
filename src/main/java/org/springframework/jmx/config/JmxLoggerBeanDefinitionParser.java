package org.springframework.jmx.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


/**
 * Simple delegating {@link BeanDefinitionParser} that uses a
 * log-library-specific {@link BeanDefinitionParser} to expose loggers via JMX.
 * 
 * @author Oliver Gierke
 */
class JmxLoggerBeanDefinitionParser implements BeanDefinitionParser {

    private static final Map<String, BeanDefinitionParser> BEAN_CLASSES =
            new HashMap<String, BeanDefinitionParser>();

    static {
        BEAN_CLASSES.put("Log4J", new Log4jJmxLoggerBeanDefinitionParser());
    }


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        BeanDefinitionParser delegate =
                BEAN_CLASSES.get(element.getAttribute("type"));
        return delegate.parse(element, parserContext);
    }

    /**
     * {@link BeanDefinitionParser} to create a {@link BeanDefinition} for a
     * Log4J logger.
     * 
     * @author Oliver Gierke
     */
    private static class Log4jJmxLoggerBeanDefinitionParser extends
            AbstractSingleBeanDefinitionParser {

        @Override
        protected String getBeanClassName(Element element) {

            return "org.apache.log4j.jmx.LoggerDynamicMBean";
        }


        @Override
        protected boolean shouldGenerateId() {

            return true;
        }


        @Override
        protected void doParse(Element element, BeanDefinitionBuilder builder) {

            BeanDefinitionBuilder builder2 =
                    BeanDefinitionBuilder
                            .rootBeanDefinition("org.apache.log4j.Logger");
            builder2.setFactoryMethod("getLogger");
            builder2.addConstructorArgValue(element.getAttribute("name"));

            builder.addConstructorArgValue(builder2.getBeanDefinition());
        }
    }
}
