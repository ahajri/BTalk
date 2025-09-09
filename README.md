# Spring Boot 3 LDAP Authentication API

A complete REST API for LDAP authentication using Spring Boot 3, Java 17, and JWT tokens.

## Features

- LDAP authentication against Docker container
- JWT token-based authentication
- RESTful endpoints for login, validation, and user management
- Global exception handling
- CORS support
- Health check endpoints
- Comprehensive logging

## Prerequisites

- Java 17
- Maven 3.6+
- Docker (for LDAP server)
- LDAP server with imported LDIF dump

## Quick Start

### 1. LDAP Docker Setup

Make sure your LDAP Docker container is running:

```bash
# Example with osixia/openldap
docker run -p 389:389 -p 636:636 \
    --name ldap_container \
    --env LDAP_ORGANISATION="My Company" \
    --env LDAP_DOMAIN="example.org" \
    --env LDAP_ADMIN_PASSWORD="admin" \
    --detach osixia/openldap:1.5.0

# Import your LDIF dump
docker cp your_dump.ldif ldap_container:/tmp/
docker exec ldap_container ldapadd -x -D "cn=admin,dc=example,dc=org" -w admin -f /tmp/your_dump.ldif
```

### 2. Application Configuration

Update `application.properties` with your LDAP settings:

```properties
# LDAP Configuration
spring.ldap.urls=ldap://localhost:389
spring.ldap.base=dc=example,dc=org
spring.ldap.username=cn=admin,dc=example,dc=org
spring.ldap.password=admin

# User search configuration
app.ldap.user-search-base=ou=people
app.ldap.user-search-filter=(uid={0})
```

### 3. Build and Run

```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080/api`

## API Endpoints

### Authentication Endpoints

#### 1. Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "your_username",
    "password": "your_password"
}
```

**Success Response:**
```json
{
    "success": true,
    "message": "Authentication successful",
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
        "username": "your_username",
        "fullName": "Your Full Name",
        "email": "your@email.com",
        "roles": ["ROLE_USER"]
    }
}
```

#### 2. Validate Token
```http
POST /api/auth/validate
Authorization: Bearer your_jwt_token
```

#### 3. Get Current User
```http
GET /api/auth/me
Authorization: Bearer your_jwt_token
```

#### 4. Refresh Token
```http
POST /api/auth/refresh
Authorization: Bearer your_jwt_token
```

#### 5. Logout
```http
POST /api/auth/logout
Authorization: Bearer your_jwt_token
```

#### 6. Health Check
```http
GET /api/auth/health
```

### Test Endpoints

#### 1. Ping
```http
GET /api/test/ping
```

#### 2. LDAP Connection Test
```http
GET /api/test/ldap
```

#### 3. Protected Endpoint
```http
GET /api/test/protected
Authorization: Bearer your_jwt_token
```

#### 4. Application Info
```http
GET /api/test/info
```

## Usage Examples

### 1. Login with curl

```bash
curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
        "username": "john.doe",
        "password": "password123"
    }'
```

### 2. Access Protected Resource

```bash
# First, get the token from login response
TOKEN="eyJhbGciOiJIUzI1NiIs..."

# Then use it to access protected resources
curl -X GET http://localhost:8080/api/auth/me \
    -H "Authorization: Bearer $TOKEN"
```

### 3. Test LDAP Connection

```bash
curl -X GET http://localhost:8080/api/test/ldap
```

## Configuration

### LDAP Settings

Adjust these properties in `application.properties`:

```properties
# LDAP Server
spring.ldap.urls=ldap://localhost:389
spring.ldap.base=dc=example,dc=org
spring.ldap.username=cn=admin,dc=example,dc=org
spring.ldap.password=admin

# Search Configuration
app.ldap.user-search-base=ou=people
app.ldap.user-search-filter=(uid={0})
app.ldap.group-search-base=ou=groups
app.ldap.group-search-filter=(member={0})
```

### JWT Settings

```properties
app.jwt.secret=mySecretKey12345678901234567890123456789012345678901234567890
app.jwt.expiration=86400000
app.jwt.issuer=ldap-auth-api
```

## Project Structure

```
src/main/java/com/example/ldapauth/
├── LdapAuthApplication.java          # Main application class
├── controller/
│   ├── AuthController.java           # Authentication endpoints
│   └── TestController.java           # Test endpoints
├── service/
│   └── LdapAuthService.java          # LDAP operations
├── config/
│   ├── LdapConfig.java              # LDAP configuration
│   └── SecurityConfig.java          # Security configuration
├── dto/
│   └── (DTOs)                       # Data transfer objects
├── util/
│   └── JwtUtil.java                 # JWT utilities
└── exception/
    └── GlobalExceptionHandler.java  # Global exception handling
```

## Troubleshooting

### Common Issues

1. **LDAP Connection Failed**
   - Check Docker container is running: `docker ps`
   - Verify LDAP URL and port in configuration
   - Test connection: `curl http://localhost:8080/api/test/ldap`

2. **Authentication Failed**
   - Verify user exists in LDAP
   - Check search base and filter configuration
   - Review LDAP logs: `docker logs ldap_container`

3. **Token Issues**
   - Check JWT secret is at least 32 characters
   - Verify token expiration settings
   - Ensure Authorization header format: `Bearer <token>`

### Debug Mode

Enable debug logging:

```properties
logging.level.com.example.ldapauth=DEBUG
logging.level.org.springframework.ldap=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Testing

### Manual Testing

1. Start the application
2. Test basic connectivity: `GET /api/test/ping`
3. Test LDAP connection: `GET /api/test/ldap`
4. Try login with valid LDAP credentials
5. Use returned token for protected endpoints

### Example Test User

If your LDIF dump contains a user like:

```ldif
dn: uid=john.doe,ou=people,dc=example,dc=org
objectClass: inetOrgPerson
uid: john.doe
cn: John Doe
sn: Doe
givenName: John
mail: john.doe@example.org
userPassword: password123
```

Test login:
```json
{
    "username": "john.doe",
    "password": "password123"
}
```

## Security Notes

- Change JWT secret in production
- Use HTTPS in production
- Implement proper token blacklisting for logout
- Consider implementing rate limiting
- Use strong LDAP admin credentials
- Regularly rotate JWT secrets

## Docker Deployment

### Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/ldap-auth-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose

```yaml
version: '3.8'
services:
  ldap-auth-api:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - ldap
    environment:
      SPRING_LDAP_URLS: ldap://ldap:389

  ldap:
    image: osixia/openldap:1.5.0
    ports:
      - "389:389"
    environment:
      LDAP_ORGANISATION: "My Company"
      LDAP_DOMAIN: "example.org"
      LDAP_ADMIN_PASSWORD: "admin"
```
