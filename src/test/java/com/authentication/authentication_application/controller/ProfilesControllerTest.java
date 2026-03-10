package com.authentication.authentication_application.controller;

import com.authentication.authentication_application.model.CreateProfileRequest;
import com.authentication.authentication_application.model.ProfileResponse;
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
 * Integration tests for ProfilesController. Tests the user profile creation
 * endpoint. Cleans up created records in the database after each test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProfilesController Tests")
class ProfilesControllerTest {

	private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	private CreateProfileRequest validRequest;
	private List<String> createdUserEmails;

	@BeforeEach
	void setUp() {
		createdUserEmails = new ArrayList<>();
		validRequest = new CreateProfileRequest();
		validRequest.setEmail("test@example.com");
		validRequest.setPassword("SecurePassword123!");
	}

	@AfterEach
	void tearDown() {
		// Clean up only the users created during this test
		for (String email : createdUserEmails) {
			userRepository.deleteByEmail(email);
		}
	}

	@Test
	@DisplayName("Should return 201 Created with valid UUID profileId, email, and success message")
	void shouldCreateProfileWithAllRequiredFieldsAndValidFormats() throws Exception {
		// GIVEN
		String requestBody = objectMapper.writeValueAsString(validRequest);
		createdUserEmails.add(validRequest.getEmail());
		// WHEN
		MvcResult result = mockMvc
				.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				// THEN - Verify status, format, and all required fields
				.andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.data").exists()).andExpect(jsonPath("$.data.profileId", notNullValue()))
				.andExpect(jsonPath("$.data.profileId").value(matchesPattern(UUID_PATTERN)))
				.andExpect(jsonPath("$.message").value("User created successfully")).andReturn();

		// Verify deserialization works correctly
		String content = result.getResponse().getContentAsString();
		ProfileResponse response = objectMapper.readValue(content, ProfileResponse.class);
		assert response.getData() != null;
		assert response.getData().getProfileId() != null;
	}

	@Test
	@DisplayName("Should preserve different emails and return unique profileIds for each request")
	void shouldPreserveEmailsAndReturnUniqueProfileIds() throws Exception {
		// Test multiple emails and verify each request returns unique profileId
		String[] testEmails = {"user1@example.com", "user2@example.com", "test@domain.org"};
		ProfileResponse[] responses = new ProfileResponse[testEmails.length];

		int index = 0;
		for (String testEmail : testEmails) {
			validRequest.setEmail(testEmail);
			String requestBody = objectMapper.writeValueAsString(validRequest);
			createdUserEmails.add(testEmail);
			// WHEN & THEN - Verify all response fields for each email
			MvcResult result = mockMvc
					.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(requestBody))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.data.profileId").value(matchesPattern(UUID_PATTERN)))
					.andExpect(jsonPath("$.message").value("User created successfully")).andReturn();

			responses[index] = objectMapper.readValue(result.getResponse().getContentAsString(), ProfileResponse.class);

			index++;
		}

		// Verify all profileIds are unique
		for (int i = 0; i < responses.length; i++) {
			for (int j = i + 1; j < responses.length; j++) {
				assert !responses[i].getData().getProfileId().equals(responses[j].getData().getProfileId());
			}
		}
	}

	@Test
	@DisplayName("Should reject profile creation with duplicate email")
	void shouldRejectProfileCreationWithDuplicateEmail() throws Exception {
		// GIVEN - Create first profile
		String requestBody = objectMapper.writeValueAsString(validRequest);
		createdUserEmails.add(validRequest.getEmail());
		mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isCreated());

		// WHEN & THEN - Try to create another profile with same email
		// Should return 400 Bad Request with generic message to prevent email
		// enumeration
		mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("INVALID_INPUT"))
				.andExpect(jsonPath("$.message").value("Email or password does not meet requirements"));
	}

	@Test
	@DisplayName("Should reject profile creation with invalid email format")
	void shouldRejectProfileCreationWithInvalidEmailFormat() throws Exception {
		// GIVEN
		validRequest.setEmail("invalid-email-without-at-symbol");
		String requestBody = objectMapper.writeValueAsString(validRequest);

		// WHEN & THEN
		mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Should reject profile creation with null email")
	void shouldRejectProfileCreationWithNullEmail() throws Exception {
		// GIVEN
		validRequest.setEmail(null);
		String requestBody = objectMapper.writeValueAsString(validRequest);

		// WHEN & THEN
		mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Should reject profile creation with null password")
	void shouldRejectProfileCreationWithNullPassword() throws Exception {
		// GIVEN
		validRequest.setPassword(null);
		String requestBody = objectMapper.writeValueAsString(validRequest);

		// WHEN & THEN
		mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Should reject profile creation with empty email")
	void shouldRejectProfileCreationWithEmptyEmail() throws Exception {
		// GIVEN
		validRequest.setEmail("");
		String requestBody = objectMapper.writeValueAsString(validRequest);

		// WHEN & THEN
		mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Should reject profile creation with empty password")
	void shouldRejectProfileCreationWithEmptyPassword() throws Exception {
		// GIVEN
		validRequest.setPassword("");
		String requestBody = objectMapper.writeValueAsString(validRequest);

		// WHEN & THEN
		mockMvc.perform(post("/profiles").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isBadRequest());
	}
}
