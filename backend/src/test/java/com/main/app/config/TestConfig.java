package com.main.app.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public SecurityContext securityContext() {
        return mock(SecurityContext.class);
    }

    @Bean
    public SecurityContextHolder securityContextHolder() {
        return new SecurityContextHolder();
    }
}