package com.authentication.authentication_application.controller;

import com.authentication.authentication_application.api.SessionsApi;
import com.authentication.authentication_application.exception.InvalidCredentialsException;
import com.authentication.authentication_application.model.CreateSessionRequest;
import com.authentication.authentication_application.model.Session;
import com.authentication.authentication_application.model.SessionResponse;
import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.service.UserAuthenticationService;
import com.authentication.authentication_application.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller implementation for user login/session creation endpoint.
 * Implements the SessionsApi interface generated from OpenAPI specification.
 * Orchestrates UserAuthenticationService and SessionService for authentication and session creation.
 * Exception handling is centralized in GlobalExceptionHandler.
 */
@RestController
@RequiredArgsConstructor
public class SessionsController implements SessionsApi {

    private final UserAuthenticationService userAuthenticationService;
    private final SessionService sessionService;

    /**
     * Creates a new login session by authenticating user credentials.
     * Authenticates user via UserAuthenticationService and creates session via SessionService.
     * Stores the session in the user's sessions array and returns session details including
     * sessionId, profileId, email, and expiryTime.
     * Exceptions are handled by GlobalExceptionHandler for consistent error responses.
     *
     * @param createSessionRequest the request containing email and password
     * @return ResponseEntity with status 200 and SessionResponse containing complete session info
     * @throws InvalidCredentialsException if email not found or password is incorrect
     */
    @Override
    public ResponseEntity<SessionResponse> createSession(CreateSessionRequest createSessionRequest) {
        User authenticatedUser = userAuthenticationService.authenticate(createSessionRequest);

        User userWithSession = sessionService.createSession(authenticatedUser);

        SessionResponse response = buildSessionResponse(userWithSession);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Builds a session response from the authenticated user with session data.
     * Extracts the most recently created session and formats response with all required fields.
     *
     * @param userWithSession the authenticated user with the new session
     * @return SessionResponse containing sessionId, profileId, email, and expiryTime
     */
    private SessionResponse buildSessionResponse(User userWithSession) {
        // Get the most recently created session (last element in sessions array)
        int lastSessionIndex = userWithSession.getSessions().size() - 1;
        String sessionId = userWithSession.getSessions().get(lastSessionIndex).getSessionId();
        Long expiryTime = userWithSession.getSessions().get(lastSessionIndex).getExpiryTime();

        // Build session data object
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setProfileId(userWithSession.getProfileId());
        session.setEmail(userWithSession.getEmail());
        session.setExpiryTime(expiryTime);

        // Build and return response
        SessionResponse response = new SessionResponse();
        response.setData(session);
        response.setMessage("Login successful");

        return response;
    }
}
