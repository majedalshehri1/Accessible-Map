package com.main.app.controller;

import com.main.app.config.SecurityUtils;
import com.main.app.dto.User.UpdateUsernameDTO;
import com.main.app.model.User.User;
import com.main.app.service.User.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/username")
    public ResponseEntity<Map<String,Object>> updateUsername(@RequestBody @Valid UpdateUsernameDTO dto) {
        var me = SecurityUtils.currentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        var updated = userService.updateUsernameById(me.id(), dto.getNewUsername());

        return ResponseEntity.ok(Map.of(
                "id", updated.getUserId(),
                "username", updated.getUserName()
        ));
    }
}