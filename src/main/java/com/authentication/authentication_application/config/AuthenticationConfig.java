package com.authentication.authentication_application.config;

import com.authentication.authentication_application.util.HashUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Authentication configuration for the application.
 * Loads pepper from environment variables and provides HashUtil bean.
 */
@Configuration
public class AuthenticationConfig {

    /**
     * Creates a HashUtil bean with the pepper from configuration.
     *
     * @param pepper the pepper value from app.security.pepper configuration
     * @return configured HashUtil instance
     */
    @Bean
    public HashUtil hashUtil(@Value("${app.security.pepper}") String pepper) {
        return new HashUtil(pepper);
    }
}


