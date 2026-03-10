package com.authentication.authentication_application.service;

import com.authentication.authentication_application.model.CreateSessionRequest;
import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.repository.UserRepository;
import com.authentication.authentication_application.util.HashUtil;
import com.authentication.authentication_application.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service for authenticating user credentials. Responsible only for email
 * lookup and password verification. Follows Single Responsibility Principle by
 * focusing solely on authentication logic.
 */
@Service
@RequiredArgsConstructor
public class UserAuthenticationService {

	private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class);

	private final UserRepository userRepository;
	private final HashUtil hashUtil;

	/**
	 * Authenticates a user by verifying email and password credentials from the
	 * request. Responsible only for credential validation.
	 *
	 * @param createSessionRequest
	 *            the session request containing email and password
	 * @return the authenticated User entity
	 * @throws InvalidCredentialsException
	 *             if email not found or password incorrect
	 */
	public User authenticate(CreateSessionRequest createSessionRequest) {
		User user = findUserByEmail(createSessionRequest.getEmail());

		verifyPassword(createSessionRequest.getPassword(), user.getHashedPassword());

		return user;
	}

	/**
	 * Finds a user by email address. Throws InvalidCredentialsException if user not
	 * found.
	 *
	 * @param email
	 *            the user's email address
	 * @return the User entity if found
	 * @throws InvalidCredentialsException
	 *             if no user with this email exists
	 */
	private User findUserByEmail(String email) {
		Optional<User> user = userRepository.findByEmail(email);

		if (user.isEmpty()) {
			logger.warn("Login attempt with non-existent email: {}", email);
			throw new InvalidCredentialsException("Invalid email or password");
		}

		return user.get();
	}

	/**
	 * Verifies the provided password matches the stored hash. Throws
	 * InvalidCredentialsException if password does not match.
	 *
	 * @param plainPassword
	 *            the plain-text password from the request
	 * @param storedHash
	 *            the bcrypt-hashed password from database
	 * @throws InvalidCredentialsException
	 *             if password does not match
	 */
	private void verifyPassword(String plainPassword, String storedHash) {
		boolean passwordMatches = hashUtil.matches(plainPassword, storedHash);

		if (!passwordMatches) {
			logger.warn("Login attempt with incorrect password");
			throw new InvalidCredentialsException("Invalid email or password");
		}
	}
}
