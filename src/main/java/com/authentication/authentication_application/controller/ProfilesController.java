package com.authentication.authentication_application.controller;

import com.authentication.authentication_application.api.ProfilesApi;
import com.authentication.authentication_application.model.CreateProfileRequest;
import com.authentication.authentication_application.model.Profile;
import com.authentication.authentication_application.model.ProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller implementation for user profile creation endpoint.
 * Implements the ProfilesApi interface generated from OpenAPI specification.
 * This allows Spring to handle routing based on the interface's @RequestMapping annotations.
 */
@RestController
public class ProfilesController implements ProfilesApi {

    /**
     * Creates a new user profile with the provided email and password.
     * Generates a unique UUID profileId and stores the user in MongoDB.
     *
     * @param createProfileRequest the request containing email and password
     * @return ResponseEntity with status 201 and ProfileResponse containing the new profile
     */
    @Override
    public ResponseEntity<ProfileResponse> createProfile(CreateProfileRequest createProfileRequest) {
        // TODO: Implement password hashing with bcrypt
        // TODO: Implement MongoDB repository save
        // TODO: Add input validation

        // Generate a profile with a random UUID
        UUID profileId = UUID.randomUUID();
        String email = createProfileRequest.getEmail();

        Profile profile = new Profile();
        profile.setProfileId(profileId);
        profile.setEmail(email);

        ProfileResponse response = new ProfileResponse();
        response.setData(profile);
        response.setMessage("User created successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

