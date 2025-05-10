package com.custempmanag.marketing.service;

import com.custempmanag.marketing.EntityResolver.ProfileResolver.Factory.ProfileResolverFactory;
import com.custempmanag.marketing.config.JwtConfig;
import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.exception.CustomException;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Permission;
import com.custempmanag.marketing.model.Role;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.RoleRepository;
import com.custempmanag.marketing.repository.UserRepository;
import com.custempmanag.marketing.request.ChangePasswordRequest;
import com.custempmanag.marketing.request.RegisterRequest;
import com.custempmanag.marketing.response.LoginResponse;
import com.custempmanag.marketing.response.MessageResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.nio.file.attribute.UserPrincipal;
import java.security.KeyPair;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RoleRepository roleRepository;


    public AuthService(UserRepository userRepository, JwtConfig jwtConfig,
                       PasswordEncoder passwordEncoder,@Lazy AuthenticationManager authenticationManager,
                       TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.jwtConfig = jwtConfig;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenBlacklistService = tokenBlacklistService;
    }
//    @Autowired
//    private FCMService fcmService;

    @Autowired
    private KeyPair keyPair;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private ProfileResolverFactory profileResolverFactory;

    @Transactional
    public MessageResponse registerUser(RegisterRequest registerRequest)
    {
        logger.info("Received registration request for user: {}", registerRequest.getUsername());

        if(userRepository.existsByUsername(registerRequest.getUsername()))
        {
            logger.info("Username is already in use, try another one");
            throw new CustomException("Username is already in use");
        }

        if(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber()))
        {
            logger.info("Phone number is already in use, try another one");
            throw new CustomException("Phone number is already in use");
        }
        User user = modelMapper.map(registerRequest, User.class);
        // Saving user's credentials
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        logger.debug("User object created: {}", user);

        logger.info("User's profile {}", user.getProfileType());
        Role role = roleRepository.findByName(user.getProfileType().toUpperCase())
                .orElseThrow(()-> new CustomException("Role not found"));
        user.setRole(role);
        // Create the appropriate profile based on the selected type
        Long profileId = profileResolverFactory.createProfile(user);

        logger.info(String.valueOf(profileId));
        // Set the profileId in Users entity
        user.setProfileId(profileId);
        logger.info("User's email is {}", user.getEmail());
        // Save the user
        userRepository.save(user);
        logger.info("User registered successfully with ID: {}", user.getId());
        return new MessageResponse(HttpStatus.CREATED.toString(), "User registered successfully!", user.getUsername());
    }

    public MessageResponse authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")); // Debug point

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException("Invalid password"); // Debug point
        }
        // THE ERROR IS IN THE LINE BELOW, DON'T FORGET TO DO IT TOMORROW
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        Set<Permission> permissions = user.getRole().getPermissions();

//        return new MessageResponse(HttpStatus.OK.toString(), "User logged in successfully!",
//                jwtConfig.generateToken(username, keyPair));
        return new MessageResponse(HttpStatus.OK.toString(), "User logged in successfully!",
                    new LoginResponse(jwtConfig.generateToken(username, keyPair),
                            user.getRole().getName(), user.getRole().getPermissions()
                            .stream().map(permission -> permission.getCode())
                            .collect(Collectors.toSet())));
    }

    public MessageResponse logout(String authHeader) {
        String token = extractToken(authHeader);

        // Calculate remaining time until token expiration
        long remainingTime = jwtConfig.getRemainingTime(token, keyPair);

        // Add token to blacklist with TTL equal to remaining validity
        tokenBlacklistService.blacklistToken(token, remainingTime);

        // Clear the security context
        SecurityContextHolder.clearContext();

        return new MessageResponse(HttpStatus.OK.toString(), "User logged out successfully", null);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }

    public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest, UserPrinciple currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword()))
        {
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);
            return new MessageResponse(HttpStatus.OK.toString(), "Password changed successfully", null);
        }
        throw new CustomException("Invalid password");

    }
    /*
    public void updateFCMToken(Long userId, String fcmToken) {
        logger.info("Updating FCM token for user ID: {}", userId);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> UserExceptionFactory.createException(
                        ExceptionType.USER_NOT_FOUND,
                        "User not found"

                ));

        logger.info("Validating new FCM token for user ID: {}", userId);
        if (fcmService.isValidFCMToken(fcmToken)) {
            user.setFcmToken(fcmToken);
            userRepository.save(user);
            logger.info("FCM token updated successfully for user ID: {}", userId);

        } else {
            logger.error("Invalid FCM token provided for user ID: {}", userId);
            throw UserExceptionFactory.createException(
                    ExceptionType.INVALID_FCM_TOKEN,
                    "Invalid FCM token"
            );
        }
    }

     */
}
