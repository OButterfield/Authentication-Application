package com.authentication.authentication_application.controller;

import com.authentication.authentication_application.api.ProfilesApi;
import com.authentication.authentication_application.exception.AuthenticationApplicationException;
import com.authentication.authentication_application.exception.DuplicateProfileException;
import com.authentication.authentication_application.model.CreateProfileRequest;
import com.authentication.authentication_application.model.Profile;
import com.authentication.authentication_application.model.ProfileResponse;
import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.service.ProfileCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller implementation for user profile creation endpoint. Implements the
 * ProfilesApi interface generated from OpenAPI specification. Delegates
 * business logic to ProfileCreationService. Exception handling is centralized
 * in GlobalExceptionHandler.
 */
@RestController
@RequiredArgsConstructor
public class ProfilesController implements ProfilesApi {

	private final ProfileCreationService profileCreationService;

	/**
	 * Creates a new user profile with the provided email and password. Delegates to
	 * ProfileCreationService for user creation logic. Exceptions are handled by
	 * GlobalExceptionHandler for consistent error responses.
	 *
	 * @param createProfileRequest
	 *            the request containing email and password
	 * @return ResponseEntity with status 201 and ProfileResponse containing the new
	 *         profile
	 * @throws DuplicateProfileException
	 *             if email or profileId already exists
	 * @throws AuthenticationApplicationException
	 *             for other authentication errors
	 */
	@Override
	public ResponseEntity<ProfileResponse> createProfile(CreateProfileRequest createProfileRequest) {
		User savedUser = profileCreationService.createUserProfile(createProfileRequest);

		Profile profile = new Profile();
		profile.setProfileId(savedUser.getProfileId());

		ProfileResponse response = new ProfileResponse();
		response.setData(profile);
		response.setMessage("User created successfully");

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
