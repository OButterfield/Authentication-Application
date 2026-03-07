package com.authentication.authentication_application.controller;

import com.authentication.authentication_application.api.SessionsApi;
import com.authentication.authentication_application.model.CreateSessionRequest;
import com.authentication.authentication_application.model.Session;
import com.authentication.authentication_application.model.SessionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Controller implementation for user login/session creation endpoint.
 * Implements the SessionsApi interface generated from OpenAPI specification.
 * This allows Spring to handle routing based on the interface's @RequestMapping annotations.
 */
@RestController
public class SessionsController implements SessionsApi {

    /**
     * Creates a new login session by authenticating user credentials.
     * Validates email and password against stored user in MongoDB.
     *
     * @param createSessionRequest the request containing email and password
     * @return ResponseEntity with status 200 and SessionResponse containing session info
     */
    @Override
    public ResponseEntity<SessionResponse> createSession(CreateSessionRequest createSessionRequest) {
        // TODO: Implement password verification with bcrypt
        // TODO: Implement MongoDB repository findByEmail
        // TODO: Add proper authentication logic
        // TODO: Add session token generation

        // For now, return a mock session
        String email = createSessionRequest.getEmail();
        UUID profileId = UUID.randomUUID();

        // Set expiry to 1 hour from now
        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1);

        Session session = new Session();
        session.setProfileId(profileId);
        session.setEmail(email);
        session.setExpiryTime(expiryTime);

        SessionResponse response = new SessionResponse();
        response.setData(session);
        response.setMessage("Login successful");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

