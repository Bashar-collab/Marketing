package com.custempmanag.marketing.service;

import com.custempmanag.marketing.exception.CustomException;
import com.custempmanag.marketing.model.Role;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.CustomerRepository;
import com.custempmanag.marketing.repository.OwnerRepository;
import com.custempmanag.marketing.repository.UserRepository;
import com.custempmanag.marketing.request.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private OwnerRepository ownerRepository;
    @MockBean
    private CustomerRepository customerRepository;

    @Test
    void updateUser_invalid_role_throws_exception() {
        // Arrange
        Long userId = 1L;
        UserRequest updatedUser = new UserRequest();
        updatedUser.setEmail("new@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("username");
        Role role = new Role();
        role.setName("INVALID_ROLE");
        existingUser.setRole(role);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.updateUser(userId, updatedUser);
        });
        assertThat(exception.getMessage()).isEqualTo("Invalid role name: INVALID_ROLE");
        verify(userRepository).save(existingUser);
        verify(ownerRepository, never()).save(any());
        verify(customerRepository, never()).save(any());
    }
}