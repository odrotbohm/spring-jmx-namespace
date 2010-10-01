package org.springframework.jmx.config;

import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Simply run this one and follow the instructions in the console.
 * 
 * @author Oliver Gierke
 */
public class Sample {

    private static final Logger LOG = Logger.getLogger(Sample.class);


    public static void main(String... args) {

        ConfigurableApplicationContext context =
                new ClassPathXmlApplicationContext("jmx-logger.xml");

        Scanner scanner = new Scanner(System.in);

        LOG.warn("Use jConsole to alter log level to DEBUG to see the next message!");
        scanner.nextLine();
        LOG.debug("Sample debug message!");

        context.close();
    }
}
