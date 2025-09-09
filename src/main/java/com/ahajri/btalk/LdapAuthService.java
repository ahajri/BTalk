package com.example.ldapauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapName;
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

    @Value("${spring.ldap.base}")
    private String baseDn;

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

            // Get the context source and attempt to bind with user credentials
            LdapContextSource contextSource = (LdapContextSource) ldapTemplate.getContextSource();
            DirContext ctx = null;
            try {
                ctx = contextSource.getContext(userDn, password);
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
            
            // Use search to find the user and get their DN
            List<String> results = ldapTemplate.search(
                userSearchBase,
                filter,
                (ctx, dn) -> dn.toString()  // This will give us the relative DN
            );

            if (!results.isEmpty()) {
                String relativeDn = results.get(0);
                // Build the full DN by combining relative DN with base DN
                return relativeDn + "," + baseDn;
            }

            // Fallback: construct DN based on common patterns
            return constructUserDn(username);

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
            
            List<String> groups = ldapTemplate.search(
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

            return groups.isEmpty() ? Collections.singletonList("ROLE_USER") : groups;

        } catch (Exception e) {
            logger.error("Error getting user groups for: {}", username, e);
            return Collections.singletonList("ROLE_USER");
        }
    }

    /**
     * Alternative method to get user groups by username
     */
    private List<String> getUserGroupsByUsername(String username) {
        try {
            // Search for groups where the user is a member by username
            String filter = String.format("(&(objectClass=groupOfNames)(member=uid=%s,%s,%s))", 
                username, userSearchBase, baseDn);
            
            List<String> groups = ldapTemplate.search(
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

            return groups.isEmpty() ? Collections.singletonList("ROLE_USER") : groups;

        } catch (Exception e) {
            logger.error("Error getting user groups by username for: {}", username, e);
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
            
            // Set default username if uid is not available
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                user.setUsername(getAttributeValue(attrs, "sAMAccountName")); // For Active Directory
            }
            
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
            // Simple search to test connection
            ldapTemplate.search("", "(objectClass=*)", 1, (AttributesMapper<String>) attrs -> "test");
            logger.info("LDAP connection test successful");
            return true;
        } catch (Exception e) {
            logger.error("LDAP connection test failed", e);
            return false;
        }
    }

    /**
     * Search for users (utility method)
     */
    public List<UserInfo> searchUsers(String searchTerm) {
        try {
            String filter = String.format("(|(uid=*%s*)(cn=*%s*)(mail=*%s*))", 
                searchTerm, searchTerm, searchTerm);
            
            return ldapTemplate.search(
                userSearchBase,
                filter,
                new UserAttributesMapper()
            );
        } catch (Exception e) {
            logger.error("Error searching users with term: {}", searchTerm, e);
            return new ArrayList<>();
        }
    }

    /**
     * Check if user exists in LDAP
     */
    public boolean userExists(String username) {
        try {
            String filter = userSearchFilter.replace("{0}", username);
            List<String> results = ldapTemplate.search(
                userSearchBase,
                filter,
                (ctx, dn) -> dn.toString()
            );
            return !results.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking if user exists: {}", username, e);
            return false;
        }
    }
}
