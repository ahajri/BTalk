package com.example.ldapauth.dto;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;

// Login Request DTO
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    @JsonProperty("username")
    private String username;
    
    @NotBlank(message = "Password is required")
    @JsonProperty("password")
    private String password;
    
    public LoginRequest() {}
    
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}

// Login Response DTO
class LoginResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("token")
    private String token;
    
    @JsonProperty("user")
    private UserInfo user;
    
    public LoginResponse() {}
    
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public LoginResponse(boolean success, String message, String token, UserInfo user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
}

// User Info DTO
class UserInfo {
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("fullName")
    private String fullName;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("roles")
    private java.util.List<String> roles;
    
    public UserInfo() {}
    
    public UserInfo(String username, String fullName, String email, java.util.List<String> roles) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }
    
    // Getters and setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public java.util.List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(java.util.List<String> roles) {
        this.roles = roles;
    }
}

// Error Response DTO
public class ErrorResponse {
    
    @JsonProperty("success")
    private boolean success = false;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("timestamp")
    private java.time.LocalDateTime timestamp;
    
    public ErrorResponse() {
        this.timestamp = java.time.LocalDateTime.now();
    }
    
    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = java.time.LocalDateTime.now();
    }
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public java.time.LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(java.time.LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
