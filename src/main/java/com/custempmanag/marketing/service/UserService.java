package com.custempmanag.marketing.service;

import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Permission;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale()))); // I SHOULD RETURN TO HERE

    }

    @Cacheable(value = "userByIdCache", key = "#userId")
    public User validateAndGetUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Cacheable(value = "userRoleCache", key = "#username")
    public String getUserRoles(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + username));
        return user.getRole().getName();
    }

    @Cacheable(value = "userPermissionsCache", key = "#username")
    public Set<Permission> getUserPermissions(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + username));
        return user.getRole().getPermissions();
    }

    @Cacheable(value = "userWithRoleAndPermissionsCache", key = "#username")
    public Optional<User> getRoleAndPermissions(String username)
    {
        return userRepository.findByUsernameWithRolesAndPermissions(username);
    }
}
