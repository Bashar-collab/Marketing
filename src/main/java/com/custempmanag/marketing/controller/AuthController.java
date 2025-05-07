package com.custempmanag.marketing.controller;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.request.ChangePasswordRequest;
import com.custempmanag.marketing.request.LoginRequest;
import com.custempmanag.marketing.request.RegisterRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        MessageResponse messageResponse = authService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        MessageResponse messageResponse = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestHeader("Authorization") String authHeader) {
        MessageResponse messageResponse = authService.logout(authHeader);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                            @AuthenticationPrincipal UserPrinciple currentUser) {
        // Business logic (e.g., verify current password, update password)
        MessageResponse messageResponse = authService.changePassword(request, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }
    /*
        NEED TO DESIGN API'S FOR THIS IN THE FUTURE
        Get Users:
            GET /api/users: Retrieves all the users (requires authentication)
        Get User Details:
            GET /api/users/{userId}: Retrieves details of a specific user (requires authentication).
        Update User Details:
            PUT /api/users/{userId}: Updates details of a specific user (requires authentication).
     */
}
