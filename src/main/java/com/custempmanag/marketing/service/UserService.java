package com.custempmanag.marketing.service;

import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User validateAndGetUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
