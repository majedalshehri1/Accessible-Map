package com.main.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUsernameDTO {
    @NotBlank(message = "Username is required")
    @Size(max = 255, message = "Username must be less than 255 characters")
    private String newUsername;
}