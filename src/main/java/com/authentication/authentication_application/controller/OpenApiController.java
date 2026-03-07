package com.authentication.authentication_application.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to serve the OpenAPI YAML specification file.
 * This allows Swagger UI to load the static openapi.yml file.
 */
@RestController
public class OpenApiController {

    @GetMapping(value = "/openapi.yml", produces = "application/yaml")
    public Resource openapi() {
        return new ClassPathResource("openapi.yml");
    }
}

