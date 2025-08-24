package com.main.app.config;

import org.springframework.stereotype.Component;

@Component
public class BootId {
    private final String id = java.util.UUID.randomUUID().toString();
    public String get() { return id; }
}
