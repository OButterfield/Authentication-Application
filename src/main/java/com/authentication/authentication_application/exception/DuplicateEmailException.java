package com.authentication.authentication_application.exception;

/**
 * Exception thrown when a duplicate email is attempted during user creation.
 * Returns HTTP 400 Bad Request with a generic message to prevent email
 * enumeration attacks. Extends AuthenticationApplicationException for
 * consistent exception handling.
 */
public class DuplicateEmailException extends AuthenticationApplicationException {

	/**
	 * Constructs a DuplicateEmailException with a generic error message. The
	 * message does not reveal whether the email already exists to prevent security
	 * issues.
	 */
	public DuplicateEmailException() {
		super("INVALID_INPUT", "Email or password does not meet requirements");
	}
}
