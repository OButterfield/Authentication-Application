package com.authentication.authentication_application.service;

import com.authentication.authentication_application.model.CreateSessionRequest;
import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.repository.UserRepository;
import com.authentication.authentication_application.util.HashUtil;
import com.authentication.authentication_application.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service for handling user session creation and authentication logic.
 * Encapsulates email lookup, password verification, and session creation.
 * Provides consistent error handling for invalid credentials.
 */
@Service
@RequiredArgsConstructor
public class SessionCreationService {

    private static final Logger logger = LoggerFactory.getLogger(SessionCreationService.class);
    private static final long SESSION_DURATION_MILLISECONDS = 60 * 60 * 1000; // 1 hour

    private final UserRepository userRepository;
    private final HashUtil hashUtil;

    /**
     * Creates a new session by authenticating user credentials.
     * Verifies email exists and password matches stored hash.
     * Updates the user's updatedAt timestamp to represent session expiry.
     *
     * @param createSessionRequest the request containing email and password
     * @return the authenticated User entity with updated session expiry time
     * @throws InvalidCredentialsException if email not found or password incorrect
     */
    public User createSession(CreateSessionRequest createSessionRequest) {
        // Find user by email
        User user = findUserByEmail(createSessionRequest.getEmail());

        // Verify password matches
        verifyPassword(createSessionRequest.getPassword(), user.getHashedPassword());

        // Update session expiry time (using updatedAt field to store expiry)
        long sessionExpiryTime = System.currentTimeMillis() + SESSION_DURATION_MILLISECONDS;
        user.setUpdatedAt(sessionExpiryTime);

        return user;
    }

    /**
     * Finds a user by email address.
     * Throws InvalidCredentialsException if user not found.
     *
     * @param email the user's email address
     * @return the User entity if found
     * @throws InvalidCredentialsException if no user with this email exists
     */
    private User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            logger.warn("Login attempt with non-existent email: {}", email);
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        return user.get();
    }

    /**
     * Verifies the provided password matches the stored hash.
     * Throws InvalidCredentialsException if password does not match.
     *
     * @param plainPassword the plain-text password from the request
     * @param storedHash the bcrypt-hashed password from database
     * @throws InvalidCredentialsException if password does not match
     */
    private void verifyPassword(String plainPassword, String storedHash) {
        boolean passwordMatches = hashUtil.matches(plainPassword, storedHash);

        if (!passwordMatches) {
            logger.warn("Login attempt with incorrect password");
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }
    }
}

