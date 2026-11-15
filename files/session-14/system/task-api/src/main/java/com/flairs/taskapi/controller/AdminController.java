package com.flairs.taskapi.controller;

import com.flairs.taskapi.auth.UserResponse;
import com.flairs.taskapi.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Two locks on the same door, to show both styles:
//  - URL rule in SecurityConfig: /api/admin/** needs ROLE_ADMIN
//  - @PreAuthorize on the method: checked again right before the method runs
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin")
public class AdminController {

    private final UserRepository users;

    public AdminController(UserRepository users) {
        this.users = users;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    @GetMapping("/users")
    public List<UserResponse> users() {
        return users.findAll(Sort.by("id")).stream().map(UserResponse::of).toList();
    }
}
