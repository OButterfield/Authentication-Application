package com.authentication.authentication_application.service;

import com.authentication.authentication_application.model.CreateSessionRequest;
import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.repository.UserRepository;
import com.authentication.authentication_application.util.HashUtil;
import com.authentication.authentication_application.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserAuthenticationService.
 * Tests user authentication via email lookup and password verification.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthenticationService Tests")
class UserAuthenticationServiceTest {

    // Test constants
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "SecurePassword123!";
    private static final String HASHED_PASSWORD = "$2b$10$hashedPassword";
    private static final String TEST_PROFILE_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String MONGO_ID = "mongoId";
    private static final long TEST_TIMESTAMP = 1709939509266L;

    // Expected exception properties
    private static final String INVALID_CREDENTIALS_CODE = "INVALID_CREDENTIALS";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashUtil hashUtil;

    private UserAuthenticationService userAuthenticationService;
    private CreateSessionRequest validRequest;

    @BeforeEach
    void setUp() {
        userAuthenticationService = new UserAuthenticationService(userRepository, hashUtil);
        validRequest = new CreateSessionRequest();
        validRequest.setEmail(TEST_EMAIL);
        validRequest.setPassword(TEST_PASSWORD);
    }

    @Test
    @DisplayName("Should successfully authenticate user with valid email and password")
    void shouldAuthenticateUserWithValidCredentials() {
        // GIVEN
        User storedUser = User.builder()
                .id(MONGO_ID)
                .profileId(TEST_PROFILE_ID)
                .email(TEST_EMAIL)
                .hashedPassword(HASHED_PASSWORD)
                .createdAt(TEST_TIMESTAMP)
                .updatedAt(TEST_TIMESTAMP)
                .sessions(new ArrayList<>())
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(storedUser));
        when(hashUtil.matches(TEST_PASSWORD, HASHED_PASSWORD)).thenReturn(true);

        // WHEN
        User result = userAuthenticationService.authenticate(validRequest);

        // THEN
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(TEST_PROFILE_ID, result.getProfileId());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when email not found")
    void shouldThrowInvalidCredentialsExceptionWhenEmailNotFound() {
        // GIVEN
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // WHEN & THEN
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userAuthenticationService.authenticate(validRequest)
        );

        assertEquals(INVALID_CREDENTIALS_CODE, exception.getErrorCode());
        assertEquals(INVALID_CREDENTIALS_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password is incorrect")
    void shouldThrowInvalidCredentialsExceptionWhenPasswordIncorrect() {
        // GIVEN
        User storedUser = User.builder()
                .id(MONGO_ID)
                .profileId(TEST_PROFILE_ID)
                .email(TEST_EMAIL)
                .hashedPassword(HASHED_PASSWORD)
                .createdAt(TEST_TIMESTAMP)
                .updatedAt(TEST_TIMESTAMP)
                .sessions(new ArrayList<>())
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(storedUser));
        when(hashUtil.matches(TEST_PASSWORD, HASHED_PASSWORD)).thenReturn(false);

        // WHEN & THEN
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userAuthenticationService.authenticate(validRequest)
        );

        assertEquals(INVALID_CREDENTIALS_CODE, exception.getErrorCode());
        assertEquals(INVALID_CREDENTIALS_MESSAGE, exception.getMessage());
    }
}


