package com.ahajri.btalk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool2.factory.PooledContextSource;
import org.springframework.ldap.pool2.validation.DefaultDirContextValidator;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class LdapConfig {

    @Value("${spring.ldap.urls}")
    private String ldapUrl;

    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Value("${spring.ldap.username}")
    private String ldapUsername;

    @Value("${spring.ldap.password}")
    private String ldapPassword;

    @Value("${app.ldap.user-search-base:ou=people}")
    private String userSearchBase;

    @Value("${app.ldap.user-search-filter:(uid={0})}")
    private String userSearchFilter;

    @Value("${app.ldap.group-search-base:ou=groups}")
    private String groupSearchBase;

    @Value("${app.ldap.group-search-filter:(member={0})}")
    private String groupSearchFilter;

    /**
     * LDAP Context Source Configuration
     */
    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setBase(ldapBase);
        contextSource.setUserDn(ldapUsername);
        contextSource.setPassword(ldapPassword);
        
        // Additional configuration
        contextSource.setReferral("follow");
        contextSource.setPooled(true);
        
        return contextSource;
    }

    /**
     * Pooled Context Source for better performance
     */
    @Bean
    public PooledContextSource pooledContextSource() {
        PooledContextSource pooledContextSource = new PooledContextSource();
        pooledContextSource.setContextSource(contextSource());
        
        // Pool configuration
        pooledContextSource.setMaxActive(8);
        pooledContextSource.setMaxIdle(8);
        pooledContextSource.setMinIdle(0);
        pooledContextSource.setMaxWait(-1);
        
        // Connection validation
        pooledContextSource.setTestOnBorrow(true);
        pooledContextSource.setTestWhileIdle(true);
        pooledContextSource.setDirContextValidator(new DefaultDirContextValidator());
        
        return pooledContextSource;
    }

    /**
     * LDAP Template for data access operations
     */
    @Bean
    public LdapTemplate ldapTemplate() {
        LdapTemplate ldapTemplate = new LdapTemplate(pooledContextSource());
        ldapTemplate.setIgnorePartialResultException(true);
        ldapTemplate.setIgnoreNameNotFoundException(true);
        ldapTemplate.setIgnoreSizeLimitExceededException(true);
        return ldapTemplate;
    }

    /**
     * Spring Security Context Source
     */
    @Bean
    public DefaultSpringSecurityContextSource springSecurityContextSource() {
        DefaultSpringSecurityContextSource contextSource = 
            new DefaultSpringSecurityContextSource(ldapUrl + "/" + ldapBase);
        contextSource.setUserDn(ldapUsername);
        contextSource.setPassword(ldapPassword);
        return contextSource;
    }

    /**
     * LDAP User Search Configuration
     */
    @Bean
    public FilterBasedLdapUserSearch userSearch() {
        return new FilterBasedLdapUserSearch(
            userSearchBase, 
            userSearchFilter, 
            springSecurityContextSource()
        );
    }

    /**
     * LDAP Bind Authenticator
     */
    @Bean
    public BindAuthenticator bindAuthenticator() {
        BindAuthenticator bindAuthenticator = new BindAuthenticator(springSecurityContextSource());
        bindAuthenticator.setUserSearch(userSearch());
        return bindAuthenticator;
    }

    /**
     * LDAP Authorities Populator (for roles/groups)
     */
    @Bean
    public DefaultLdapAuthoritiesPopulator authoritiesPopulator() {
        DefaultLdapAuthoritiesPopulator authoritiesPopulator = 
            new DefaultLdapAuthoritiesPopulator(springSecurityContextSource(), groupSearchBase);
        authoritiesPopulator.setGroupSearchFilter(groupSearchFilter);
        authoritiesPopulator.setGroupRoleAttribute("cn");
        authoritiesPopulator.setRolePrefix("ROLE_");
        authoritiesPopulator.setSearchSubtree(true);
        authoritiesPopulator.setConvertToUpperCase(true);
        return authoritiesPopulator;
    }

    /**
     * LDAP Authentication Provider
     */
    @Bean
    public LdapAuthenticationProvider ldapAuthenticationProvider() {
        LdapAuthenticationProvider provider = 
            new LdapAuthenticationProvider(bindAuthenticator(), authoritiesPopulator());
        return provider;
    }

    /**
     * Security Filter Chain Configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .authenticationProvider(ldapAuthenticationProvider());

        return http.build();
    }
}
