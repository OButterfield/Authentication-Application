package com.authentication.authentication_application.exception;

import lombok.Getter;

/**
 * Exception thrown when a duplicate user profile creation is attempted.
 * Indicates that either the email or profileId already exists in the database.
 * Extends AuthenticationApplicationException for consistent exception handling.
 */
@Getter
public class DuplicateProfileException extends AuthenticationApplicationException {

    /**
     * -- GETTER --
     *  Gets the field that caused the duplicate.
     *
     * @return the field name (email or profileId)
     */
    private final String field;

    /**
     * Constructs a DuplicateProfileException.
     *
     * @param errorCode the error code identifier (DUPLICATE_EMAIL or DUPLICATE_PROFILE_ID)
     * @param field the field that caused the duplicate (email or profileId)
     * @param message the human-readable error message
     */
    public DuplicateProfileException(String errorCode, String field, String message) {
        super(errorCode, message);
        this.field = field;
    }

}

