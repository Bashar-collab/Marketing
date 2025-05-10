package com.custempmanag.marketing.service;

import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Permission;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User validateAndGetUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public String getUserRoles(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + username));
        return user.getRole().getName();
    }

    public Set<Permission> getUserPermissions(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + username));
        return user.getRole().getPermissions();
    }

    public Optional<User> getRoleAndPermissions(String username)
    {
        return userRepository.findByUsernameWithRolesAndPermissions(username);
    }
}
