package com.authentication.authentication_application.config;

import com.authentication.authentication_application.util.HashUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for AuthenticationConfig. Verifies that Spring
 * configuration properly loads pepper and creates HashUtil bean.
 */
@SpringBootTest
@TestPropertySource(properties = "app.security.pepper=integration-test-pepper-secret")
@DisplayName("AuthenticationConfig Integration Tests")
class AuthenticationConfigIntegrationTest {

	@Autowired
	private HashUtil hashUtil;

	@Test
	@DisplayName("Should create HashUtil bean from Spring configuration")
	void shouldCreateHashUtilBeanFromSpringConfiguration() {
		// GIVEN & WHEN & THEN
		assertNotNull(hashUtil, "HashUtil bean should be created by Spring");
	}
}
