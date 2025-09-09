package com.main.app.dto.User;
import com.main.app.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String userName;
    private String userEmail;
    private Role hasRole;
    private Boolean isBlocked;

    public UserDto() {}
}
