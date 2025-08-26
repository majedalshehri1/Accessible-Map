package com.main.app.dto;
import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import com.main.app.Enum.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private Role hasRole;
    private Boolean isBlocked;

}
