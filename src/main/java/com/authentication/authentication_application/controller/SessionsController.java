package com.authentication.authentication_application.controller;

import com.authentication.authentication_application.api.SessionsApi;
import com.authentication.authentication_application.exception.InvalidCredentialsException;
import com.authentication.authentication_application.model.CreateSessionRequest;
import com.authentication.authentication_application.model.Session;
import com.authentication.authentication_application.model.SessionResponse;
import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.service.SessionCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller implementation for user login/session creation endpoint.
 * Implements the SessionsApi interface generated from OpenAPI specification.
 * Delegates business logic to SessionCreationService.
 * Exception handling is centralized in GlobalExceptionHandler.
 */
@RestController
@RequiredArgsConstructor
public class SessionsController implements SessionsApi {

    private final SessionCreationService sessionCreationService;

    /**
     * Creates a new login session by authenticating user credentials.
     * Delegates to SessionCreationService for authentication logic.
     * Exceptions are handled by GlobalExceptionHandler for consistent error responses.
     *
     * @param createSessionRequest the request containing email and password
     * @return ResponseEntity with status 200 and SessionResponse containing session info
     * @throws InvalidCredentialsException if email not found or password is incorrect
     */
    @Override
    public ResponseEntity<SessionResponse> createSession(CreateSessionRequest createSessionRequest) {
        // Authenticate user via service
        User authenticatedUser = sessionCreationService.createSession(createSessionRequest);

        // Build session response
        Session session = new Session();
        session.setProfileId(authenticatedUser.getProfileId());
        session.setEmail(authenticatedUser.getEmail());
        session.setExpiryTime(authenticatedUser.getUpdatedAt());

        SessionResponse response = new SessionResponse();
        response.setData(session);
        response.setMessage("Login successful");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
