package com.authentication.authentication_application.exception;

import lombok.Getter;

/**
 * Base exception class for all authentication application exceptions.
 * Provides common error handling properties and behavior.
 * All custom exceptions in the application should extend this class.
 */
@Getter
public class AuthenticationApplicationException extends RuntimeException {

    /**
     * -- GETTER --
     *  Gets the error code for this exception.
     *
     * @return the error code identifier
     */
    private final String errorCode;

    /**
     * Constructs an AuthenticationApplicationException with error code and message.
     *
     * @param errorCode the error code identifier (e.g., "DUPLICATE_EMAIL", "INVALID_INPUT")
     * @param message the human-readable error message
     */
    public AuthenticationApplicationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs an AuthenticationApplicationException with error code, message, and cause.
     *
     * @param errorCode the error code identifier
     * @param message the human-readable error message
     * @param cause the underlying cause of the exception
     */
    public AuthenticationApplicationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}

