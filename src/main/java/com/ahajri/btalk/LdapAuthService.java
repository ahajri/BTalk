package com.example.ldapauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.ldapauth.dto.UserInfo;

@Service
public class LdapAuthService {

    private static final Logger logger = LoggerFactory.getLogger(LdapAuthService.class);

    @Autowired
    private LdapTemplate ldapTemplate;

    @Value("${app.ldap.user-search-base}")
    private String userSearchBase;

    @Value("${app.ldap.user-search-filter}")
    private String userSearchFilter;

    @Value("${app.ldap.group-search-base}")
    private String groupSearchBase;

    @Value("${app.ldap.group-search-filter}")
    private String groupSearchFilter;

    /**
     * Authenticate user against LDAP
     */
    public boolean authenticate(String username, String password) {
        try {
            String userDn = findUserDn(username);
            if (userDn == null) {
                logger.warn("User not found: {}", username);
                return false;
            }

            // Attempt to bind with user credentials
            DirContext ctx = null;
            try {
                ctx = ldapTemplate.getContextSource().getContext(userDn, password);
                logger.info("Successfully authenticated user: {}", username);
                return true;
            } catch (Exception e) {
                logger.error("Authentication failed for user: {}", username, e);
                return false;
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (Exception e) {
                        logger.error("Error closing LDAP context", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error during authentication for user: {}", username, e);
            return false;
        }
    }

    /**
     * Find user DN by username
     */
    private String findUserDn(String username) {
        try {
            String filter = userSearchFilter.replace("{0}", username);
            List<String> dns = ldapTemplate.search(
                userSearchBase,
                filter,
                (AttributesMapper<String>) attrs -> {
                    try {
                        return attrs.get("dn") != null ? attrs.get("dn").get().toString() : null;
                    } catch (NamingException e) {
                        return null;
                    }
                }
            );

            if (!dns.isEmpty()) {
                // Build full DN
                String relativeDn = dns.get(0);
                return userSearchFilter.replace("({0})", "(" + username + ")").contains("uid") ?
                    "uid=" + username + "," + userSearchBase + "," + ldapTemplate.getContextSource().getBaseLdapPath() :
                    relativeDn;
            }

            // Alternative approach: search and get DN from search result
            return ldapTemplate.search(
                userSearchBase,
                filter,
                1,
                (AttributesMapper<String>) attrs -> {
                    // This will be called with the full DN context
                    return null; // We'll get the DN from the search context
                }
            ).stream().findFirst().orElse(null);

        } catch (Exception e) {
            logger.error("Error finding user DN for: {}", username, e);
            // Fallback: construct DN based on common patterns
            return constructUserDn(username);
        }
    }

    /**
     * Construct user DN based on common LDAP patterns
     */
    private String constructUserDn(String username) {
        String baseDn = ldapTemplate.getContextSource().getBaseLdapPath().toString();
        return String.format("uid=%s,%s,%s", username, userSearchBase, baseDn);
    }

    /**
     * Get user information from LDAP
     */
    public UserInfo getUserInfo(String username) {
        try {
            String filter = userSearchFilter.replace("{0}", username);
            
            List<UserInfo> users = ldapTemplate.search(
                userSearchBase,
                filter,
                new UserAttributesMapper()
            );

            if (!users.isEmpty()) {
                UserInfo user = users.get(0);
                // Get user groups/roles
                user.setRoles(getUserGroups(username));
                return user;
            }
        } catch (Exception e) {
            logger.error("Error getting user info for: {}", username, e);
        }
        
        // Return basic user info if LDAP lookup fails
        return new UserInfo(username, username, username + "@example.org", Collections.singletonList("ROLE_USER"));
    }

    /**
     * Get user groups from LDAP
     */
    private List<String> getUserGroups(String username) {
        try {
            String userDn = findUserDn(username);
            if (userDn == null) {
                return Collections.singletonList("ROLE_USER");
            }

            String filter = groupSearchFilter.replace("{0}", userDn);
            
            return ldapTemplate.search(
                groupSearchBase,
                filter,
                (AttributesMapper<String>) attrs -> {
                    try {
                        return attrs.get("cn") != null ? "ROLE_" + attrs.get("cn").get().toString().toUpperCase() : null;
                    } catch (NamingException e) {
                        return null;
                    }
                }
            );
        } catch (Exception e) {
            logger.error("Error getting user groups for: {}", username, e);
            return Collections.singletonList("ROLE_USER");
        }
    }

    /**
     * Mapper for user attributes
     */
    private static class UserAttributesMapper implements AttributesMapper<UserInfo> {
        @Override
        public UserInfo mapFromAttributes(Attributes attrs) throws NamingException {
            UserInfo user = new UserInfo();
            
            user.setUsername(getAttributeValue(attrs, "uid"));
            user.setFullName(getAttributeValue(attrs, "cn"));
            user.setEmail(getAttributeValue(attrs, "mail"));
            
            return user;
        }

        private String getAttributeValue(Attributes attrs, String attributeName) {
            try {
                return attrs.get(attributeName) != null ? 
                    attrs.get(attributeName).get().toString() : "";
            } catch (NamingException e) {
                return "";
            }
        }
    }

    /**
     * Test LDAP connection
     */
    public boolean testConnection() {
        try {
            ldapTemplate.search("", "(objectClass=*)", 1, (AttributesMapper<String>) attrs -> "test");
            return true;
        } catch (Exception e) {
            logger.error("LDAP connection test failed", e);
            return false;
        }
    }
}
