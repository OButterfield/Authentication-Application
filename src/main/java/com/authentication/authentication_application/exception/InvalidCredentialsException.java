package com.authentication.authentication_application.exception;

/**
 * Exception thrown when user authentication fails due to invalid credentials.
 * Indicates that the email or password provided is incorrect.
 * Should return HTTP 401 Unauthorized.
 */
public class InvalidCredentialsException extends AuthenticationApplicationException {

    /**
     * Constructs an InvalidCredentialsException.
     *
     * @param message the human-readable error message
     */
    public InvalidCredentialsException(String message) {
        super("INVALID_CREDENTIALS", message);
    }
}

