package com.authentication.authentication_application.service;

import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.model.UserSession;
import com.authentication.authentication_application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Service for creating and managing user sessions. Responsible only for session
 * creation and persistence. Follows Single Responsibility Principle by focusing
 * solely on session logic.
 */
@Service
@RequiredArgsConstructor
public class SessionService {

	private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
	private static final long SESSION_DURATION_MILLISECONDS = 60 * 60 * 1000; // 1 hour

	private final UserRepository userRepository;

	/**
	 * Creates a new session for the authenticated user and persists it to the
	 * database. Generates a unique sessionId and calculates expiry time (1 hour
	 * from now).
	 *
	 * @param user
	 *            the authenticated user
	 * @return the authenticated User with the new session added to sessions array
	 */
	public User createSession(User user) {
		UserSession session = createUserSession();

		persistSessionToUser(user, session);

		logger.info("Session created successfully for email: {}", user.getEmail());
		return user;
	}

	/**
	 * Persists a new session to the user's sessions array and saves to database.
	 * Responsible only for storing the session.
	 *
	 * @param user
	 *            the user entity
	 * @param session
	 *            the session to persist
	 */
	private void persistSessionToUser(User user, UserSession session) {
		user.getSessions().add(session);

		userRepository.save(user);
	}

	/**
	 * Creates a new UserSession with unique UUID sessionId and 1 hour expiry.
	 * Responsible only for session object creation.
	 *
	 * @return new UserSession instance
	 */
	private UserSession createUserSession() {
		String sessionId = UUID.randomUUID().toString();
		long expiryTime = System.currentTimeMillis() + SESSION_DURATION_MILLISECONDS;

		return UserSession.builder().sessionId(sessionId).expiryTime(expiryTime).build();
	}
}
