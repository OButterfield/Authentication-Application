package com.authentication.authentication_application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User document entity stored in MongoDB.
 * Represents a user account with authentication credentials.
 * Timestamps are stored as milliseconds since Unix epoch (January 1, 1970 UTC).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    /**
     * MongoDB internal ID - auto-generated.
     */
    @Id
    private String id;

    /**
     * Unique profile identifier assigned at account creation.
     */
    @Indexed(unique = true)
    private String profileId;

    /**
     * User's email address - unique and indexed for fast lookups.
     */
    @Indexed(unique = true)
    private String email;

    /**
     * Bcrypt hashed password with pepper.
     */
    private String hashedPassword;

    /**
     * Timestamp of account creation (milliseconds since Unix epoch).
     */
    @Indexed(unique = false)
    private Long createdAt;

    /**
     * Timestamp of last account update (milliseconds since Unix epoch).
     */
    private Long updatedAt;
}

