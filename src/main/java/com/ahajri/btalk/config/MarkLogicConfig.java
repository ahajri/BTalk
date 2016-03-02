package com.ahajri.btalk.config;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.ahajri.btalk.data.domain.DiscussionMember;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.query.QueryManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPDigestAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@PropertySource("classpath:src/main/resources/ml-config.properties")
public class MarkLogicConfig {

	private static final Logger LOGGER = Logger
			.getLogger(MarkLogicConfig.class);

	@Value("${marklogic.host}")
	private String host;

	@Value("${marklogic.port}")
	private int port;

	@Value("${marklogic.username}")
	private String username;

	@Value("${marklogic.password}")
	private String password;

	@Bean
	public DatabaseClient getDatabaseClient() {
		try {
			DatabaseClientFactory.getHandleRegistry().register(
					JAXBHandle.newFactory(DiscussionMember.class));
		} catch (JAXBException e) {
			LOGGER.error(e);
		}
		return DatabaseClientFactory.newClient(host, port, username, password,
				DatabaseClientFactory.Authentication.DIGEST);
	}

	@Bean
	public QueryManager getQueryManager() {
		return getDatabaseClient().newQueryManager();
	}

	@Bean
	public XMLDocumentManager getXMLDocumentManager() {
		return getDatabaseClient().newXMLDocumentManager();
	}

	@Bean
	public JSONDocumentManager getJSONDocumentManager() {
		return getDatabaseClient().newJSONDocumentManager();
	}

	@Bean
	public String getMarkLogicBaseURL() {
		return String.format("http://%s:%d", host, port);
	}

	@Bean
	public Client getJerseyClient() {
		Client client = Client.create(); // thread-safe
		client.addFilter(new LoggingFilter());
		client.addFilter(new HTTPDigestAuthFilter(username, password));
		return client;
	}

}
