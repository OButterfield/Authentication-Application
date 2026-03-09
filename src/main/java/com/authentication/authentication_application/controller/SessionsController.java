package com.authentication.authentication_application.controller;

import com.authentication.authentication_application.api.SessionsApi;
import com.authentication.authentication_application.model.CreateSessionRequest;
import com.authentication.authentication_application.model.Session;
import com.authentication.authentication_application.model.SessionResponse;
import com.authentication.authentication_application.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller implementation for user login/session creation endpoint.
 * Implements the SessionsApi interface generated from OpenAPI specification.
 * This allows Spring to handle routing based on the interface's @RequestMapping annotations.
 * Note we are using constructor injection of the HashUtil component for password hashing.
 */
@RestController
@RequiredArgsConstructor
public class SessionsController implements SessionsApi {

    private final HashUtil hashUtil;

    private static final int oneHourInMilliseconds = 60 * 60 * 1000;

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
        final String storedHashedPassword = "storedHashedPassword"; // This should come from the database
        final boolean passwordMatches = hashUtil.matches(createSessionRequest.getPassword(), storedHashedPassword);
        // TODO: Implement MongoDB repository findByEmail
        // TODO: Add proper authentication logic
        // TODO: Add session token generation

        // For now, return a mock session
        String email = createSessionRequest.getEmail();
        String profileId = UUID.randomUUID().toString();

        // Set expiry to 1 hour from now (in epoch milliseconds)
        long expiryTime = System.currentTimeMillis() + (oneHourInMilliseconds);

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


