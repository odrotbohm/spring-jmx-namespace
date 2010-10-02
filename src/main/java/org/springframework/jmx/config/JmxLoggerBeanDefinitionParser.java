package org.springframework.jmx.config;

import java.util.Enumeration;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.w3c.dom.Element;


/**
 * Simple delegating {@link BeanDefinitionParser} that uses a
 * log-library-specific {@link BeanDefinitionParser} to expose loggers via JMX.
 * 
 * @author Oliver Gierke
 */
class JmxLoggerBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder
                        .rootBeanDefinition(AnnotationMBeanExporter.class);

        BeanDefinitionParser delegate =
                new Log4JAutoDiscoveringLoggerBeanDefinitionParser(builder);

        BeanDefinition result = delegate.parse(element, parserContext);

        BeanDefinitionReaderUtils.registerWithGeneratedName(
                builder.getBeanDefinition(), parserContext.getRegistry());

        return result;
    }

    private static class Log4JAutoDiscoveringLoggerBeanDefinitionParser
            implements BeanDefinitionParser {

        private final BeanDefinitionBuilder exporterBuilder;


        public Log4JAutoDiscoveringLoggerBeanDefinitionParser(
                BeanDefinitionBuilder builder) {

            this.exporterBuilder = builder;
        }


        @Override
        public BeanDefinition parse(Element element, ParserContext parserContext) {

            String basePackage = element.getAttribute("name");
            int levels = Integer.parseInt(element.getAttribute("levels"));
            boolean omitClassLoggers =
                    Boolean.parseBoolean(element
                            .getAttribute("omit-class-loggers"));

            LoggerSelector selector =
                    new LoggerSelector(basePackage, levels, omitClassLoggers);
            JmxLoggers alreadyRegistered = new JmxLoggers();
            ManagedMap<String, BeanReference> beans =
                    new ManagedMap<String, BeanReference>();
            Object source =
                    parserContext.getReaderContext().extractSource(element);

            for (Enumeration<?> e = LogManager.getCurrentLoggers(); e
                    .hasMoreElements();) {
                Logger logger = (Logger) e.nextElement();

                for (JmxLogger toBeRegistered : selector.getLoggersFor(
                        logger.getName(), alreadyRegistered)) {

                    String defName =
                            registerBeanDefinition(toBeRegistered.getName(),
                                    parserContext, source);

                    beans.put(toBeRegistered.getJmxName(),
                            new RuntimeBeanReference(defName));
                    alreadyRegistered.add(toBeRegistered);
                }
            }

            exporterBuilder.addPropertyValue("beans", beans);

            return null;
        }


        private String registerBeanDefinition(String name,
                ParserContext parserContext, Object source) {

            BeanDefinitionBuilder builder =
                    BeanDefinitionBuilder
                            .rootBeanDefinition("org.apache.log4j.jmx.LoggerDynamicMBean");
            builder.addConstructorArgValue(getLoggerBeanDefinition(name, source));

            return BeanDefinitionReaderUtils.registerWithGeneratedName(
                    getSourcedBeanDefinition(builder, source),
                    parserContext.getRegistry());
        }


        private BeanDefinition getLoggerBeanDefinition(String name,
                Object source) {

            BeanDefinitionBuilder innerBuilder =
                    BeanDefinitionBuilder
                            .rootBeanDefinition("org.apache.log4j.Logger");
            innerBuilder.setFactoryMethod("getLogger");
            innerBuilder.addConstructorArgValue(name);
            return getSourcedBeanDefinition(innerBuilder, source);
        }


        private AbstractBeanDefinition getSourcedBeanDefinition(
                BeanDefinitionBuilder builder, Object source) {

            AbstractBeanDefinition definition = builder.getBeanDefinition();
            definition.setSource(source);
            definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            return definition;
        }
    }
}
