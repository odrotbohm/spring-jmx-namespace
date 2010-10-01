package org.springframework.jmx.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * Registers {@link org.springframework.beans.factory.xml.BeanDefinitionParser}s
 * got JMX namespace.
 * 
 * @author Oliver Gierke
 */
public class JmxNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {

        registerBeanDefinitionParser("logger",
                new JmxLoggerBeanDefinitionParser());
    }
}
