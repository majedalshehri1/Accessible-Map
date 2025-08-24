package com.main.app.dto;
import com.main.app.Enum.AccessibillityType;
import com.main.app.Enum.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private String userName;
    private String userEmail;
    private String hasRole;
    private boolean isBlocked;

}
