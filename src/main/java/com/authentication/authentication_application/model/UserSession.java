package com.authentication.authentication_application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user session stored within the User document. Each session has a
 * unique ID and an expiry time. Timestamps are stored as milliseconds since
 * Unix epoch (January 1, 1970 UTC).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

	/**
	 * Unique session identifier (UUID as string).
	 */
	private String sessionId;

	/**
	 * Session expiry time in milliseconds since Unix epoch. Sessions expire 1 hour
	 * after creation.
	 */
	private Long expiryTime;
}
