package com.authentication.authentication_application.controller;

import com.authentication.authentication_application.model.CreateSessionRequest;
import com.authentication.authentication_application.model.SessionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SessionsController.
 * Tests the user login/session creation endpoint.
 * Timestamps are stored as epoch milliseconds (Long).
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SessionsController Tests")
class SessionsControllerTest {

    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateSessionRequest createSessionRequest;

    @BeforeEach
    void setUp() {
        createSessionRequest = new CreateSessionRequest();
        createSessionRequest.setEmail("test@example.com");
        createSessionRequest.setPassword("SecurePassword123!");
    }

    @Test
    @DisplayName("Should return 200 OK with valid profileId, email, epoch milliseconds expiryTime, and success message")
    void shouldCreateSessionWithAllRequiredFieldsAndValidFormats() throws Exception {
        // GIVEN
        String requestBody = objectMapper.writeValueAsString(createSessionRequest);
        long beforeRequest = System.currentTimeMillis();

        // WHEN
        MvcResult result = mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                // THEN - Verify status, format, and all required fields
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.profileId", notNullValue()))
                .andExpect(jsonPath("$.data.profileId").value(matchesPattern(UUID_PATTERN)))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.expiryTime").exists())
                .andExpect(jsonPath("$.data.expiryTime").isNumber())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andReturn();

        // Verify deserialization and business logic
        String content = result.getResponse().getContentAsString();
        SessionResponse response = objectMapper.readValue(content, SessionResponse.class);
        assert response.getData() != null;
        assert response.getData().getProfileId() != null;
        assert response.getData().getEmail().equals("test@example.com");
        assert response.getData().getExpiryTime() != null;
        assert response.getData().getExpiryTime() > beforeRequest;
    }

    @Test
    @DisplayName("Should preserve different emails and return unique profileIds with future expiryTime for each session")
    void shouldPreserveEmailsAndReturnUniqueProfileIdsWithFutureExpiry() throws Exception {
        // Test multiple emails and verify each session returns unique profileId with future expiry
        String[] testEmails = {"user1@example.com", "user2@example.com", "test@domain.org"};
        SessionResponse[] responses = new SessionResponse[testEmails.length];
        long beforeRequests = System.currentTimeMillis();

        int index = 0;
        for (String testEmail : testEmails) {
            createSessionRequest.setEmail(testEmail);
            String requestBody = objectMapper.writeValueAsString(createSessionRequest);

            // WHEN & THEN - Verify all response fields for each session
            MvcResult result = mockMvc.perform(post("/sessions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.email").value(testEmail))
                    .andExpect(jsonPath("$.data.profileId").value(
                            matchesPattern(UUID_PATTERN)))
                    .andExpect(jsonPath("$.data.expiryTime").isNumber())
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andReturn();

            responses[index] = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    SessionResponse.class);

            // Verify expiryTime is in the future
            assert responses[index].getData().getExpiryTime() > beforeRequests;
            index++;
        }

        // Verify all profileIds are unique
        for (int i = 0; i < responses.length; i++) {
            for (int j = i + 1; j < responses.length; j++) {
                assert !responses[i].getData().getProfileId()
                        .equals(responses[j].getData().getProfileId());
            }
        }
    }

    @Test
    @DisplayName("Should reject session creation with invalid email format")
    void shouldRejectSessionCreationWithInvalidEmailFormat() throws Exception {
        // GIVEN
        createSessionRequest.setEmail("invalid-email-without-at-symbol");
        String requestBody = objectMapper.writeValueAsString(createSessionRequest);

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
        createSessionRequest.setEmail(null);
        String requestBody = objectMapper.writeValueAsString(createSessionRequest);

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
        createSessionRequest.setPassword(null);
        String requestBody = objectMapper.writeValueAsString(createSessionRequest);

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
        createSessionRequest.setEmail("");
        String requestBody = objectMapper.writeValueAsString(createSessionRequest);

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
        createSessionRequest.setPassword("");
        String requestBody = objectMapper.writeValueAsString(createSessionRequest);

        // WHEN & THEN
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
