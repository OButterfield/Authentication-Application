package com.authentication.authentication_application.service;

import com.authentication.authentication_application.model.User;
import com.authentication.authentication_application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for SessionService. Tests session creation, persistence, and
 * expiry time calculation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService Tests")
class SessionServiceTest {

	// Test constants
	private static final String TEST_EMAIL = "test@example.com";
	private static final String TEST_PROFILE_ID = "550e8400-e29b-41d4-a716-446655440000";
	private static final String MONGO_ID = "mongoId";
	private static final long TEST_TIMESTAMP = 1709939509266L;
	private static final long SESSION_DURATION_MILLISECONDS = 60 * 60 * 1000; // 1 hour

	@Mock
	private UserRepository userRepository;

	private SessionService sessionService;

	@BeforeEach
	void setUp() {
		sessionService = new SessionService(userRepository);
	}

	@Test
	@DisplayName("Should create session and persist to database")
	void shouldCreateSession() {
		// GIVEN
		User user = User.builder().id(MONGO_ID).profileId(TEST_PROFILE_ID).email(TEST_EMAIL).createdAt(TEST_TIMESTAMP)
				.updatedAt(TEST_TIMESTAMP).sessions(new ArrayList<>()).build();

		// WHEN
		User result = sessionService.createSession(user);

		// THEN - Verify session was added and user was saved
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());

		User savedUser = userCaptor.getValue();
		assertEquals(1, savedUser.getSessions().size());
		assertNotNull(result.getSessions().get(0).getSessionId());
		assertNotNull(result.getSessions().get(0).getExpiryTime());
	}

	@Test
	@DisplayName("Should calculate correct session expiry time (1 hour from now)")
	void shouldCalculateCorrectSessionExpiryTime() {
		// GIVEN
		User user = User.builder().id(MONGO_ID).profileId(TEST_PROFILE_ID).email(TEST_EMAIL).createdAt(TEST_TIMESTAMP)
				.updatedAt(TEST_TIMESTAMP).sessions(new ArrayList<>()).build();

		long beforeCall = System.currentTimeMillis();

		// WHEN
		User result = sessionService.createSession(user);
		long afterCall = System.currentTimeMillis();

		// THEN - Verify expiry is approximately 1 hour from call time
		long expectedExpiryMin = beforeCall + SESSION_DURATION_MILLISECONDS;
		long expectedExpiryMax = afterCall + SESSION_DURATION_MILLISECONDS;

		assertTrue(result.getSessions().get(0).getExpiryTime() >= expectedExpiryMin - 100
				&& result.getSessions().get(0).getExpiryTime() <= expectedExpiryMax + 100);
	}

	@Test
	@DisplayName("Should generate unique sessionId for each session creation")
	void shouldGenerateUniqueSessionIdEachTime() {
		// GIVEN
		User user1 = User.builder().id(MONGO_ID).profileId(TEST_PROFILE_ID).email(TEST_EMAIL).createdAt(TEST_TIMESTAMP)
				.updatedAt(TEST_TIMESTAMP).sessions(new ArrayList<>()).build();

		User user2 = User.builder().id(MONGO_ID).profileId(TEST_PROFILE_ID).email(TEST_EMAIL).createdAt(TEST_TIMESTAMP)
				.updatedAt(TEST_TIMESTAMP).sessions(new ArrayList<>()).build();

		// WHEN
		User result1 = sessionService.createSession(user1);
		User result2 = sessionService.createSession(user2);

		// THEN
		assertNotEquals(result1.getSessions().get(0).getSessionId(), result2.getSessions().get(0).getSessionId());
	}
}
