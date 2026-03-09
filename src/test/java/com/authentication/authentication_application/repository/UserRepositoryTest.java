package com.authentication.authentication_application.repository;

import com.authentication.authentication_application.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserRepository.
 * Tests MongoDB persistence and query functionality.
 * Uses @DataMongoTest with embedded MongoDB via Flapdoodle for isolated testing.
 * Timestamps are stored as epoch milliseconds (Long) to avoid codec issues.
 * Test users are cleaned up after each test to preserve database state.
 */
@DataMongoTest
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String NONEXISTENT_EMAIL = "nonexistent@example.com";
    private static final String HASHED_PASSWORD = "$2b$10$hashedPasswordHere";

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String testProfileId;
    private List<String> createdUserIds;

    @BeforeEach
    void setUp() {
        createdUserIds = new ArrayList<>();

        testProfileId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        testUser = User.builder()
                .profileId(testProfileId)
                .email(TEST_EMAIL)
                .hashedPassword(HASHED_PASSWORD)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @AfterEach
    void tearDown() {
        // Delete only the users created during this test
        System.out.println(createdUserIds);
        for (String userId : createdUserIds) {
            userRepository.deleteById(userId);
        }
    }

    @Test
    @DisplayName("Should save a user to MongoDB")
    void shouldSaveUserToMongoDB() {
        // GIVEN - User object created in setUp

        // WHEN
        User savedUser = userRepository.save(testUser);
        createdUserIds.add(savedUser.getId());

        // THEN
        assertNotNull(savedUser.getId());
        assertEquals(testProfileId, savedUser.getProfileId());
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    @DisplayName("Should find user by email address")
    void shouldFindUserByEmail() {
        // GIVEN
        User savedUser = userRepository.save(testUser);
        createdUserIds.add(savedUser.getId());

        // WHEN
        Optional<User> foundUser = userRepository.findByEmail(TEST_EMAIL);

        // THEN
        assertTrue(foundUser.isPresent());
        assertEquals(testProfileId, foundUser.get().getProfileId());
        assertEquals(TEST_EMAIL, foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty Optional when email not found")
    void shouldReturnEmptyOptionalWhenEmailNotFound() {
        // GIVEN
        User savedUser = userRepository.save(testUser);
        createdUserIds.add(savedUser.getId());

        // WHEN
        Optional<User> foundUser = userRepository.findByEmail(NONEXISTENT_EMAIL);

        // THEN
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("Should find user by profileId")
    void shouldFindUserByProfileId() {
        // GIVEN
        User savedUser = userRepository.save(testUser);
        createdUserIds.add(savedUser.getId());

        // WHEN
        Optional<User> foundUser = userRepository.findByProfileId(testProfileId);

        // THEN
        assertTrue(foundUser.isPresent());
        assertEquals(TEST_EMAIL, foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        // GIVEN
        User savedUser = userRepository.save(testUser);
        createdUserIds.add(savedUser.getId());

        // WHEN & THEN
        assertTrue(userRepository.existsByEmail(TEST_EMAIL));
        assertFalse(userRepository.existsByEmail(NONEXISTENT_EMAIL));
    }

    @Test
    @DisplayName("Should check if user exists by profileId")
    void shouldCheckIfUserExistsByProfileId() {
        // GIVEN
        User savedUser = userRepository.save(testUser);
        createdUserIds.add(savedUser.getId());
        String differentProfileId = UUID.randomUUID().toString();

        // WHEN & THEN
        assertTrue(userRepository.existsByProfileId(testProfileId));
        assertFalse(userRepository.existsByProfileId(differentProfileId));
    }
}
