package com.main.app.config;

import com.main.app.Enum.Role;

import java.io.Serializable;

public record AuthUser(Long id, String name , String email , Role role){}