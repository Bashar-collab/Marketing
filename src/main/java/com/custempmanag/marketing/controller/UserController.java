package com.custempmanag.marketing.controller;

import com.custempmanag.marketing.request.UserRequest;
import com.custempmanag.marketing.response.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> getAllUsers() {
        MessageResponse messageResponse = userService.getAllUsers();
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/users/{userId}")
//    @PreAuthorize("hasRole('OWNER') or hasRole('CUSTOMER')")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId,
                                            @AuthenticationPrincipal UserPrinciple currentUser) {
        // Allow users to view their own details or admins to view any user's details
        if (!currentUser.getId().equals(userId) && !currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN") || a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        User user = userService.validateAndGetUserById(userId);
        MessageResponse messageResponse = userService.getUserDetails(userId, currentUser);
        return ResponseEntity.ok(messageResponse);
//        return ResponseEntity.ok("nice");
    }


    @GetMapping("/admin/users/{userId}")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
//    @PreAuthorize("isAuthenticated()")
    public MessageResponse getAdminUserDetails(@PathVariable Long userId) {
        return userService.getAdminUserDetails(userId);
    }

    @PutMapping("/users/{userId}")
//     @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserDetails(@PathVariable Long userId,
                                                          @Valid @RequestBody UserRequest updatedUser,
                                                          @AuthenticationPrincipal UserPrinciple currentUser) {
//        // Allow users to update their own details or admins to update any user's details
        if (!currentUser.getId().equals(userId) && currentUser.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN") || a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MessageResponse response = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok("response");
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long profileId) {
//        UserResponse response = userService.getProfileDetails(profileId).getBody();
        return ResponseEntity.ok("response");
    }
} 