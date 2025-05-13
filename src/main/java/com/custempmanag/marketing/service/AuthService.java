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
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
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

    @Autowired
    private MessageSource messageSource;

    @Transactional
    public MessageResponse registerUser(RegisterRequest registerRequest)
    {
        logger.info("Received registration request for user: {}", registerRequest.getUsername());

        if(userRepository.existsByUsername(registerRequest.getUsername()))
        {
            logger.info("Username is already in use, try another one");

            throw new
                    CustomException(messageSource.getMessage("username.register.exists", null, LocaleContextHolder.getLocale()));
        }

        if(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber()))
        {
            logger.info("Phone number is already in use, try another one");
            throw new
                    CustomException(messageSource.getMessage("phone-number.register.exists", null, LocaleContextHolder.getLocale()));
        }
        User user = modelMapper.map(registerRequest, User.class);
        // Saving user's credentials
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        logger.debug("User object created: {}", user);

        logger.info("User's profile {}", user.getProfileType());
        Role role = roleRepository.findByName(user.getProfileType().toUpperCase())
                .orElseThrow(()-> new CustomException(messageSource.getMessage("role.not.found", null, LocaleContextHolder.getLocale())));

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
        String localizedMessage = messageSource.getMessage("user.register.success", null, LocaleContextHolder.getLocale());
        return new MessageResponse(HttpStatus.CREATED.toString(), localizedMessage, user.getUsername());
    }

    public MessageResponse authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale()))); // I SHOULD RETURN TO HERE
        // Debug point

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(
                    messageSource.getMessage("auth.password.invalid", null, LocaleContextHolder.getLocale())); // Debug point
        }
        // THE ERROR IS IN THE LINE BELOW, DON'T FORGET TO DO IT TOMORROW
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        Set<Permission> permissions = user.getRole().getPermissions();

//        return new MessageResponse(HttpStatus.OK.toString(), "User logged in successfully!",
//                jwtConfig.generateToken(username, keyPair));
        String localizedMessage = messageSource.getMessage("auth.login.success", null, LocaleContextHolder.getLocale());
        return new MessageResponse(HttpStatus.OK.toString(), localizedMessage,
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

        return new MessageResponse(HttpStatus.OK.toString(),
                messageSource.getMessage("user.logout.success", null, LocaleContextHolder.getLocale()),
                null);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }

    public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest, UserPrinciple currentUser) {
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new
                        ResourceNotFoundException(messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())));
        if(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword()))
        {
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);
            return new MessageResponse(HttpStatus.OK.toString(),
                    messageSource.getMessage("user.password-change.success", null, LocaleContextHolder.getLocale()),
                    null);
        }
        throw new
                CustomException(messageSource.getMessage("auth.password.invalid", null, LocaleContextHolder.getLocale()));

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
