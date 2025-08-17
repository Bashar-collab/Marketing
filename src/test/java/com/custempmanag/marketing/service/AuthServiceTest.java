package com.custempmanag.marketing.service;

import com.custempmanag.marketing.EntityResolver.ProfileResolver.Factory.ProfileResolverFactory;
import com.custempmanag.marketing.config.JwtConfig;
import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.exception.CustomException;
import com.custempmanag.marketing.model.*;
import com.custempmanag.marketing.repository.OwnerRepository;
import com.custempmanag.marketing.repository.RoleRepository;
import com.custempmanag.marketing.repository.UserRepository;
import com.custempmanag.marketing.request.ChangePasswordRequest;
import com.custempmanag.marketing.request.RegisterRequest;
import com.custempmanag.marketing.response.LoginResponse;
import com.custempmanag.marketing.response.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.KeyPair;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private ProfileResolverFactory profileResolverFactory;

    @Mock
    private MessageSource messageSource;

    @Mock
    private UserService userService;

    @Mock
    private KeyPair keyPair;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUser2_owner_success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("username");
        request.setPassword("password");
        request.setPhoneNumber("0982119939");
        request.setProfileType("owner");

        Role role = new Role();
        role.setName("OWNER");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("username");
        savedUser.setPassword("encodedPassword");
        savedUser.setPhoneNumber("0982119939");
        savedUser.setRole(role);

        when(userRepository.existsByUsername("username")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("0982119939")).thenReturn(false);
        when(roleRepository.findByName("OWNER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(ownerRepository.save(any(Owner.class))).thenReturn(new Owner());
        when(messageSource.getMessage("user.register.success", null, LocaleContextHolder.getLocale()))
                .thenReturn("User registered successfully");

        // Act
        MessageResponse response = authService.registerUser2(request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.toString());
        assertThat(response.getMessage()).isEqualTo("User registered successfully");
        assertThat(response.getData()).isEqualTo("username");
        verify(userRepository).save(any(User.class));
        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    void registerUser2_username_exists_throws_exception() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");
        request.setProfileType("owner");

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);
        when(messageSource.getMessage("username.register.exists", null, LocaleContextHolder.getLocale()))
                .thenReturn("Username already exists");

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> authService.registerUser2(request));
        assertThat(exception.getMessage()).isEqualTo("Username already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser2_phone_number_exists_throws_exception() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setPhoneNumber("1234567890");
        request.setProfileType("owner");

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("1234567890")).thenReturn(true);
        when(messageSource.getMessage("phone-number.register.exists", null, LocaleContextHolder.getLocale()))
                .thenReturn("Phone number already exists");

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> authService.registerUser2(request));
        assertThat(exception.getMessage()).isEqualTo("Phone number already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser2_invalid_profile_type_throws_exception() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setPhoneNumber("1234567890");
        request.setProfileType("invalid");

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("1234567890")).thenReturn(false);
        when(roleRepository.findByName("INVALID")).thenReturn(Optional.empty());
        when(messageSource.getMessage("role.not.found", null, LocaleContextHolder.getLocale()))
                .thenReturn("Role not found");

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> authService.registerUser2(request));
        assertThat(exception.getMessage()).isEqualTo("Role not found");
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate_success() {
        // Arrange
        String username = "testUser";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");
        Role role = new Role();
        role.setName("CUSTOMER");
        user.setRole(role);
        Permission permission = new Permission();
        permission.setCode("PER");
        role.setPermissions(Set.of(permission));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refreshToken");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(jwtConfig.generateToken(eq(username), any())).thenReturn("jwtToken");
        when(refreshTokenService.createRefreshToken(username)).thenReturn(refreshToken);
        when(messageSource.getMessage("auth.login.success", null, LocaleContextHolder.getLocale()))
                .thenReturn("Login successful");

        // Act
        MessageResponse response = authService.authenticate(username, password);

        // Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.toString());
        assertThat(response.getMessage()).isEqualTo("Login successful");
        assertThat(response.getData()).isInstanceOf(LoginResponse.class);
        LoginResponse loginResponse = (LoginResponse) response.getData();
        assertThat(loginResponse.getAccessToken()).isEqualTo("jwtToken");
        assertThat(loginResponse.getRefreshToken()).isEqualTo("refreshToken");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_invalid_username_throws_exception() {
        // Arrange
        String username = "nonExistentUser";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(messageSource.getMessage("auth.password.invalid", null, LocaleContextHolder.getLocale()))
                .thenReturn("Invalid username or password");

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> authService.authenticate(username, password));
        assertThat(exception.getMessage()).isEqualTo("Invalid username or password");
        verify(passwordEncoder).matches(eq(password), anyString());
    }

    @Test
    void authenticate_invalid_password_throws_exception() {
        // Arrange
        String username = "testUser";
        String password = "wrongPassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);
        when(messageSource.getMessage("auth.password.invalid", null, LocaleContextHolder.getLocale()))
                .thenReturn("Invalid username or password");

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> authService.authenticate(username, password));
        assertThat(exception.getMessage()).isEqualTo("Invalid username or password");
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void logout_success() {
        // Arrange
        String authHeader = "Bearer jwtToken";
        Long userId = 1L;
        String token = "jwtToken";

//        User user = new User();
//        user.setId(userId);
//        user.setUsername("testUser");
//        user.setPassword("encodedPassword");
//        user.setProfileId(1L);
//        Role role = new Role();
//        role.setName("CUSTOMER");
//        user.setRole(role);
//
//        UserPrinciple userPrinciple = UserPrinciple.create(user);

        when(jwtConfig.extractToken(authHeader)).thenReturn(token);
        when(jwtConfig.getRemainingTime(eq(token), any())).thenReturn(3600L);
        when(messageSource.getMessage("user.logout.success", null, LocaleContextHolder.getLocale()))
                .thenReturn("Logout successful");

        // Act
        MessageResponse response = authService.logout(authHeader, userId);

        // Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.toString());
        assertThat(response.getMessage()).isEqualTo("Logout successful");
        assertThat(response.getData()).isNull();
        verify(tokenBlacklistService).blacklistToken(token, 3600L);
        verify(refreshTokenService).revokeToken(userId);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void changePassword_success() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewPassword("newPassword");

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        Role role = new Role();
        role.setName("CUSTOMER");
        user.setRole(role);

        UserPrinciple userPrinciple = UserPrinciple.create(user);

        when(userService.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(messageSource.getMessage("user.password-change.success", null, LocaleContextHolder.getLocale()))
                .thenReturn("Password changed successfully");

        // Act
        MessageResponse response = authService.changePassword(request, userPrinciple);

        // Assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.toString());
        assertThat(response.getMessage()).isEqualTo("Password changed successfully");
        assertThat(response.getData()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_invalid_current_password_throws_exception() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword");

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        Role role = new Role();
        role.setName("CUSTOMER");
        user.setRole(role);

        UserPrinciple userPrinciple = UserPrinciple.create(user);

        when(userService.findByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);
        when(messageSource.getMessage("auth.password.invalid", null, LocaleContextHolder.getLocale()))
                .thenReturn("Invalid current password");

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> authService.changePassword(request, userPrinciple));
        assertThat(exception.getMessage()).isEqualTo("Invalid current password");
        verify(userRepository, never()).save(any());
    }
    @Test
    void authenticate() {
    }

    @Test
    void logout() {
    }

    @Test
    void changePassword() {
    }
}