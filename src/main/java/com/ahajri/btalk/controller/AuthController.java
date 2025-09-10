package com.example.ldapauth.controller;

import com.example.ldapauth.dto.LoginRequest;
import com.example.ldapauth.dto.LoginResponse;
import com.example.ldapauth.dto.UserInfo;
import com.example.ldapauth.dto.ErrorResponse;
import com.example.ldapauth.service.LdapAuthService;
import com.example.ldapauth.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private LdapAuthService ldapAuthService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Login endpoint
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());

        try {
            // Authenticate against LDAP
            boolean isAuthenticated = ldapAuthService.authenticate(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );

            if (isAuthenticated) {
                // Get user information from LDAP
                UserInfo userInfo = ldapAuthService.getUserInfo(loginRequest.getUsername());

                // Generate JWT token
                Map<String, Object> claims = new HashMap<>();
                claims.put("roles", userInfo.getRoles());
                claims.put("fullName", userInfo.getFullName());
                claims.put("email", userInfo.getEmail());

                String token = jwtUtil.generateToken(loginRequest.getUsername(), claims);

                // Create successful response
                LoginResponse response = new LoginResponse(
                    true, 
                    "Authentication successful", 
                    token, 
                    userInfo
                );

                logger.info("Login successful for user: {}", loginRequest.getUsername());
                return ResponseEntity.ok(response);

            } else {
                logger.warn("Login failed for user: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Invalid username or password"));
            }

        } catch (Exception e) {
            logger.error("Login error for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error during authentication"));
        }
    }

    /**
     * Validate token endpoint
     * POST /api/auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Missing or invalid authorization header"));
            }

            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);

            if (isValid) {
                String username = jwtUtil.extractUsername(token);
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                response.put("message", "Token is valid");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token is invalid or expired"));
            }

        } catch (Exception e) {
            logger.error("Token validation error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error validating token"));
        }
    }

    /**
     * Get current user info
     * GET /api/auth/me
     */
    @Operation(
        summary = "Get Current User",
        description = "Get current authenticated user information from LDAP",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information retrieved", 
                    content = @Content(schema = @Schema(implementation = UserInfo.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
        @Parameter(description = "Authorization header with Bearer token", required = true)
        @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Missing or invalid authorization header"));
            }

            String token = authHeader.substring(7);
            
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token is invalid or expired"));
            }

            String username = jwtUtil.extractUsername(token);
            UserInfo userInfo = ldapAuthService.getUserInfo(username);

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            logger.error("Get current user error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error getting user information"));
        }
    }

    /**
     * Refresh token endpoint
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Missing or invalid authorization header"));
            }

            String token = authHeader.substring(7);
            
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token is invalid or expired"));
            }

            String newToken = jwtUtil.refreshToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", newToken);
            response.put("message", "Token refreshed successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Token refresh error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error refreshing token"));
        }
    }

    /**
     * Logout endpoint
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            // In a real application, you might want to blacklist the token
            // For now, we'll just return a success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Logout successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Logout error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error during logout"));
        }
    }

    /**
     * Health check for LDAP connection
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try {
            boolean ldapConnected = ldapAuthService.testConnection();
            
            Map<String, Object> response = new HashMap<>();
            response.put("ldapConnected", ldapConnected);
            response.put("status", ldapConnected ? "UP" : "DOWN");
            response.put("timestamp", java.time.LocalDateTime.now());

            HttpStatus status = ldapConnected ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
            return ResponseEntity.status(status).body(response);

        } catch (Exception e) {
            logger.error("Health check error", e);
            Map<String, Object> response = new HashMap<>();
            response.put("ldapConnected", false);
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}
