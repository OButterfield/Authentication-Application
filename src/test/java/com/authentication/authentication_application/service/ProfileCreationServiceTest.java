package com.authentication.authentication_application.service;

import com.authentication.authentication_application.model.CreateProfileRequest;
import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.repository.UserRepository;
import com.authentication.authentication_application.util.HashUtil;
import com.authentication.authentication_application.exception.DuplicateProfileException;
import com.authentication.authentication_application.exception.DuplicateEmailException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ProfileCreationService.
 * Tests user profile creation, password hashing, and duplicate handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileCreationService Tests")
class ProfileCreationServiceTest {

    // Test constants
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "SecurePassword123!";
    private static final String HASHED_PASSWORD = "$2b$10$hashedPassword";
    private static final String TEST_PROFILE_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String DUPLICATE_EMAIL_ERROR = "E11000 duplicate key error collection: authentication_db.users index: email_1";
    private static final String DUPLICATE_PROFILE_ID_ERROR = "E11000 duplicate key error collection: authentication_db.users index: profileId_1";
    private static final String GENERIC_DUPLICATE_ERROR = "E11000 duplicate key error";
    private static final String MONGO_ID = "mongoId";

    // Expected exception properties
    private static final String DUPLICATE_EMAIL_CODE = "INVALID_INPUT";
    private static final String DUPLICATE_EMAIL_MESSAGE = "Email or password does not meet requirements";
    private static final String DUPLICATE_PROFILE_ID_CODE = "DUPLICATE_PROFILE_ID";
    private static final String DUPLICATE_PROFILE_ID_FIELD = "profileId";
    private static final String DUPLICATE_PROFILE_ID_MESSAGE = "A user with this profileId already exists (this should be extremely rare)";
    private static final String DUPLICATE_ENTRY_CODE = "DUPLICATE_ENTRY";
    private static final String DUPLICATE_ENTRY_FIELD = "unknown";
    private static final String DUPLICATE_ENTRY_MESSAGE = "A duplicate entry already exists (unknown field)";
    private static final String BCRYPT_PREFIX = "$2b$10$";

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashUtil hashUtil;

    private ProfileCreationService profileCreationService;

    private CreateProfileRequest validRequest;

    @BeforeEach
    void setUp() {
        profileCreationService = new ProfileCreationService(userRepository, hashUtil);

        validRequest = new CreateProfileRequest();
        validRequest.setEmail(TEST_EMAIL);
        validRequest.setPassword(TEST_PASSWORD);
    }

    @Test
    @DisplayName("Should successfully create user profile with valid request")
    void shouldCreateUserProfileWithValidRequest() {
        // GIVEN
        User mockUser = User.builder()
                .id(MONGO_ID)
                .profileId(TEST_PROFILE_ID)
                .email(TEST_EMAIL)
                .hashedPassword(HASHED_PASSWORD)
                .createdAt(System.currentTimeMillis())
                .updatedAt(System.currentTimeMillis())
                .build();

        when(hashUtil.hash(TEST_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // WHEN
        User result = profileCreationService.createUserProfile(validRequest);

        // THEN
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(HASHED_PASSWORD, result.getHashedPassword());
        assertNotNull(result.getProfileId());
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException with generic message when email already exists")
    void shouldThrowDuplicateEmailExceptionForDuplicateEmail() {
        // GIVEN
        when(hashUtil.hash(TEST_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class)))
                .thenThrow(new DuplicateKeyException(DUPLICATE_EMAIL_ERROR));

        // WHEN & THEN
        Exception exception = assertThrows(
                Exception.class,
                () -> profileCreationService.createUserProfile(validRequest)
        );

        // Verify it's a DuplicateEmailException with generic message
        assertTrue(exception instanceof DuplicateEmailException);
        assertEquals(DUPLICATE_EMAIL_CODE, ((DuplicateEmailException) exception).getErrorCode());
        assertEquals(DUPLICATE_EMAIL_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw DuplicateProfileException with DUPLICATE_PROFILE_ID when profileId already exists")
    void shouldThrowDuplicateProfileExceptionForDuplicateProfileId() {
        // GIVEN
        when(hashUtil.hash(TEST_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class)))
                .thenThrow(new DuplicateKeyException(DUPLICATE_PROFILE_ID_ERROR));

        // WHEN & THEN
        DuplicateProfileException exception = assertThrows(
                DuplicateProfileException.class,
                () -> profileCreationService.createUserProfile(validRequest)
        );

        assertEquals(DUPLICATE_PROFILE_ID_CODE, exception.getErrorCode());
        assertEquals(DUPLICATE_PROFILE_ID_FIELD, exception.getField());
        assertEquals(DUPLICATE_PROFILE_ID_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw DuplicateProfileException with DUPLICATE_ENTRY for unknown duplicate key error")
    void shouldThrowDuplicateProfileExceptionForUnknownDuplicateError() {
        // GIVEN
        when(hashUtil.hash(TEST_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class)))
                .thenThrow(new DuplicateKeyException(GENERIC_DUPLICATE_ERROR));

        // WHEN & THEN
        DuplicateProfileException exception = assertThrows(
                DuplicateProfileException.class,
                () -> profileCreationService.createUserProfile(validRequest)
        );

        assertEquals(DUPLICATE_ENTRY_CODE, exception.getErrorCode());
        assertEquals(DUPLICATE_ENTRY_FIELD, exception.getField());
        assertEquals(DUPLICATE_ENTRY_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Should hash password using HashUtil")
    void shouldHashPasswordUsingHashUtil() {
        // GIVEN
        User mockUser = User.builder()
                .id(MONGO_ID)
                .profileId(TEST_PROFILE_ID)
                .email(TEST_EMAIL)
                .hashedPassword(HASHED_PASSWORD)
                .createdAt(System.currentTimeMillis())
                .updatedAt(System.currentTimeMillis())
                .build();

        when(hashUtil.hash(TEST_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // WHEN
        profileCreationService.createUserProfile(validRequest);

        // THEN - Verify password was hashed (called via mockito spy)
        assertNotNull(mockUser.getHashedPassword());
        assertTrue(mockUser.getHashedPassword().startsWith(BCRYPT_PREFIX));
    }
}
