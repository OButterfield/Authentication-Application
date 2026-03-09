package com.authentication.authentication_application.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global exception handler for the authentication application. Handles all
 * authentication-related exceptions and converts them to appropriate HTTP
 * responses. Uses Spring's @RestControllerAdvice to provide centralized
 * exception handling across all controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * Handles DuplicateEmailException. Returns HTTP 400 Bad Request with a generic
	 * message to prevent email enumeration attacks. Does not reveal whether the
	 * email already exists in the system.
	 *
	 * @param exception
	 *            the DuplicateEmailException
	 * @return ResponseEntity with 400 status and generic error message
	 */
	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException exception) {
		logger.warn("Duplicate email attempt detected - returning generic response for security");

		ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode(), exception.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles MethodArgumentNotValidException. Returns HTTP 400 Bad Request when
	 * request validation fails (e.g., null/empty fields). This is thrown by Spring
	 * when @Valid annotations detect validation errors.
	 *
	 * @param exception
	 *            the MethodArgumentNotValidException
	 * @return ResponseEntity with 400 status and error details
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException exception) {
		logger.warn("Validation error: {}", exception.getMessage());

		// Get the first validation error message
		String errorMessage = exception.getBindingResult().getFieldErrors().stream().findFirst()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).orElse("Invalid request input");

		ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR", errorMessage);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles DuplicateProfileException. Returns HTTP 409 Conflict when a duplicate
	 * email or profileId is detected.
	 *
	 * @param exception
	 *            the DuplicateProfileException
	 * @return ResponseEntity with 409 status and error details
	 */
	@ExceptionHandler(DuplicateProfileException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateProfileException(DuplicateProfileException exception) {
		logger.warn("Duplicate profile error: {} on field: {}", exception.getErrorCode(), exception.getField());

		ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode(), exception.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}

	/**
	 * Handles InvalidCredentialsException. Returns HTTP 401 Unauthorized when user
	 * authentication fails.
	 *
	 * @param exception
	 *            the InvalidCredentialsException
	 * @return ResponseEntity with 401 status and error details
	 */
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException exception) {
		logger.warn("Invalid credentials error: {}", exception.getErrorCode());

		ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode(), exception.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handles generic AuthenticationApplicationException. Returns HTTP 500 Bad
	 * Request for validation/input errors. This is a catch-all for any
	 * AuthenticationApplicationException that doesn't have a specific handler
	 * (e.g., invalid email format, invalid password format).
	 *
	 * @param exception
	 *            the AuthenticationApplicationException
	 * @return ResponseEntity with 500 status and error details
	 */
	@ExceptionHandler(AuthenticationApplicationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationApplicationException(
			AuthenticationApplicationException exception) {
		logger.warn("Authentication application error: {}", exception.getErrorCode());

		ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode(), exception.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handles all uncaught exceptions. Returns HTTP 500 Internal Server Error for
	 * unexpected errors. This is the final fallback for any exception that wasn't
	 * caught by more specific handlers.
	 *
	 * @param exception
	 *            the unexpected exception
	 * @return ResponseEntity with 500 status and error details
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralException(Exception exception) {
		logger.error("Unexpected error occurred", exception);

		ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Standard error response structure for all exceptions.
	 */
	@Setter
	@Getter
	public static class ErrorResponse {
		private String error;
		private String message;

		public ErrorResponse(String error, String message) {
			this.error = error;
			this.message = message;
		}

	}
}
