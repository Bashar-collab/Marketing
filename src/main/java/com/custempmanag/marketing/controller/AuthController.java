package com.custempmanag.marketing.controller;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.model.RefreshToken;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.request.ChangePasswordRequest;
import com.custempmanag.marketing.request.LoginRequest;
import com.custempmanag.marketing.request.RefreshTokenRequest;
import com.custempmanag.marketing.request.RegisterRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.response.RefreshTokenResponse;
import com.custempmanag.marketing.service.AuthService;
import com.custempmanag.marketing.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow all origins for all methods in this controller
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        MessageResponse messageResponse = authService.registerUser2(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        MessageResponse messageResponse = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

//    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestHeader("Authorization") String authHeader,
                                                  @AuthenticationPrincipal UserPrinciple userPrinciple) {
        MessageResponse messageResponse = authService.logout(authHeader, userPrinciple.getId());
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

//    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                            @AuthenticationPrincipal UserPrinciple currentUser) {
        // Business logic (e.g., verify current password, update password)
        MessageResponse messageResponse = authService.changePassword(request, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenResponse response = refreshTokenService.getRefreshToken(refreshTokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
