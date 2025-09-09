package com.main.app.controller;

import com.main.app.dto.User.UpdateUsernameDTO;
import com.main.app.model.User.User;
import com.main.app.service.User.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/username")
    public ResponseEntity<User> updateUsername(@RequestBody @Valid UpdateUsernameDTO updateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User updatedUser = userService.updateUsername(currentUserEmail, updateDTO.getNewUsername());
        return ResponseEntity.ok(updatedUser);
    }
}