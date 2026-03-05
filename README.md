# Authentication Application

A Java Spring Boot backend API for user authentication and account management with MongoDB persistence.

## Overview

This application provides RESTful API endpoints for:
- **User Registration**: Create new user accounts
- **User Login**: Authenticate users with email and password

## Features

- ✅ User account creation with automatic UUID generation
- ✅ Secure password storage using bcrypt hashing
- ✅ User login authentication with email and password
- ✅ RESTful API endpoints following HTTP standards
- ✅ MongoDB persistence for user data
- ✅ Comprehensive error handling
- ✅ Swagger/OpenAPI documentation
- ✅ Full unit and integration test coverage

## Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 17+ | Programming language |
| Spring Boot | 3.x | Web framework |
| Spring Data MongoDB | Latest | Data access layer |
| MongoDB | 4.0+ | Document database |
| JUnit 5 | Latest | Unit testing |
| Mockito | Latest | Mocking framework |
| Bcrypt | Latest | Password hashing |
| Springdoc OpenAPI | Latest | API documentation |

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Apache Maven 3.8.0 or higher
- MongoDB 4.0 or higher (running locally on default port 27017)

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/OButterfield/authentication-application.git
cd authentication-application
```

### 2. Install MongoDB

**macOS (using Homebrew):**
```bash
brew tap mongodb/brew
brew install mongodb-community
brew services start mongodb-community
```

**Linux (Ubuntu/Debian):**
```bash
curl -fsSL https://www.mongodb.org/static/pgp/server-4.0.asc | sudo apt-key add -
sudo apt-get install -y mongodb-org
sudo systemctl start mongod
```

**Windows:**
Download and run the MongoDB installer from [mongodb.com/try/download/community](https://www.mongodb.com/try/download/community)

### 3. Build the Application

```bash
mvn clean install
```

## Running the Application

### Start the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access Swagger UI

Once running, visit the interactive API documentation: `http://localhost:8080/swagger-ui.html`

## API Endpoints

This application provides 2 core endpoints for user authentication. Complete endpoint documentation with request/response examples is available in the interactive Swagger UI.

**Access Swagger Documentation:**
- Run the application: `mvn spring-boot:run`
- Navigate to: `http://localhost:8080/swagger-ui.html`

### Available Endpoints

The following endpoints are documented in the Swagger UI:

1. **POST /api/v1/profiles** - Create a new user profile with email and password
2. **POST /api/v1/sessions** - Create a new login session to authenticate a user with email and password

For detailed request/response formats, authentication requirements, and error handling, please refer to the Swagger UI.

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=UserServiceTest
```

### Run Tests with Coverage Report

```bash
mvn clean test jacoco:report
# Coverage report available at: target/site/jacoco/index.html
```

### Run Integration Tests

```bash
mvn verify
```

## Code Quality

### Check Code Formatting

```bash
mvn spotless:check
```

### Apply Code Formatting

```bash
mvn spotless:apply
```

### Run Static Analysis

```bash
mvn checkstyle:check
```

### Full Build with All Checks

```bash
mvn clean verify
```

## Project Structure

```
authentication-application/
├── .github/
│   └── copilot-instructions.md       # AI Agent guidelines
├── src/
│   ├── main/
│   │   ├── java/com/authentication/authentication_application/
│   │   │   ├── AuthenticationApplication.java      # Main Spring Boot app
│   │   │   ├── controller/                         # REST controllers
│   │   │   ├── service/                            # Business logic
│   │   │   ├── repository/                         # Data access
│   │   │   ├── model/                              # Domain entities
│   │   │   ├── dto/                                # Data transfer objects
│   │   │   ├── exception/                          # Custom exceptions
│   │   │   └── config/                             # Spring configuration
│   │   └── resources/
│   │       ├── application.yaml                    # Configuration
│   │       ├── static/                             # Static files
│   │       └── templates/                          # Templates
│   └── test/
│       └── java/com/authentication/authentication_application/
│           ├── controller/                         # Controller tests
│           ├── service/                            # Service tests
│           ├── repository/                         # Repository tests
│           └── integration/                        # Integration tests
├── .gitignore
├── pom.xml                                        # Maven configuration
├── README.md                                      # This file
├── CONTRIBUTING.md                                # Contribution guidelines
└── HELP.md                                        # Additional help

```

## Configuration

### application.yaml

Configure the application in `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: authentication-application
  data:
    mongodb:
      uri: mongodb://localhost:27017/authentication_db
      database: authentication_db
  
  jpa:
    show-sql: false
    
server:
  port: 8080
  servlet:
    context-path: /api/v1
```

## Development Workflow

1. **Create a feature branch**: `git checkout -b feature/new-feature`
2. **Write tests first** (TDD approach)
3. **Implement the feature** to make tests pass
4. **Validate your code**:
   - Run tests: `mvn test`
   - Check formatting: `mvn spotless:check`
   - Run analysis: `mvn checkstyle:check`
5. **Commit with clear messages**: `git commit -m "feat: add new feature"`
6. **Push and create a pull request**: `git push origin feature/new-feature`

For detailed contribution guidelines, see [CONTRIBUTING.md](CONTRIBUTING.md)

## Standards and Best Practices

This project adheres to:
- ✅ Test-Driven Development (TDD)
- ✅ REST API standards
- ✅ Google Java Style Guide
- ✅ Security best practices
- ✅ Clean code principles
- ✅ SOLID design principles

See [.github/copilot-instructions.md](.github/copilot-instructions.md) for detailed standards.

## Security Considerations

- Passwords are hashed using bcrypt with salt
- Never store plain-text passwords
- Input validation on all endpoints
- CORS configuration for frontend integration (when needed)
- SQL injection prevention through MongoDB parameterized queries
- Rate limiting recommendations for production

## Troubleshooting

### MongoDB Connection Issues

```bash
# Check if MongoDB is running
brew services list

# Restart MongoDB
brew services restart mongodb-community

# Check connection
mongo mongodb://localhost:27017
```

### Port Already in Use

If port 8080 is in use, configure a different port in `application.yaml`:
```yaml
server:
  port: 9090
```

### Build Failures

Clean and rebuild:
```bash
mvn clean install
```

## Performance Considerations

- MongoDB indexing on email field for faster lookups
- Implement caching for frequently accessed data - not suitable for login
- Consider pagination for list endpoints (future feature)
- Connection pooling configured for optimal performance - currently autoconfigured by MongoDB driver.

## Future Enhancements

- [ ] User profile endpoint (GET /api/v1/profiles/{emailAddress} or GET /api/v1/profiles/{profileId})
- [ ] User password change endpoint
- [ ] Email verification
- [ ] Password reset functionality
- [ ] JWT token-based authentication
- [ ] Two-factor authentication
- [ ] Rate limiting
- [ ] Additional API versions (/api/v2/, etc.)
- [ ] Docker containerization

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to contribute.

## Support

For issues and questions:
1. Check existing issues on GitHub
2. Review the [CONTRIBUTING.md](CONTRIBUTING.md) guide
3. Contact the development team

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [REST API Best Practices](https://restfulapi.net/)

---

**Last Updated:** March 5, 2026

**Project Status:** In Development