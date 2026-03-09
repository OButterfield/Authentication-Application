package com.authentication.authentication_application.controller;

import com.authentication.authentication_application.model.CreateSessionRequest;
import com.authentication.authentication_application.model.SessionResponse;
import com.authentication.authentication_application.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SessionsController.
 * Tests the user login/session creation endpoint with proper authentication.
 * Cleans up created records in the database after each test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SessionsController Tests")
class SessionsControllerTest {

    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASSWORD = "SecurePassword123!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private CreateSessionRequest validRequest;
    private List<String> createdUserEmails;

    @BeforeEach
    void setUp() {
        createdUserEmails = new ArrayList<>();
        validRequest = new CreateSessionRequest();
        validRequest.setEmail(VALID_EMAIL);
        validRequest.setPassword(VALID_PASSWORD);
    }

    @AfterEach
    void tearDown() {
        // Clean up only the users created during this test
        for (String email : createdUserEmails) {
            userRepository.deleteByEmail(email);
        }
    }

    @Test
    @DisplayName("Should return 200 OK with valid sessionId, profileId, email, epoch milliseconds expiryTime, and success message")
    void shouldCreateSessionWithAllRequiredFieldsAndValidFormats() throws Exception {
        // GIVEN - First create a user profile
        CreateSessionRequest profileRequest = new CreateSessionRequest();
        profileRequest.setEmail(VALID_EMAIL);
        profileRequest.setPassword(VALID_PASSWORD);

        String profileRequestBody = objectMapper.writeValueAsString(profileRequest);
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(profileRequestBody))
                .andExpect(status().isCreated());

        createdUserEmails.add(VALID_EMAIL);

        // WHEN - Now attempt to login with the same credentials
        long beforeRequest = System.currentTimeMillis();
        String sessionRequestBody = objectMapper.writeValueAsString(validRequest);

        MvcResult result = mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sessionRequestBody))
                // THEN - Verify status, format, and all required fields
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.sessionId", notNullValue()))
                .andExpect(jsonPath("$.data.sessionId").value(matchesPattern(UUID_PATTERN)))
                .andExpect(jsonPath("$.data.profileId", notNullValue()))
                .andExpect(jsonPath("$.data.profileId").value(matchesPattern(UUID_PATTERN)))
                .andExpect(jsonPath("$.data.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.data.expiryTime").exists())
                .andExpect(jsonPath("$.data.expiryTime").isNumber())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andReturn();

        // Verify deserialization and expiryTime is in the future
        String content = result.getResponse().getContentAsString();
        SessionResponse response = objectMapper.readValue(content, SessionResponse.class);
        assert response.getData() != null;
        assert response.getData().getExpiryTime() > beforeRequest;
        assert response.getData().getProfileId() != null;
        assert response.getData().getEmail().equals(VALID_EMAIL);
    }

    @Test
    @DisplayName("Should reject session creation with invalid email format")
    void shouldRejectSessionCreationWithInvalidEmailFormat() throws Exception {
        // GIVEN
        validRequest.setEmail("invalid-email-without-at-symbol");
        String requestBody = objectMapper.writeValueAsString(validRequest);

        // WHEN & THEN
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject session creation with null email")
    void shouldRejectSessionCreationWithNullEmail() throws Exception {
        // GIVEN
        validRequest.setEmail(null);
        String requestBody = objectMapper.writeValueAsString(validRequest);

        // WHEN & THEN
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject session creation with null password")
    void shouldRejectSessionCreationWithNullPassword() throws Exception {
        // GIVEN
        validRequest.setPassword(null);
        String requestBody = objectMapper.writeValueAsString(validRequest);

        // WHEN & THEN
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject session creation with empty email")
    void shouldRejectSessionCreationWithEmptyEmail() throws Exception {
        // GIVEN
        validRequest.setEmail("");
        String requestBody = objectMapper.writeValueAsString(validRequest);

        // WHEN & THEN
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject session creation with empty password")
    void shouldRejectSessionCreationWithEmptyPassword() throws Exception {
        // GIVEN
        validRequest.setPassword("");
        String requestBody = objectMapper.writeValueAsString(validRequest);

        // WHEN & THEN
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject session creation with non-existent email")
    void shouldRejectSessionCreationWithNonExistentEmail() throws Exception {
        // GIVEN - Use email that doesn't exist
        validRequest.setEmail("nonexistent@example.com");
        String requestBody = objectMapper.writeValueAsString(validRequest);

        // WHEN & THEN
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Should reject session creation with incorrect password")
    void shouldRejectSessionCreationWithIncorrectPassword() throws Exception {
        // GIVEN - First create a user profile
        CreateSessionRequest profileRequest = new CreateSessionRequest();
        profileRequest.setEmail(VALID_EMAIL);
        profileRequest.setPassword(VALID_PASSWORD);

        String profileRequestBody = objectMapper.writeValueAsString(profileRequest);
        mockMvc.perform(post("/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(profileRequestBody))
                .andExpect(status().isCreated());

        createdUserEmails.add(VALID_EMAIL);

        // WHEN - Try to login with wrong password
        validRequest.setPassword("WrongPassword123!");
        String sessionRequestBody = objectMapper.writeValueAsString(validRequest);

        // THEN
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sessionRequestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
