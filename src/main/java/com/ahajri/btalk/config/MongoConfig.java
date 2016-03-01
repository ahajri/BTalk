package com.ahajri.btalk.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * Mongo Config
 * 
 * @author ahajri
 *
 */
@Configuration
@EnableMongoRepositories
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "infocentreEntityManagerFactory", transactionManagerRef = "infocentreTransactionManager")
public class MongoConfig extends AbstractMongoConfiguration {

	public static final String INFOCENTRE_QUALIFIER = "infocentreQualifier";

	private static final int PORT = 27017;

	private static final String MAPPING_BASE_PACKAGE = "com.ahajri.msgsys";
	private static final String HOST = "localhost";
	private static final String DBNAME = "MSGSYSDB";

	@Bean
	@Override
	protected String getDatabaseName() {
		return DBNAME;
	}

	@Bean
	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient(new ServerAddress(HOST, PORT));
	}

	@Override
	protected String getMappingBasePackage() {
		return MAPPING_BASE_PACKAGE;
	}

	@Bean
	@Override
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), DBNAME);
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(new MappingJackson2HttpMessageConverter());
		messageConverters.add(new MarshallingHttpMessageConverter(
				new XStreamMarshaller()));
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}

	@Bean
	@Qualifier(INFOCENTRE_QUALIFIER)
	public DataSource dataSourceInfocentre() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource
				.setUrl("jdbc:mysql://vdrmerc95.auxia.lan:3306/infocentre_pprd");
		dataSource.setUsername("mercure");
		dataSource.setPassword("mercurepass");
		return dataSource;
	}

	@Bean
	@Qualifier(INFOCENTRE_QUALIFIER)
	public LocalContainerEntityManagerFactoryBean infocentreEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSourceInfocentre());
		em.setPackagesToScan(new String[] { "com.auxia.nsi.jpa.infocentre.hibernate.*" });
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());
		return em;
	}

	@Bean
	@Qualifier(INFOCENTRE_QUALIFIER)
	public PlatformTransactionManager infocentreTransactionManager(
			@Qualifier(INFOCENTRE_QUALIFIER) EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	private Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "validate");
		properties.setProperty("hibernate.dialect",
				"org.hibernate.dialect.MySQL5Dialect");
		properties.setProperty("hibernate.ejb.naming_strategy",
				"org.hibernate.cfg.ImprovedNamingStrategy");
		return properties;
	}

	@Bean
	@Qualifier("jdbcTemplateInfocentre")
	public JdbcTemplate jdbcTemplateInfocentre() {
		return new JdbcTemplate(dataSourceInfocentre());
	}

	@Bean(name = "namedParameterJdbcTemplateInfocentre")
	public NamedParameterJdbcTemplate infocentreNamedParameterJdbcTemplateInfocentre() {
		return new NamedParameterJdbcTemplate(dataSourceInfocentre());
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
}
