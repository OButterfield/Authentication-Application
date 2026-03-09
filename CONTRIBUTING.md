# Contributing to Authentication Application

Thank you for contributing to the Authentication Application! This document provides guidelines and standards for all contributions.

## Code Review Checklist

Before submitting your code, ensure it meets all the following criteria:

### Functionality
- [ ] Feature works as expected
- [ ] Edge cases are handled
- [ ] Error scenarios are properly managed
- [ ] Code follows business logic requirements

### Testing
- [ ] All tests pass locally (`mvn test`)
- [ ] New features have corresponding test cases
- [ ] Test coverage is at least 80%
- [ ] Tests are independent and repeatable
- [ ] Mock objects are used appropriately for external dependencies

### Code Quality
- [ ] Code compiles without errors (`mvn clean compile`)
- [ ] No syntax errors present
- [ ] No warnings from the compiler
- [ ] Code follows formatting standards
- [ ] No code duplication (DRY principle)

### Documentation
- [ ] Javadoc comments for public methods and classes if particularly complex
- [ ] Complex logic has inline comments
- [ ] API endpoints are documented
- [ ] README is updated if necessary

### Security
- [ ] No hardcoded secrets or credentials
- [ ] Input validation is implemented
- [ ] Sensitive data is properly handled
- [ ] Password handling follows best practices

### Git Practices
- [ ] Commit messages are clear and descriptive
- [ ] Commits are logical and atomic
- [ ] Branch naming follows convention: `feature/feature-name` or `bugfix/bug-name`

## Feature Development Process

### Step 1: Plan
1. Create or identify the issue/requirement
2. Break down into smaller tasks
3. Plan the implementation approach
4. Identify test cases needed

### Step 2: Test First (TDD)
1. Write failing unit tests based on requirements
2. Ensure tests are descriptive and comprehensive
3. Tests should cover:
   - Happy path scenarios
   - Error conditions
   - Edge cases
   - Invalid inputs

### Step 3: Implementation
1. Write the minimum code to make tests pass
2. Follow coding standards and best practices
3. Add Javadoc comments
4. Keep methods small and focused (single responsibility)

### Step 4: Validation
1. Run full test suite: `mvn clean test`
2. Verify code formatting: `mvn spotless:check`
3. Check for compilation errors: `mvn clean compile`
4. Run static analysis: `mvn checkstyle:check`

### Step 5: Refactoring
1. Improve code quality without changing functionality
2. Ensure all tests still pass
3. Remove any code duplication

### Step 6: Documentation
1. Update API documentation
2. Add/update Javadoc comments
3. Update README if needed
4. Document any configuration changes

## Branch Naming Convention

- Feature branches: `feature/descriptive-name`
- Bug fixes: `bugfix/descriptive-name`
- Documentation: `docs/descriptive-name`
- Hotfixes: `hotfix/descriptive-name`

Example: `feature/user-login-endpoint`, `bugfix/password-hashing-issue`

## Commit Message Format

```
<type>: <subject>

<body>

<footer>
```

### Types
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Build process, dependencies, etc.

### Example
```
feat: Implement user creation endpoint

- Create UserController with POST /users endpoint
- Generate UUID (as string) profileId for new users
- Store user details in MongoDB
- Add input validation for email and password

Closes #12
```

## Testing Guidelines

### Unit Tests
- Test single units of code in isolation
- Mock external dependencies
- Use descriptive test names
- Follow Given When Then pattern

### Integration Tests
- Test interaction between components
- Use test databases (embedded MongoDB)
- Test actual REST endpoints
- Verify database persistence

### Example Test Structure
```java
@Test
void shouldCreateUserWithValidInput() {
    // GIVEN
    CreateUserRequest request = new CreateUserRequest("test@example.com", "password123");
    
    // WHEN
    UserResponse response = userService.createUser(request);
    
    // THEN
    assertNotNull(response.getProfileId());
    assertEquals("test@example.com", response.getEmail());
}
```

## Code Style Guide

### Naming Conventions
- Classes: `PascalCase` (e.g., `UserService`, `UserController`)
- Methods/Variables: `camelCase` (e.g., `createUser`, `profileId`)
- Constants: `UPPER_CASE` (e.g., `DEFAULT_PASSWORD_LENGTH`)
- Packages: lowercase (e.g., `com.authentication.controller`)

### Indentation and Formatting
- Use 4 spaces for indentation
- Maximum line length: 100 characters
- One blank line between methods
- Import statements should be organized

### Method Guidelines

Abide by SOLID principles:

- Keep methods short and focused (ideal: under 20 lines)
- One responsibility per method
- Descriptive method names that indicate purpose
- Use meaningful parameter names

## Javadoc Requirements

Public classes and methods must have Javadoc comments if they are particularly complex or if their purpose is not immediately clear from the name or they are particularly complex. Javadoc should include:

```java
/**
 * Creates a new user account with the provided credentials.
 *
 * @param request the user creation request containing email and password
 * @return the created user response with profileId
 * @throws InvalidEmailException if the email format is invalid
 * @throws DuplicateUserException if a user with this email already exists
 */
public UserResponse createUser(CreateUserRequest request) {
    // implementation
}
```

## REST API Standards

### HTTP Methods
- `GET`: Retrieve resource
- `POST`: Create new resource
- `PUT`: Update existing resource
- `DELETE`: Delete resource

### Status Codes
- `200 OK`: Successful GET/PUT request
- `201 Created`: Successful POST request
- `204 No Content`: Successful DELETE request
- `400 Bad Request`: Invalid input
- `401 Unauthorized`: Authentication required
- `409 Conflict`: Resource already exists
- `500 Internal Server Error`: Server error

### Response Format

Refer to Swagger documentation for consistent response structures.

## Running Tests and Validation Locally

```bash
# Clean build
mvn clean compile

# Run all tests
mvn test

# Run specific test
mvn test -Dtest=UserServiceTest

# Check code formatting
mvn spotless:check

# Apply code formatting
mvn spotless:apply

# Run static analysis
mvn checkstyle:check

# Full build with all checks
mvn clean verify
```

## Before Submitting

1. Run `mvn clean verify` - ensures all checks pass
2. Review your own changes - catch obvious issues
3. Ensure all tests pass
4. Update documentation
5. Write a clear commit message
6. Push your branch and create a pull request