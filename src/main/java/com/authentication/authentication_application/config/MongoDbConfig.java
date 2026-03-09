package com.authentication.authentication_application.config;

import com.authentication.authentication_application.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.Index;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MongoDB configuration for setting up indexes.
 * Ensures optimal query performance and data integrity by creating indexes on frequently queried fields.
 */
@Configuration
@RequiredArgsConstructor
public class MongoDbConfig {

    private static final Logger logger = LoggerFactory.getLogger(MongoDbConfig.class);
    private final MongoTemplate mongoTemplate;

    /**
     * Initializes MongoDB indexes for the User collection.
     * Called automatically after the bean is created.
     * Creates indexes on:
     * - email: Unique index for fast user lookups and duplicate prevention
     * - profileId: Unique index for profile-based queries
     * - createdAt: Index for sorting recent accounts
     */
    @PostConstruct
    @SuppressWarnings("deprecation")
    public void initializeIndexes() {
        try {
            IndexOperations indexOps = mongoTemplate.indexOps(User.class);

            // Create unique index on email field
            indexOps.ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
            logger.info("Created unique index on email field");

            // Create unique index on profileId field
            indexOps.ensureIndex(new Index().on("profileId", Sort.Direction.ASC).unique());
            logger.info("Created unique index on profileId field");

            // Create index on createdAt for sorting recent accounts
            indexOps.ensureIndex(new Index().on("createdAt", Sort.Direction.DESC));
            logger.info("Created index on createdAt field");

            logger.info("MongoDB indexes initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize MongoDB indexes", e);
        }
    }
}

