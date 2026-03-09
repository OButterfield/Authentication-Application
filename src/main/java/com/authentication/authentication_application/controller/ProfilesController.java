package com.authentication.authentication_application.controller;

import com.authentication.authentication_application.api.ProfilesApi;
import com.authentication.authentication_application.model.CreateProfileRequest;
import com.authentication.authentication_application.model.Profile;
import com.authentication.authentication_application.model.ProfileResponse;
import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.repository.UserRepository;
import com.authentication.authentication_application.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller implementation for user profile creation endpoint.
 * Implements the ProfilesApi interface generated from OpenAPI specification.
 * This allows Spring to handle routing based on the interface's @RequestMapping annotations.
 * Note we are using constructor injection of the HashUtil component for password hashing.
 */
@RestController
@RequiredArgsConstructor
public class ProfilesController implements ProfilesApi {

    private final HashUtil hashUtil;

    private final UserRepository userRepository;

    /**
     * Creates a new user profile with the provided email and password.
     * Generates a unique profileId (UUID as String) and stores the user in MongoDB.
     *
     * @param createProfileRequest the request containing email and password
     * @return ResponseEntity with status 201 and ProfileResponse containing the new profile
     */
    @Override
    public ResponseEntity<ProfileResponse> createProfile(CreateProfileRequest createProfileRequest) {
        // Hash the password with bcrypt and pepper
        final String hashedPassword = hashUtil.hash(createProfileRequest.getPassword());

        // Generate a unique profile ID
        String profileId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        // Create and save the user to MongoDB
        User user = User.builder()
                .profileId(profileId)
                .email(createProfileRequest.getEmail())
                .hashedPassword(hashedPassword)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedUser = userRepository.save(user);

        // Build the response
        Profile profile = new Profile();
        profile.setProfileId(savedUser.getProfileId());
        profile.setEmail(savedUser.getEmail());

        ProfileResponse response = new ProfileResponse();
        response.setData(profile);
        response.setMessage("User created successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
