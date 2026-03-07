# OpenAPI Code Generation Guide

## Overview

Your project is now configured to automatically generate Spring Boot API interfaces from your OpenAPI specification. This guide explains how to use the OpenAPI tooling.

## Maven Commands

### 1. Generate Code from OpenAPI Spec

```bash
mvn clean compile openapi-generator:generate
```

**What it does:**
- Reads your `openapi.yml` file
- Generates Spring Boot interface files
- Creates DTOs (Data Transfer Objects) for request/response models
- Places generated code in `target/generated-sources/openapi/src/gen/java`

### 2. Generate and Compile

```bash
mvn clean compile
```

**What it does:**
- Generates code from OpenAPI spec
- Compiles all source code (including generated code)
- Places compiled classes in `target/classes`

### 3. Full Build with All Checks

```bash
mvn clean verify
```

**What it does:**
- Generates code
- Compiles everything
- Runs all tests
- Runs code formatting checks (Spotless)
- Runs static analysis (Checkstyle, SpotBugs)
- Generates reports

### 4. Format Your Code

```bash
# Check if code needs formatting
mvn spotless:check

# Automatically apply formatting
mvn spotless:apply
```

### 5. Run Static Analysis

```bash
# Check style violations
mvn checkstyle:check

# Find bugs
mvn spotbugs:check
```

### 6. View Generated Files

After running `mvn clean compile openapi-generator:generate`, the generated files will be at:
```
target/generated-sources/openapi/src/gen/java/com/authentication/authentication_application/
├── api/
│   ├── ProfilesApi.java          # Interface for /profiles endpoint
│   ├── SessionsApi.java          # Interface for /sessions endpoint
│   └── ApiUtil.java              # Utility class
└── model/
    ├── CreateProfileRequest.java
    ├── CreateSessionRequest.java
    ├── ProfileResponse.java
    ├── SessionResponse.java
    ├── Profile.java
    ├── Session.java
    └── ErrorResponse.java
```

## Access Swagger UI

Once your application is running, you can view and interact with your API:

### 1. Start the Application

```bash
mvn spring-boot:run
```

### 2. Open Swagger UI

Navigate to: **`http://localhost:8080/api/v1/swagger-ui.html`**

You will see:
- ✅ All endpoints with descriptions
- ✅ Request/response schemas
- ✅ HTTP status codes
- ✅ Example payloads
- ✅ "Try it out" button to test endpoints

### 3. View Raw OpenAPI Spec

The machine-readable JSON spec is available at:

**`http://localhost:8080/api/v1/v3/api-docs`**

This returns the complete OpenAPI specification in JSON format.

## Plugin Configuration Details

### OpenAPI Generator Plugin

Located in `pom.xml`, this plugin:
- **Reads from**: `src/main/resources/openapi.yml`
- **Generates to**: `target/generated-sources/openapi/src/gen/java`
- **Generator Type**: Spring (Spring Boot controllers)
- **Options**:
  - `delegatePattern: true` - Generates interfaces and delegate classes
  - `interfaceOnly: true` - Only generates interfaces (you implement them)
  - `useSpringBoot3: true` - Uses Spring Boot 3.x annotations

### Spotless Plugin

Automatically formats your code to Google Java Style Guide standards:
- 4 spaces for indentation
- Line length limits
- Import organization
- Annotation formatting

### Checkstyle Plugin

Validates code style against Google Java Style Guide using `google_checks.xml` configuration.

### SpotBugs Plugin

Detects common bugs and code quality issues with maximum effort and low threshold for detection.

## Workflow

### When You Update OpenAPI Spec

1. **Edit** `src/main/resources/openapi.yml`
2. **Run** `mvn clean compile openapi-generator:generate`
3. **Implement** the generated interfaces in your controllers
4. **Format** with `mvn spotless:apply`
5. **Validate** with `mvn clean verify`

### When You Start Coding

```bash
# Generate code from spec
mvn clean compile openapi-generator:generate

# Start development (tests and code)
mvn test

# Format code
mvn spotless:apply

# Full validation before commit
mvn clean verify
```

## Generated Files Usage

After generation, you'll have:

### Interfaces (to implement)
```java
// Generated - you implement this
public interface ProfilesApi {
    @PostMapping("/profiles")
    ResponseEntity<ProfileResponse> createProfile(
        @RequestBody CreateProfileRequest createProfileRequest
    );
}
```

### DTOs (use in your code)
```java
// Use these for type safety
CreateProfileRequest request = new CreateProfileRequest();
request.setEmail("user@example.com");
request.setPassword("SecurePassword123!");
```

### Your Implementation
```java
@RestController
public class ProfilesController implements ProfilesApi {
    @Override
    public ResponseEntity<ProfileResponse> createProfile(
        CreateProfileRequest request) {
        // Your implementation here
        return ResponseEntity.status(201).body(response);
    }
}
```

## Troubleshooting

### Generation Fails
```bash
# Clear cache and try again
mvn clean compile openapi-generator:generate -X
```

### Swagger UI Not Showing
1. Verify application is running: `mvn spring-boot:run`
2. Check port 8080 is accessible
3. Ensure `springdoc-openapi-starter-webmvc-ui` dependency is in pom.xml
4. Visit: `http://localhost:8080/api/v1/swagger-ui.html`

### Generated Code Has Errors
1. Validate OpenAPI spec YAML syntax
2. Ensure all required fields are defined
3. Check schema references are correct with `$ref`

## Tips

✅ **Always format before committing**: `mvn spotless:apply`
✅ **Validate everything**: `mvn clean verify`
✅ **Keep spec updated**: Update OpenAPI spec first, then generate code
✅ **Test via Swagger UI**: Use "Try it out" button to test endpoints
✅ **Monitor warnings**: Address any Checkstyle or SpotBugs warnings

## Next Steps

1. **Run generation**: `mvn clean compile openapi-generator:generate`
2. **Review generated code**: Check `target/generated-sources/openapi/src/gen/java`
3. **Create implementations**: Implement the generated interfaces
4. **Write tests**: Follow TDD pattern
5. **Start application**: `mvn spring-boot:run`
6. **Test via Swagger**: Visit `http://localhost:8080/api/v1/swagger-ui.html`


