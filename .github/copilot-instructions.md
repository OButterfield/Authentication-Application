# GitHub Copilot Instructions

## Project Overview

This is a Java Spring Boot backend API application designed to handle user authentication and account management. The API will run locally and provide RESTful endpoints for user creation and login operations.

### Core Objectives

1. **User Creation Endpoint**: Create new user accounts with:
   - Automatic unique UUID assignment as `profileId`
   - Persistent storage of account details (email, password, profileId) in MongoDB
   - Validation of input data
   - Appropriate HTTP responses following REST standards

2. **User Login Endpoint**: Authenticate existing users with:
   - Email and password validation
   - Success/failure responses following REST standards
   - Secure credential handling

3. **Data Persistence**: Use MongoDB as the local database for storing user account information

## Development Standards

### 1. Test-Driven Development (TDD)

- **Write tests first**: Before implementing any feature, write failing unit tests that define the expected behavior
- **Test Coverage**: Aim for at least 80% code coverage
- **Test Organization**: 
  - Place unit tests in `src/test/java/` following the same package structure as source code
  - Use descriptive test method names that clearly indicate what is being tested
  - Use JUnit 5 and Mockito for testing frameworks
- **Testing Best Practices**:
  - Each test should verify a single behavior
  - Tests should be independent and isolated
  - Use "Given When Then" pattern in test structure

### 2. Code Formatting and Style

- **Language**: Java 21 or higher
- **Code Style**:
  - Follow Google Java Style Guide conventions
  - Use 4 spaces for indentation (no tabs)
  - Maximum line length: 100 characters
  - Use meaningful variable and method names
  - PascalCase for class names
  - camelCase for method and variable names
  - UPPER_CASE for constants

- **Formatting Tools**:
  - Use Spotless Maven plugin for automatic formatting
  - All code must pass formatting checks before commit
  - IDE should be configured to apply formatting on save

### 3. Code Quality and Validation

- **Syntax Validation**: All code changes must:
  - Compile without errors
  - Pass all linting checks
  - Satisfy static analysis rules

- **Static Analysis Tools**:
  - Use Checkstyle for code style enforcement
  - Use SpotBugs for bug detection
  - Use SonarQube principles for code quality

- **Error Handling**:
  - All exceptions must be properly handled
  - Use appropriate HTTP status codes for different scenarios
  - Provide meaningful error messages in API responses

### 4. REST API Standards

- **HTTP Methods**: Use appropriate methods (GET, POST, PUT, DELETE, etc.)
- **Status Codes**:
  - `201 Created`: Successful resource creation
  - `200 OK`: Successful request
  - `400 Bad Request`: Invalid input
  - `401 Unauthorized`: Authentication failure
  - `409 Conflict`: Resource already exists
  - `500 Internal Server Error`: Server-side errors

- **Response Format**:
  - Use JSON for all API responses
  - Include meaningful success/error messages
  - Use consistent response structure across all endpoints

### 5. Security Standards

- **Password Handling**:
  - Never store plain-text passwords
  - Use bcrypt or similar for password hashing
  - Validate password strength requirements

- **Data Validation**:
  - Validate email format
  - Validate all input fields
  - Implement proper constraint validation using annotations

### 6. Documentation Requirements

- **Code Comments**: 
  - Add Javadoc comments to public methods and classes when necessary based on complexity
  - Explain complex business logic
  - Document any non-obvious design decisions

- **README**: Keep updated with:
  - Project description
  - Setup instructions
  - How to run tests
  - How to build and run the application

- **API Documentation**:
  - Use Springdoc OpenAPI (Swagger) for API documentation
  - Keep API documentation synchronized with code changes

### 7. Git Practices

- **Commit Messages**: 
  - Use clear, descriptive commit messages
  - Reference relevant issue numbers
  - Use imperative mood ("Add feature" not "Added feature")

- **Pull Requests**:
  - Include description of changes
  - Reference related issues
  - Ensure all tests pass before merging

## AI Agent Validation Checklist

When implementing features, the AI agent must validate:

- [ ] Code compiles without errors
- [ ] All tests pass (unit and integration tests)
- [ ] Code follows formatting standards (no style violations)
- [ ] No syntax errors
- [ ] Required test coverage is met (minimum 80%)
- [ ] API endpoints follow REST standards
- [ ] Error handling is implemented appropriately
- [ ] Javadoc comments are present for public APIs
- [ ] No deprecated code or deprecated dependencies
- [ ] Security best practices are followed
- [ ] Database schema changes are documented

## Project Structure

```
authentication-application/
├── .github/
│   └── copilot-instructions.md       (This file)
├── src/
│   ├── main/
│   │   ├── java/com/authentication/
│   │   │   └── authentication_application/
│   │   │       ├── AuthenticationApplication.java
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── model/
│   │   │       ├── dto/
│   │   │       ├── exception/
│   │   │       └── config/
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/com/authentication/
│           └── authentication_application/
│               ├── controller/
│               ├── service/
│               ├── repository/
│               └── integration/
├── pom.xml
├── README.md
├── CONTRIBUTING.md
└── .gitignore
```

## Dependencies

- **Spring Boot**: Web framework and dependency injection
- **MongoDB**: NoSQL database for user storage
- **Spring Data MongoDB**: Data access abstraction
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework for tests
- **Springdoc OpenAPI**: API documentation
- **Bcrypt**: Password hashing

## Getting Started

1. Clone the repository
2. Install Java 21+
3. Install MongoDB locally
4. Run `mvn clean install` to build
5. Run `mvn test` to execute tests
6. Run `mvn spring-boot:run` to start the application

## Additional Notes

- All code changes must be validated by the AI agent before being considered complete
- Consistency is key - follow these standards across the entire codebase
- When in doubt, refer to the Spring Boot best practices and Java conventions
- Keep security as a top priority throughout development