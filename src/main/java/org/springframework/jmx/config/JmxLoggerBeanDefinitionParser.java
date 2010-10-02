package org.springframework.jmx.config;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

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

    /**
     * Base class to implement auto discovery of loggers.
     * 
     * @author Oliver Gierke
     */
    private static abstract class AbstractAutoDiscoveringLoggerBeanDefinitionParser
            implements BeanDefinitionParser {

        private final BeanDefinitionBuilder exporterBuilder;


        public AbstractAutoDiscoveringLoggerBeanDefinitionParser(
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

            for (String loggerSource : getSources(basePackage)) {

                for (JmxLogger toBeRegistered : selector.getLoggersFor(
                        loggerSource, alreadyRegistered)) {

                    String defName =
                            registerBeanDefinition(toBeRegistered,
                                    parserContext, source);

                    beans.put(toBeRegistered.getJmxName(),
                            new RuntimeBeanReference(defName));
                    alreadyRegistered.add(toBeRegistered);
                }
            }

            exporterBuilder.addPropertyValue("beans", beans);
            return null;
        }


        protected static AbstractBeanDefinition getSourcedBeanDefinition(
                BeanDefinitionBuilder builder, Object source) {

            AbstractBeanDefinition definition = builder.getBeanDefinition();
            definition.setSource(source);
            definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            return definition;
        }


        protected abstract Iterable<String> getSources(String basePackage);


        protected abstract String registerBeanDefinition(JmxLogger logger,
                ParserContext parserContext, Object source);
    }

    /**
     * Log4J based auto discovering logger {@link BeanDefinitionParser}.
     * 
     * @author Oliver Gierke
     */
    private static class Log4JAutoDiscoveringLoggerBeanDefinitionParser extends
            AbstractAutoDiscoveringLoggerBeanDefinitionParser {

        public Log4JAutoDiscoveringLoggerBeanDefinitionParser(
                BeanDefinitionBuilder builder) {

            super(builder);
        }


        @Override
        protected Iterable<String> getSources(String basePackage) {

            Set<String> sources = new HashSet<String>();

            for (Enumeration<?> e = LogManager.getCurrentLoggers(); e
                    .hasMoreElements();) {
                String loggerName = ((Logger) e.nextElement()).getName();

                if (loggerName.startsWith(basePackage)) {
                    sources.add(loggerName);
                }
            }
            return sources;
        }


        @Override
        protected String registerBeanDefinition(JmxLogger logger,
                ParserContext parserContext, Object source) {

            BeanDefinitionBuilder builder =
                    BeanDefinitionBuilder
                            .rootBeanDefinition("org.apache.log4j.jmx.LoggerDynamicMBean");
            builder.addConstructorArgValue(getLoggerBeanDefinition(
                    logger.getName(), source));

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

    }
}
