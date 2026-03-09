package com.authentication.authentication_application.service;

import com.authentication.authentication_application.exception.AuthenticationApplicationException;
import com.authentication.authentication_application.model.CreateProfileRequest;
import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.repository.UserRepository;
import com.authentication.authentication_application.util.HashUtil;
import com.authentication.authentication_application.exception.DuplicateProfileException;
import com.authentication.authentication_application.exception.DuplicateEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Service for handling user profile creation logic.
 * Encapsulates user creation, password hashing, and MongoDB persistence.
 * Provides error handling for duplicate email scenarios.
 */
@Service
@RequiredArgsConstructor
public class ProfileCreationService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileCreationService.class);
    private final UserRepository userRepository;
    private final HashUtil hashUtil;

    /**
     * Creates a new user profile from the creation request.
     * Handles password hashing, unique ID generation, and MongoDB persistence.
     *
     * @param createProfileRequest the request containing email and password
     * @return the created User entity
     * @throws DuplicateProfileException if a user with this email already exists
     */
    public User createUserProfile(CreateProfileRequest createProfileRequest) {
        // Hash the password with bcrypt and pepper
        final String hashedPassword = hashUtil.hash(createProfileRequest.getPassword());

        // Build the user entity
        User user = buildUserFromRequest(createProfileRequest, hashedPassword);

        // Save to MongoDB and handle duplicate email
        return saveUserToDatabase(user);
    }

    /**
     * Builds a User entity from the creation request.
     * Generates unique profileId and timestamps.
     *
     * @param createProfileRequest the request containing email and password
     * @param hashedPassword the bcrypt-hashed password
     * @return constructed User entity ready to be saved
     */
    private User buildUserFromRequest(CreateProfileRequest createProfileRequest,
                                      String hashedPassword) {
        String profileId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        return User.builder()
                .profileId(profileId)
                .email(createProfileRequest.getEmail())
                .hashedPassword(hashedPassword)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Saves the user to MongoDB with error handling for duplicate email or profileId.
     *
     * @param user the User entity to save
     * @return the saved User entity with MongoDB-generated ID
     * @throws DuplicateEmailException if email already exists (returns generic message for security)
     * @throws DuplicateProfileException if profileId already exists
     * @throws AuthenticationApplicationException for other database errors
     */
    private User saveUserToDatabase(User user) {
        try {
            return userRepository.save(user);
        } catch (DuplicateKeyException e) {
            // Determine which field caused the duplicate
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("email")) {
                logger.warn("Duplicate email attempted: {}", user.getEmail());
                // Throw generic exception to prevent email enumeration attacks
                throw new DuplicateEmailException();
            } else if (errorMessage != null && errorMessage.contains("profileId")) {
                logger.warn("Duplicate profileId attempted: {}", user.getProfileId());
                throw new DuplicateProfileException(
                        "DUPLICATE_PROFILE_ID",
                        "profileId",
                        "A user with this profileId already exists (this should be extremely rare)"
                );
            } else {
                logger.warn("Unknown duplicate key error: {}", errorMessage);
                throw new DuplicateProfileException(
                        "DUPLICATE_ENTRY",
                        "unknown",
                        "A duplicate entry already exists (unknown field)"
                );
            }
        } catch (Exception e) {
            logger.error("Failed to save user profile to database for email: {}. Error: {}",
                    user.getEmail(), e.getMessage(), e);
            throw new AuthenticationApplicationException(
                    "DATABASE_ERROR",
                    "Failed to save user profile. Please try again later.",
                    e
            );
        }
    }
}

