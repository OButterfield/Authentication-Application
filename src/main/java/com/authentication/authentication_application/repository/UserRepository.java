package com.authentication.authentication_application.repository;

import com.authentication.authentication_application.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User document persistence in MongoDB. Provides CRUD
 * operations and custom query methods. Extends MongoRepository for automatic
 * implementation by Spring Data MongoDB.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

	/**
	 * Finds a user by email address. Email is indexed in MongoDB for fast lookups.
	 *
	 * @param email
	 *            the user's email address
	 * @return Optional containing the user if found, empty otherwise
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Finds a user by their profile ID. Profile ID is unique and indexed in
	 * MongoDB.
	 *
	 * @param profileId
	 *            the user's unique profile identifier
	 * @return Optional containing the user if found, empty otherwise
	 */
	Optional<User> findByProfileId(String profileId);

	/**
	 * Checks if a user with the given email already exists. Useful for preventing
	 * duplicate account creation.
	 *
	 * @param email
	 *            the user's email address
	 * @return true if a user with this email exists, false otherwise
	 */
	boolean existsByEmail(String email);

	/**
	 * Checks if a user with the given profile ID already exists.
	 *
	 * @param profileId
	 *            the user's profile identifier
	 * @return true if a user with this profile ID exists, false otherwise
	 */
	boolean existsByProfileId(String profileId);

	/**
	 * Deletes a user by email address. Used primarily for test cleanup.
	 *
	 * @param email
	 *            the user's email address
	 */
	void deleteByEmail(String email);
}
