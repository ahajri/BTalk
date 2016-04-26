package com.ahajri.btalk.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 
 * @author
 *         <p>
 *         ahajri
 *         </p>
 *
 */
@Configuration
@ComponentScan
@PropertySource({ "classpath:jms.properties", "classpath:jndi.properties" })
public class JmsConfig {

	private static final Logger LOGGER = Logger.getLogger(JmsConfig.class);

	@Value("${java.naming.provider.url}")
	private String brokerURL;

	@Bean
	public ActiveMQConnectionFactory jmsConnectionFactory() {
		LOGGER.debug("Broker URL: " + brokerURL);
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
		return factory;
	}
}
