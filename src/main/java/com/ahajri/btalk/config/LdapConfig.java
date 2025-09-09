package com.example.ldapauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {

    @Value("${spring.ldap.urls}")
    private String ldapUrl;

    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Value("${spring.ldap.username}")
    private String ldapUsername;

    @Value("${spring.ldap.password}")
    private String ldapPassword;

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setBase(ldapBase);
        contextSource.setUserDn(ldapUsername);
        contextSource.setPassword(ldapPassword);
        
        // Additional settings
        contextSource.setReferral("follow");
        contextSource.setPooled(true);
        
        // Set connection timeout
        contextSource.setBaseEnvironmentProperties(java.util.Map.of(
            "com.sun.jndi.ldap.connect.timeout", "5000",
            "com.sun.jndi.ldap.read.timeout", "10000"
        ));

        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource());
        
        // Configure template settings
        ldapTemplate.setIgnorePartialResultException(true);
        ldapTemplate.setIgnoreNameNotFoundException(true);
        ldapTemplate.setIgnoreSizeLimitExceededException(true);
        
        return ldapTemplate;
    }
}
